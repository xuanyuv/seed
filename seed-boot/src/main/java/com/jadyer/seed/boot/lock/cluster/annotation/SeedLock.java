package com.jadyer.seed.boot.lock.cluster.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Seed分布式锁（采用Redisson实现的RedLock算法）
 * Created by 玄玉<http://jadyer.cn/> on 2018/6/5 9:53.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeedLock {
    /**
     * 锁的资源
     * 与属性key相同的含义和作用，支持SpringEL表达式
     */
    @AliasFor("key")
    String value() default "";

    /**
     * 锁的资源
     * 与属性value相同的含义和作用，支持SpringEL表达式
     */
    @AliasFor("value")
    String key() default "";

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