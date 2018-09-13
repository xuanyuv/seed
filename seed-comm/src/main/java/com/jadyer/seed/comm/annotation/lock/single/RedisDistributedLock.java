package com.jadyer.seed.comm.annotation.lock.single;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisCluster;

import java.util.concurrent.TimeUnit;

/**
 * 仿照java.util.concurrent.locks.Lock用法编写的Redis实现的分布式锁
 * <code>
 *     本锁使用举例如下<br>
 *     (at)Service
 *     class RouterService{
 *         (at)Resource
 *         private JedisCluster jedisCluster;
 *         private DistributedLock lock;
 *         (at)PostConstruct
 *         public void initRedisDistributedLock(){
 *             this.lock = new RedisDistributedLock(jedisCluster, "demo-boot-open-router", 50000);
 *         }
 *         CommResult loanGet(ReqData reqData){
 *             if(!lock.lock()){
 *                 return CommResult.fail(OpenCodeEnum.SIGN_ERROR.getCode(), "获取锁失败");
 *             }
 *             //do something.
 *             lock.unLock()
 *             return CommResult.success();
 *         }
 *     }
 * </code>
 * Created by 玄玉<http://jadyer.cn/> on 2016/7/8 18:58.
 */
public class RedisDistributedLock implements DistributedLock {
    private JedisCluster jedisCluster;
    private String key;          //加锁key
    private long expiresAt = 0;  //当前锁失效时间
    private long holdTimeMillis; //锁持有时间

    /**
     * @param jedisCluster   操作Redis3.0集群的对象
     * @param key            RedisCluster全局唯一的key名称
     * @param holdTimeMillis 锁持有时间
     */
    public RedisDistributedLock(JedisCluster jedisCluster, String key, long holdTimeMillis) {
        if(holdTimeMillis <= 0){
            throw new IllegalArgumentException("Argument holdTimeMillis must > 0.");
        }
        this.jedisCluster = jedisCluster;
        this.key = key;
        this.holdTimeMillis = holdTimeMillis;
    }


    @Override
    public boolean lock() {
        return this.lock(0);
    }


    @Override
    public boolean lock(long waitTimeMillis) {
        System.out.println("to while");
        while(waitTimeMillis >= 0){
            long expiresAt = System.currentTimeMillis() + this.holdTimeMillis + 1;
            /**
             * 先尝试获取锁
             */
            //setnx=SET if Not eXists
            if(this.jedisCluster.setnx(this.key, String.valueOf(expiresAt)) == 1){
                this.expiresAt = expiresAt;
                return true;
            }
            /**
             * 获取锁失败表示已有人持锁，那再看该锁有没有过期（防止持锁人中途宕机或忘记解锁），过期就再获取锁
             */
            //获取到该锁的过期时间
            String expectedExpiryTime = this.jedisCluster.get(this.key);
            //该锁已过期则获取锁，未过期则作罢（暂未处理setnx到get期间持锁人unlock的情况）
            if(StringUtils.isNotBlank(expectedExpiryTime) && System.currentTimeMillis()>Long.parseLong(expectedExpiryTime)){
                //锁过期则获取锁，并得到本次获取锁之前的该锁最新原过期时间
                String actualExpiryTime = this.jedisCluster.getSet(this.key, String.valueOf(expiresAt));
                //两次获取的锁的原过期时间相同，说明我们获取锁期间，没有别人获取锁，则获取锁成功
                //这里的equals()比较重要，起码看上去场景也比较复杂
                //但注意一般this.key相同，说明是同一个业务，同一个业务的expiresAt也是相同的
                //所以这里getSet之前若有人捷足先登，致使我们覆盖了人家的锁时间，这种极致的情景，暂时忽略
                if(expectedExpiryTime.equals(actualExpiryTime)){
                    this.expiresAt = expiresAt;
                    return true;
                }
            }
            /**
             * 获取锁失败的话，提供尝试机制，合计尝试的时间取决于传入的合计等待时间
             */
            //注意减100ms的操作不能放到if里面，否则会导致waitTimeMillis传0时，while()不停循环到持锁成功为止
            waitTimeMillis -= 100;
            if(waitTimeMillis > 0){
                try {
                    //等效Thread.sleep(100)
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    //do nothing.
                }
            }
        }
        return false;
    }


    @Override
    public boolean unLock() {
        //过期时间相等才删除（约等于限制只有原持锁人才能解锁）
        if(String.valueOf(this.expiresAt).equals(this.jedisCluster.get(this.key))){
            this.jedisCluster.del(key);
            this.expiresAt = 0;
            return true;
        }
        return false;
    }
}