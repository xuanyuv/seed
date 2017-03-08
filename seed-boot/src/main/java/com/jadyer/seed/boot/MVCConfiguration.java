package com.jadyer.seed.boot;

import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.web.servlet.ErrorPage;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * JSP文件必须放在/src/main/webapp/目录下
 * 因为/src/main/resources/下的任何一个位置在打包war时都会被编译到/WEB-INF/classes/下面，这个目录下的JSP文件是不会被JavaEE容器识别的
 * Created by 玄玉<https://jadyer.github.io/> on 2016/11/24 10:45.
 */
//@Configuration
public class MVCConfiguration extends WebMvcConfigurerAdapter {
	@Bean
	public EmbeddedServletContainerCustomizer containerCustomizer(){
		return new EmbeddedServletContainerCustomizer() {
			@Override
			public void customize(ConfigurableEmbeddedServletContainer container) {
				container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/WEB-INF/jsp/common/404.jsp"));
				container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/WEB-INF/jsp/common/500.jsp"));
			}
		};
	}

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