package com.jadyer.seed.comm.annotation.lock.cluster;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Seed分布式锁（采用Redisson实现的RedLock算法）
 * ---------------------------------------------------------------------------------------------------
 * 注：被注解方法不能private，其余三种均可
 * ---------------------------------------------------------------------------------------------------
 * 1、@SpringBootApplication(scanBasePackages="com.jadyer.seed")
 * 2、org.redisson:redisson:3.7.1
 * 3、redisson:
 *      lockWatchdogTimeout: 10000
 *      connectionMinimumIdleSize: 16
 *      connectionPoolSize: 32
 *      connectTimeout: 3000
 *      password: xuanyu
 *      nodes:
 *        - redis://192.168.2.210:7000
 *        - redis://192.168.2.210:7001
 *        - redis://192.168.2.210:7002
 *        - redis://192.168.2.210:7003
 *        - redis://192.168.2.210:7004
 *        - redis://192.168.2.210:7005
 * 4、@SeedLock(key="#userMsg.name", appname="seedboot")
 *    public CommResult<Map<String, Object>> prop(int id, UserMsg userMsg){ // do business... }
 * ---------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/5 9:53.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeedLock {
    /**
     * 锁的资源
     * --------------------------------------------------------------------------------
     * 支持SPEL：比如下面的两种加锁，是分别针对入参id，和UserMsg的name属性的
     * @SeedLock(key="#id")
     * @SeedLock(key="#userMsg.name")
     * public CommResult<Map<String, Object>> prop(int id, UserMsg userMsg){}
     * --------------------------------------------------------------------------------
     * 如果想在key值中加入字符串，可以像下面这样写
     * key="'seedboot:' + #userMsg.name"
     * key="'seedboot:'.concat(#userMsg.name)"
     * --------------------------------------------------------------------------------
     * 注意：该值不支持从配置文件读取，因为还没遇到这种场景需求
     * --------------------------------------------------------------------------------
     */
    String key() default "";

    /**
     * 锁的资源所属的应用名称
     * --------------------------------------------------------------------------------
     * 其与key()组成唯一锁标志，可避免多个系统连接相同Redis并使用同一个key()来加锁造成业务冲突
     * 支持从配置文件取值，举例：appname="${spring.application.name}"
     * --------------------------------------------------------------------------------
     */
    String appname() default "";

    /**
     * 锁等待时间
     */
    long waitTime() default -1;

    /**
     * 锁释放时间
     */
    long leaseTime() default -1;

    /**
     * 时间单位，默认：秒
     */
    TimeUnit unit() default TimeUnit.SECONDS;

    /**
     * 加锁失败时回调的方法名
     * --------------------------------------------------------------------------------
     * 未传值则不回调，传值时其方法参数应与加锁的方法参数完全相同（方法名和返回值可不同）
     * --------------------------------------------------------------------------------
     */
    String fallbackMethod() default "";
}