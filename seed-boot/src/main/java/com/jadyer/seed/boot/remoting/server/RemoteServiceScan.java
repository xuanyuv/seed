package com.jadyer.seed.boot.remoting.server;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务端用来标注所输出的SpringHttpInvoker服务类包名
 * Created by 玄玉<http://jadyer.cn/> on 2016/11/21 18:23.
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RemoteServiceScannerRegistrar.class)
public @interface RemoteServiceScan {
    /**
     * 默认值，用于接收扫描的包路径（支持多路径）
     * <p>
     *     其与scanBasePackages二者必传其一，同时传则会取并集
     * </p>
     */
    String[] value() default {};

    /**
     * 扫描的包路径（支持多路径）
     */
    String[] scanBasePackages() default {};

    //Class<?>[] scanBasePackageClasses() default {};
}