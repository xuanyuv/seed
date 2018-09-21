package com.jadyer.seed.boot.event;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

/**
 * SpringBoot在启动过程中支持的监听事件：ApplicationEnvironmentPreparedEvent
 * --------------------------------------------------------------------------------
 * 它表示SpringBoot对应Enviroment已经准备完毕，但此时上下文Context还没有创建
 * 该监听中获取到ConfigurableEnvironment后可以操作配置信息，比如修改或增加配置信息
 * --------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2015/12/08 18:56.
 */
public class ApplicationEnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment envi = event.getEnvironment();
        MutablePropertySources mps = envi.getPropertySources();
        if(null != mps){
            for (PropertySource<?> ps : mps) {
                System.out.println("SpringBoot对应Enviroment已经准备完毕，但此时上下文Context还没有创建，得到PropertySource-->" + ps);
            }
        }
    }
}