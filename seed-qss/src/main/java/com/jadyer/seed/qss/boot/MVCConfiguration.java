package com.jadyer.seed.qss.boot;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class MVCConfiguration extends WebMvcConfigurerAdapter {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		//registry.addViewController("/apidoc").setViewName("forward:/apidoc/index.jsp");
		//registry.addViewController("/apidoc/").setViewName("forward:/apidoc/index.jsp");
		registry.addViewController("/").setViewName("forward:/building.jsp");
		registry.addViewController("/login").setViewName("forward:/login.jsp");
	}

	//@Override
	//public void addResourceHandlers(ResourceHandlerRegistry registry) {
	//	registry.addResourceHandler("/apidoc/**").addResourceLocations("/apidoc/");
	//}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver irvr = new InternalResourceViewResolver();
		irvr.setPrefix("/WEB-INF/jsp/");
		irvr.setSuffix(".jsp");
		irvr.setViewClass(JstlView.class);
		registry.viewResolver(irvr);
	}
}