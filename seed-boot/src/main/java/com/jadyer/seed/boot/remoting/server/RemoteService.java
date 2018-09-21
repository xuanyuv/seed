package com.jadyer.seed.boot.remoting.server;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务端用来标注所输出的SpringHttpInvoker服务的注解
 * Created by 玄玉<https://jadyer.cn/> on 2016/11/21 18:22.
 */
@Inherited
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RemoteService {
    /**
     * 默认值，用于定义服务端所输出的serviceInterface（比如：IFSService.class）
     * <p>
     *     其与serviceInterface二者必传其一，同时传则以value为准
     * </p>
     */
    Class<?> value();

    /**
     * 用于定义服务端所输出的serviceInterface（比如：IFSService.class）
     */
    Class<?> serviceInterface() default Class.class;

    /**
     * 自定义服务端输出的服务名（若未传此值，则默认取serviceInterface的不含包路径的类名，比如：IFSService）
     */
    String name() default "";

    /**
     * 自定义服务端输出的服务路径前缀（最终拼出来的路径就是："/" + path + "/" + name）
     * <p>
     *     可以传入诸如这四种模式的值：remoting，remoting/，/remoting，/remoting/
     * </p>
     */
    String path() default "/remoting";
}