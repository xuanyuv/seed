package com.jadyer.seed.comm.annotation.qss;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * QSS任务注册
 * ----------------------------------------------------------------------------------------------------
 * 注意：注册的任务必须支持POST请求
 * 注意：该注解目前只提供首次注册的功能（更新的话，会存在每启动时，注解的配置与管理员修改了的配置，优先级的取舍）
 * ----------------------------------------------------------------------------------------------------
 * 使用举例
 * @RestController
 * public class DemoController {
 *     @PostMapping("/prop")
 *     @SeedQSSReg(qssHost="${qss.host}", appHost="${qss.appHost}", appname="seedboot", name="打印系统属性", cron="0 *／2 * * * ?")
 *     public CommResult<Map<String, Object>> prop(){
 *         Map<String, Object> map = new HashMap<>();
 *         map.put("hello", "Hello QSS.");
 *         return CommResult.success(map);
 *     }
 * }
 * ----------------------------------------------------------------------------------------------------
 * 本注解的所有属性传参，均支持两种方式：
 * 1、从配置文件读值：@SeedQSSReg(qssHost="${jadyer.qss.host}")
 * 2、直接写具体的值：@SeedQSSReg(qssHost="http://192.168.2.34:8008")
 * ----------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2018/6/12 16:46.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SeedQSSReg {
    /**
     * QSS系统地址
     * 注意：该地址可以为QSS负载均衡的地址，也可以是QSS某一个节点的地址
     * 传值：尾部斜线可加可不加，比如“http://192.168.2.34:8008”和“http://192.168.2.34:8008/”都可以
     */
    String qssHost();

    /**
     * 定时任务的应用系统地址
     * 传值：尾部斜线可加可不加，比如“https://192.168.2.36/boot”和“https://192.168.2.36/boot/”都可以
     */
    String appHost();

    /**
     * 定时任务的应用名称
     */
    String appname();

    /**
     * 定时任务名称
     */
    String name();

    /**
     * 定时任务执行的CronExpression
     * 例如：表达式“0 0/10 9-23 * * ?”表示每天09:00-23:00之间每10分钟触发一次
     */
    String cron();
}