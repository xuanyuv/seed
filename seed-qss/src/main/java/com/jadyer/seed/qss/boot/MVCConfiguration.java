package com.jadyer.seed.qss.boot;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
public class MVCConfiguration extends WebMvcConfigurerAdapter {
	//@Bean
	//public EmbeddedServletContainerCustomizer containerCustomizer(){
	//	return new EmbeddedServletContainerCustomizer() {
	//		@Override
	//		public void customize(ConfigurableEmbeddedServletContainer container) {
	//			container.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND, "/WEB-INF/jsp/common/404.jsp"));
	//			container.addErrorPages(new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/WEB-INF/jsp/common/500.jsp"));
	//		}
	//	};
	//}
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("forward:/qss/list");
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		InternalResourceViewResolver irvr = new InternalResourceViewResolver();
		irvr.setPrefix("/WEB-INF/jsp/");
		irvr.setSuffix(".jsp");
		irvr.setViewClass(JstlView.class);
		registry.viewResolver(irvr);
	}
}