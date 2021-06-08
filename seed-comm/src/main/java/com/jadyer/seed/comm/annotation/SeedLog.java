package com.jadyer.seed.comm.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 日志记录
 * Created by 玄玉<https://jadyer.cn/> on 2017/6/20 16:53.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeedLog {
    /**
     * 日志描述
     */
    String value() default "";

    /**
     * 日志动作（默认为其它）
     */
    ActionEnum action() default ActionEnum.OTHER;
}