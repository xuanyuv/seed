package com.jadyer.seed.comm.util;

import java.sql.Timestamp;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 高并发场景下System.currentTimeMillis()的性能问题的优化
 * -----------------------------------------------------------------------------------------------
 * -----------------------------------------------------------------------------------------------
 * System.currentTimeMillis()的调用比new一个普通对象要耗时的多（据称100倍左右），因为它要和系统打一次交道
 * 于是编写此类，它在后台定时更新时钟，JVM退出时，线程自动回收
 * -----------------------------------------------------------------------------------------------
 * @version v1.0
 * @history v1.0-->新建
 * -----------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/10/10 12:34.
 */
public enum SystemClockUtil {
    INSTANCE;

    private final AtomicLong now;

    SystemClockUtil(){
        this.now = new AtomicLong(System.currentTimeMillis());
        this.scheduleClockUpdating();
    }

    private void scheduleClockUpdating() {
        //守护线程
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "thread-daemon-system-clock-util");
            thread.setDaemon(true);
            return thread;
        });
        // 1 秒后首次执行，之后每 1 秒均自动执行一次
        scheduler.scheduleAtFixedRate(() -> now.set(System.currentTimeMillis()), 1, 1, TimeUnit.MILLISECONDS);
    }


    /**
     * @return 1507610640351
     */
    public long now(){
        return now.get();
    }


    /**
     * @return 2017-10-10 12:44:00.351
     */
    public String nowDate(){
        return new Timestamp(this.now()).toString();
    }
}