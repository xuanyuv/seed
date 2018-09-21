package com.jadyer.seed.open.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/19 19:13.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpenMethod {
    String value() default "";

    /** 等效于value属性 */
    String methodName() default "";
}