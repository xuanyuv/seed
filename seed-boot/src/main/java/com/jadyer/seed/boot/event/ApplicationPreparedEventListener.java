package com.jadyer.seed.boot.event;

import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * SpringBoot在启动过程中支持的监听事件：ApplicationPreparedEvent
 * -------------------------------------------------------------------------------
 * 它表示SpringBoot上下文Context创建完成，但此时Spring中的Bean是没有完全加载完成的
 * 该监听器中是无法获取自定义bean并操作的
 * -------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2015/12/08 19:00.
 */
public class ApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent> {
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        ConfigurableApplicationContext cac = event.getApplicationContext();
        System.out.println("SpringBoot上下文Context创建完成，但此时Spring中的Bean是没有完全加载完成的，得到ApplicationContext-->" + cac.getDisplayName());
    }
}