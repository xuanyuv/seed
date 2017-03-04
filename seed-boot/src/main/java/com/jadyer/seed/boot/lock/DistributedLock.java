package com.jadyer.seed.boot.lock;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/7/8 17:04.
 */
public interface DistributedLock {
    /**
     * 加锁
     * <p>加锁失败的话，该方法不会重试再去加锁，若需要重试，可以调用lock(long waitTimeMillis);</p>
     * @return true if lock success, false if not
     */
    boolean lock();


    /**
     * 加锁
     * @param waitTimeMillis 合计等待的毫秒数，获取锁失败时会等待一小会儿再去获取锁，尝试时间直至超过该参数
     * @return true if lock success, false if not
     */
    boolean lock(long waitTimeMillis);


    /**
     * 解锁
     * @return true if unlock success, false if not
     */
    boolean unLock();
}