package com.jadyer.seed.comm.base;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext持有器
 * <p>
 *     使用方式为：ApplicationContextHolder.getBean("beanName")
 * </p>
 * Created by 玄玉<https://jadyer.github.io/> on 2015/2/27 10:01.
 */
@Component
public class ApplicationContextHolder implements ApplicationContextAware {
	private static ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextHolder.applicationContext = applicationContext;
	}

	public static Object getBean(String beanName){
		if(null == ApplicationContextHolder.applicationContext){
			throw new NullPointerException("applicationContext is null.");
		}
		return applicationContext.getBean(beanName);
	}
}