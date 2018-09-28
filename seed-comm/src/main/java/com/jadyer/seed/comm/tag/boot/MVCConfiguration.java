package com.jadyer.seed.comm.tag.boot;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 * --------------------------------------------------------------------------------------------------------------
 * JSP文件必须放在/src/main/webapp/目录下
 * 因为/src/main/resources/下的任何一个位置在打包war时都会被编译到/WEB-INF/classes/下面，这个目录下的JSP文件是不会被JavaEE容器识别的
 * --------------------------------------------------------------------------------------------------------------
 * SpringBoot1.x 到 2.x 中关于embeded container的配置改动
 * 1.x的相关类如下：
 * org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer
 * org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer
 * org.springframework.boot.context.embedded.tomcat.TomcatConnectorCustomizer
 * org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory
 * 2.x的相关类如下：
 * org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory
 * org.springframework.boot.web.server.WebServerFactoryCustomizer
 * org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer
 * org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory
 * --------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2016/11/24 10:45.
 */
//@Configuration
public class MVCConfiguration implements WebMvcConfigurer, ErrorPageRegistrar {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        //registry.addViewController("/apidoc").setViewName("forward:/apidoc/index.jsp");
        //registry.addViewController("/apidoc/").setViewName("forward:/apidoc/index.jsp");
        registry.addViewController("/").setViewName("forward:/login.jsp");
        registry.addViewController("/login").setViewName("forward:/login.jsp");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //registry.addResourceHandler("/apidoc/**").addResourceLocations("/apidoc/");
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        InternalResourceViewResolver irvr = new InternalResourceViewResolver();
        irvr.setPrefix("/WEB-INF/jsp/");
        irvr.setSuffix(".jsp");
        irvr.setViewClass(JstlView.class);
        registry.viewResolver(irvr);
    }

    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        ErrorPage[] errorPages = new ErrorPage[2];
        errorPages[0] = new ErrorPage(HttpStatus.NOT_FOUND, "/WEB-INF/jsp/common/404.jsp");
        errorPages[1] = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/WEB-INF/jsp/common/500.jsp");
        registry.addErrorPages(errorPages);
    }
}