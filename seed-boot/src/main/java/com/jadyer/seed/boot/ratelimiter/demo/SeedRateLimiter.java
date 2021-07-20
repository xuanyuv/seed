package com.jadyer.seed.boot.ratelimiter.demo;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 限流
 * ----------------------------------------------------------------------------------------
 * @ResponseBody
 * @GetMapping("/testrl")
 * @SeedRateLimiter(key="testrl", tps=1)
 * CommResult<String> testrl(){
 *     return CommResult.success("你是最棒的");
 * }
 * ----------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2021/7/19 18:56.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeedRateLimiter {
    String key();

    /**
     * 每秒事务数
     */
    long tps();
}