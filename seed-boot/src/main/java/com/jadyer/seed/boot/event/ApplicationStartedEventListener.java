package com.jadyer.seed.boot.event;

import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;

/**
 * SpringBoot在启动过程中支持的监听事件：ApplicationStartedEvent
 * -----------------------------------------------------------------
 * 它是SpringBoot启动开始时执行的事件
 * -----------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/12/08 17:54.
 */
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartedEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        System.out.println("SpringBoot开始启动-->" + event.getSpringApplication());
    }
}