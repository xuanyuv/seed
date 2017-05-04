package com.jadyer.seed.boot.remoting.client;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 客户端用来初始化服务端Bean
 * <ul>
 *     <li>它被用在客户端</li>
 *     <li>它只能用在Field上/li>
 *     <li>它的处理实现为RemoteClientBeanPostProcessor.java</li>
 * </ul>
 * Created by 玄玉<http://jadyer.cn/> on 2016/11/21 18:22.
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteClient {
    /**
     * 默认值（若传值则只能传服务端接口地址）
     * <p>
     *     其与serviceUrl二者必传其一，同时传则以value为准
     * </p>
     */
    String value();

    /**
     * 服务端接口地址
     */
    String serviceUrl() default "";
}