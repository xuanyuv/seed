package com.jadyer.seed.open.core;

import org.springframework.stereotype.Component;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * -----------------------------------------------------------------
 * 【注】：本注解暂未使用
 * -----------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/19 19:12.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
@Deprecated
public @interface OpenService {
    //appid属性暂未使用，故注释
    //String appid() default "";
    String value() default "";
}