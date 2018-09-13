package com.jadyer.seed.comm.annotation.lock.single;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * <p>
 *     用法：将其当成工具类或Helper类，直接调用对应方法即可
 * </p>
 * Created by 玄玉<http://jadyer.cn/> on 2016/11/19 15:02.
 */
//@Component
public class RedisDistributedLockV2 {
    @Resource
    private JedisCluster jedisCluster;

    /**
     * 解锁
     * @param key 加锁key
     */
    public void unLock(String key) {
        this.jedisCluster.del(key);
    }


    /**
     * 加锁（失败后不会重试）
     * @param key            加锁key
     * @param holdTimeMillis 锁持有时间
     * @return true if lock success, false if not
     */
    public boolean lock(String key, long holdTimeMillis) {
        return this.lock(key, holdTimeMillis, 0);
    }


    /**
     * 加锁（失败后会重试）
     * @param key            加锁key
     * @param holdTimeMillis 锁持有时间
     * @param waitTimeMillis 合计等待的毫秒数，获取锁失败时会等待一小会儿再去获取锁，尝试时间直至超过该参数
     * @return true if lock success, false if not
     */
    public boolean lock(String key, long holdTimeMillis, long waitTimeMillis) {
        if(holdTimeMillis <= 0){
            throw new IllegalArgumentException("Argument holdTimeMillis must > 0.");
        }
        if(waitTimeMillis < 0){
            throw new IllegalArgumentException("Argument waitTimeMillis must >= 0.");
        }
        while(waitTimeMillis >= 0){
            //当前锁失效时间
            long expiresAt = System.currentTimeMillis() + holdTimeMillis + 3;
            /**
             * 先尝试获取锁
             */
            //setnx=SET if Not eXists
            if(this.jedisCluster.setnx(key, String.valueOf(expiresAt)) == 1){
                return true;
            }
            /**
             * 获取锁失败表示已有人持锁，那再看该锁有没有过期（防止持锁人中途宕机或忘记解锁），过期就再获取锁
             */
            //获取到该锁的过期时间
            String expectedExpiryTime = this.jedisCluster.get(key);
            //该锁已过期则获取锁，未过期则作罢（暂未处理setnx到get期间持锁人unlock的情况）
            if(StringUtils.isNotBlank(expectedExpiryTime) && System.currentTimeMillis()>Long.parseLong(expectedExpiryTime)){
                //锁过期则获取锁，并得到本次获取锁之前的该锁最新原过期时间
                String actualExpiryTime = this.jedisCluster.getSet(key, String.valueOf(expiresAt));
                //两次获取的锁的原过期时间相同，说明我们获取锁期间，没有别人获取锁，则获取锁成功
                //这里的equals()比较重要，起码看上去场景也比较复杂
                //但注意一般this.key相同，说明是同一个业务，同一个业务的expiresAt也是相同的
                //所以这里getSet之前若有人捷足先登，致使我们覆盖了人家的锁时间，这种极致的情景，暂时忽略
                if(expectedExpiryTime.equals(actualExpiryTime)){
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
}