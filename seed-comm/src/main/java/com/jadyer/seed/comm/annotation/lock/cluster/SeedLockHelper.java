package com.jadyer.seed.comm.annotation.lock.cluster;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.StringUtils;
import org.redisson.RedissonRedLock;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * --------------------------------------------------------------------------------------------------------------
 * try {
 *     if(SeedLockHelper.lock(SeedLockConfiguration.redissonClientList, "资源key", "资源应用名称")){
 *         //业务处理-->start...
 *         HTTPUtil.post("the url", "the params");
 *         //业务处理-->end.....
 *     }
 * } finally {
 *     SeedLockHelper.unlock();
 * }
 * --------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/7/17 16:44.
 */
public class SeedLockHelper {
    private static final String LOCK_PREFIX = "SeedLockHelper:";
    private static ThreadLocal<String> keyMap = new ThreadLocal<>();
    private static ThreadLocal<RedissonRedLock> redLockMap = new ThreadLocal<>();

    public static boolean lock(List<RedissonClient> redissonClientList, String key){
        return lock(redissonClientList, key, null, -1, TimeUnit.SECONDS);
    }


    public static boolean lock(List<RedissonClient> redissonClientList, String key, String appname){
        return lock(redissonClientList, key, appname, -1, TimeUnit.SECONDS);
    }


    public static boolean lock(List<RedissonClient> redissonClientList, String key, String appname, long waitTime, TimeUnit unit){
        key = LOCK_PREFIX + (StringUtils.isBlank(appname)?"":appname+":") + key;
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
        } catch (Throwable t) {
            LogUtil.getLogger().error("资源[{}]加锁-->失败：{}", key, JadyerUtil.extractStackTraceCausedBy(t), t);
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