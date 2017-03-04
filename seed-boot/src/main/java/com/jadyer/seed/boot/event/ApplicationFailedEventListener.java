package com.jadyer.seed.boot.event;

import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

/**
 * SpringBoot在启动过程中支持的监听事件：ApplicationFailedEvent
 * -----------------------------------------------------------------------
 * 它是在SpringBoot启动异常时执行的事件
 * -----------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/12/08 19:09.
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {
	@Override
	public void onApplicationEvent(ApplicationFailedEvent event) {
		Throwable cause = event.getException();
		System.out.println("SpringBoot启动异常-->" + cause.getMessage());
	}
}