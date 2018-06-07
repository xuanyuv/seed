package com.jadyer.seed.boot.lock.cluster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Seed分布式锁（采用Redisson实现的RedLock算法）
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/5 9:53.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeedLock {
    /**
     * 锁的资源
     * --------------------------------------------------------------------------------
     * 支持EL表达式：比如下面的两种加锁，是分别针对入参id，和UserMsg的name属性
     * @SeedLock("#id")
     * public CommResult<Map<String, Object>> prop(int id, UserMsg userMsg){}
     * @SeedLock("#userMsg.name")
     * public CommResult<Map<String, Object>> prop(int id, UserMsg userMsg){}
     * --------------------------------------------------------------------------------
     */
    String value() default "";

    /**
     * 锁等待时间
     */
    int waitTime() default -1;

    /**
     * 锁释放时间
     */
    int leaseTime() default -1;

    /**
     * 时间单位，默认：秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;
}