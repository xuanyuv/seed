package com.jadyer.seed.boot.lock.cluster;

import com.jadyer.seed.boot.RedissonConfiguration;
import com.jadyer.seed.comm.util.LogUtil;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 【待测试】
 * --------------------------------------------------------------------------------------
 * Redisson官方讲解：https://github.com/redisson/redisson/wiki/目录
 * Redisson实例配置：https://github.com/redisson/redisson/wiki/2.-配置方法
 * --------------------------------------------------------------------------------------
 */
public class RedLockUtil {
    /**
     * 加锁
     * @param key       被锁的资源标识
     * @param waitTime  锁等待时间
     * @param leaseTime 锁释放时间
     * @param unit      时间单位
     */
    public static boolean tryLock(String key, int waitTime, int leaseTime, TimeUnit unit){
        List<RLock> rLockList = new ArrayList<>();
        for(RedissonClient client : RedissonConfiguration.getRedissonClientList()){
            rLockList.add(client.getLock(key));
        }
        RedissonRedLock redLock = new RedissonRedLock((RLock[]) rLockList.toArray());
        try {
            return redLock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            LogUtil.getLogger().error("加锁[" + key + "]失败，堆栈轨迹如下", e);
            return false;
        }
    }


    /**
     * 解锁
     */
    public static void unlock(RedissonRedLock redLock){
        redLock.unlock();
    }
}