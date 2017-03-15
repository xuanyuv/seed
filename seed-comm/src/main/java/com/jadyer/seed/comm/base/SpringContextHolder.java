package com.jadyer.seed.comm.base;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * ApplicationContext持有器
 * <p>
 *     使用方式为：SpringContextHolder.getBean("beanName")
 * </p>
 * Created by 玄玉<https://jadyer.github.io/> on 2015/2/27 10:01.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	private static ApplicationContext applicationContext;

	/**
	 * 从静态变量applicationContext中取得Bean，自动转型为所赋值对象的类型
	 */
	public static <T> T getBean(String beanName){
		assertContextInjected();
		return (T)applicationContext.getBean(beanName);
	}

	/**
	 * 从静态变量applicationContext中取得Bean，自动转型为所赋值对象的类型
	 */
	public static <T> T getBean(Class<T> requiredType){
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public static ApplicationContext getApplicationContext() {
		assertContextInjected();
		return applicationContext;
	}

	/**
	 * 实现ApplicationContextAware接口，将ApplicationContext注入到静态变量中
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if(null != SpringContextHolder.applicationContext){
			System.out.println("SpringContextHolder中的ApplicationContext将被覆盖，原ApplicationContext为：" + SpringContextHolder.applicationContext);
		}
		SpringContextHolder.applicationContext = applicationContext;
	}

	/**
	 * 实现DisposableBean接口，在Context关闭时清理静态变量
	 */
	@Override
	public void destroy() throws Exception {
		SpringContextHolder.clearHolder();
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	private static void clearHolder(){
		LogUtil.getLogger().warn("清除SpringContextHolder中的ApplicationContext：{}", applicationContext);
		applicationContext = null;
	}

	/**
	 * 检查ApplicationContext不为空
	 */
	private static void assertContextInjected(){
		Validate.validState(null!=applicationContext, "ApplicaitonContext属性未注入，请定义SpringContextHolder...");
	}
}