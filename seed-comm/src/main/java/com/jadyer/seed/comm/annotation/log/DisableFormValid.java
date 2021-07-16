package com.jadyer.seed.comm.annotation.log;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 禁用JSR303自动验证
 * ------------------------------------------------------
 * 优先级：Disable 高于 Enable
 * ------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2021/7/16 9:54.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DisableFormValid {
    String value() default "";
}