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
 * --------------------------------------------------------------------------------------------------
 * Spring提供了一些Aware接口，比如BeanFactoryAware、ApplicationContextAware、ResourceLoaderAware、ServletContextAware
 * 实现了这些接口的Bean在被初始化之后，便拥有了取得相关资源的能力
 * 比如实现BeanFactoryAware的Bean在被初始化之后，Spring容器会注入BeanFactory的实例
 * 而实现了ApplicationContextAware的Bean在被初始化之后，会被注入ApplicationContext的实例
 * --------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/2/27 10:01.
 */
@Component
public class SpringContextHolder implements ApplicationContextAware, DisposableBean {
	private static ApplicationContext applicationContext;

	/**
	 * 从静态变量applicationContext中取得Bean，自动转型为所赋值对象的类型
	 */
	public Object getBean(String beanName){
		assertContextInjected();
		return applicationContext.getBean(beanName);
	}

	/**
	 * 从静态变量applicationContext中取得Bean，自动转型为所赋值对象的类型
	 */
	public <T> T getBean(Class<T> requiredType){
		assertContextInjected();
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 取得存储在静态变量中的ApplicationContext.
	 */
	public ApplicationContext getApplicationContext() {
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
		this.clearHolder();
	}

	/**
	 * 清除SpringContextHolder中的ApplicationContext为Null.
	 */
	private void clearHolder(){
		LogUtil.getLogger().warn("清除SpringContextHolder中的ApplicationContext：{}", applicationContext);
		applicationContext = null;
	}

	/**
	 * 检查ApplicationContext不为空
	 */
	private void assertContextInjected(){
		Validate.validState(null!=applicationContext, "ApplicaitonContext属性未注入，请定义SpringContextHolder...");
	}
}