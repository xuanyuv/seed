package com.jadyer.seed.comm;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2018/7/17 16:44.
 */
public class SeedLockHelper {
    private static final String PREFIX = "seedLockHelper:";
    private static ThreadLocal<String> keyMap = new ThreadLocal<>();
    private static ThreadLocal<RedissonRedLock> redLockMap = new ThreadLocal<>();

    public static boolean lock(List<RedissonClient> redissonClientList, String key){
        return lock(redissonClientList, key, -1, TimeUnit.SECONDS);
    }


    public static boolean lock(List<RedissonClient> redissonClientList, String key, long waitTime, TimeUnit unit){
        key = PREFIX + key;
        RedissonRedLock redLock = redLockMap.get();
        if(null != redLock){
            LogUtil.getLogger().error("资源[{}]加锁-->失败：上一次未解锁", key);
            return false;
        }
        //加锁
        RLock[] rLocks = new RLock[redissonClientList.size()];
        for(int i=0; i<redissonClientList.size(); i++){
            rLocks[i] = redissonClientList.get(i).getLock(key);
        }
        try {
            //new RedissonRedLock(rLocks)可能发生异常：比如应用正在启动中，就来调用这里
            //Caused by: java.lang.IllegalArgumentException: Lock objects are not defined
            redLock = new RedissonRedLock(rLocks);
            if(!redLock.tryLock(waitTime, unit)){
                LogUtil.getLogger().error("资源[{}]加锁-->失败", key);
                return false;
            }
        } catch (InterruptedException | IllegalArgumentException e) {
            LogUtil.getLogger().error("资源[{}]加锁-->失败：{}", key, JadyerUtil.extractStackTraceCausedBy(e));
            return false;
        }
        keyMap.set(key);
        redLockMap.set(redLock);
        LogUtil.getLogger().info("资源[{}]加锁-->成功", key);
        return true;
    }


    public static void unlock(){
        RedissonRedLock redLock = redLockMap.get();
        if(null != redLock){
            redLock.unlock();
            LogUtil.getLogger().info("资源[{}]解锁-->完毕", keyMap.get());
        }
        keyMap.remove();
        redLockMap.remove();
    }
}