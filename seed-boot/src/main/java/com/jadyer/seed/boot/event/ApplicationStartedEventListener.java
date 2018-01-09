package com.jadyer.seed.boot.event;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

/**
 * SpringBoot在启动过程中支持的监听事件：ApplicationStartedEvent
 * -----------------------------------------------------------------
 * 它是SpringBoot启动开始时执行的事件
 * -----------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2015/12/08 17:54.
 */
public class ApplicationStartedEventListener implements ApplicationListener<ApplicationStartingEvent> {
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        System.out.println("SpringBoot开始启动-->" + event.getSpringApplication());
    }
}