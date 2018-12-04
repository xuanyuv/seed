package com.jadyer.seed.comm.annotation.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用JSR303自动验证
 * Created by 玄玉<https://jadyer.cn/> on 2018/4/17 13:24.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableFormValid {
    String value() default "";
}