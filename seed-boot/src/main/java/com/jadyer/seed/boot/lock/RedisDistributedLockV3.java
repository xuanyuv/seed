package com.jadyer.seed.boot.lock;

import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * 分布式锁
 * -----------------------------------------------------------------------------------------------------
 * 用法：将其当成工具类或Helper类，直接调用对应方法即可
 * -----------------------------------------------------------------------------------------------------
 * 不推荐使用RedisDistributedLockV2.java的原因如下
 * 1.锁没有标记请求标识，会导致任何客户端都可以解锁
 * 2.锁过期时，若多个客户端getSet()，虽然最终只会有一人持锁成功，但该人的锁的过期时间是有可能被其它人覆盖的
 * 3.并且getSet()的if{}语句块中，是要求各个客户端的时间必须精确同步的（NTPD也不是精确可靠的，更不用说ntpdate了）
 * -----------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2017/11/24 19:36.
 */
//@Component
public class RedisDistributedLockV3 {
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";

    @Resource
    private JedisCluster jedisCluster;

    /**
     * 解锁
     * @param key       锁
     * @param requestId 加锁时的请求标识
     */
    public boolean unLock(String key, String requestId) {
        String luaScript = "if redis.call('get', KEYS[1]) == ARGV[1] then\n" +
                "    return redis.call('del', KEYS[1])\n" +
                "else\n" +
                "    return 0\n" +
                "end";
        return "1".equals(jedisCluster.eval(luaScript, Collections.singletonList(key), Collections.singletonList(requestId)));
    }


    /**
     * 加锁（失败后不会重试）
     * @param key            锁
     * @param requestId      请求标识，用于识别该锁是哪个请求加的，以作为解锁时的依据
     * @param holdTimeMillis 锁持有时间
     * @return true if lock success, false if not
     */
    public boolean lock(String key, String requestId, long holdTimeMillis) {
        return this.lock(key, requestId, holdTimeMillis, 0);
    }


    /**
     * 加锁（失败后会重试）
     * @param key            锁
     * @param requestId      请求标识，用于识别该锁是哪个请求加的，以作为解锁时的依据
     * @param holdTimeMillis 锁持有时间
     * @param waitTimeMillis 合计等待的毫秒数，获取锁失败时会等待一小会儿再去获取锁，尝试时间直至超过该参数
     * @return true if lock success, false if not
     */
    public boolean lock(String key, String requestId, long holdTimeMillis, long waitTimeMillis) {
        if(holdTimeMillis <= 0){
            throw new IllegalArgumentException("Argument holdTimeMillis must > 0.");
        }
        if(waitTimeMillis < 0){
            throw new IllegalArgumentException("Argument waitTimeMillis must >= 0.");
        }
        while(waitTimeMillis >= 0){
            //第三个参数表示setnx=SET if Not eXists，第四个参数表示设置锁的过期时间（时间数为第五个参数）
            if("OK".equals(jedisCluster.set(key, requestId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, holdTimeMillis))){
                return true;
            }
            //获取锁失败的话，提供尝试机制，合计尝试的时间取决于传入的合计等待时间
            //注意减50ms的操作不能放到if里面，否则会导致waitTimeMillis传0时，while()不停循环到持锁成功为止
            waitTimeMillis -= 50;
            if(waitTimeMillis > 0){
                try {
                    //等效Thread.sleep(50)
                    TimeUnit.MILLISECONDS.sleep(50);
                } catch (InterruptedException e) {
                    //do nothing.
                }
            }
        }
        return false;
    }
}