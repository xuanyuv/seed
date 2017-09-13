package com.jadyer.seed.seedoc.boot;

import org.beetl.core.resource.ClasspathResourceLoader;
import org.beetl.ext.spring.BeetlGroupUtilConfiguration;
import org.beetl.ext.spring.BeetlSpringViewResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * --------------------------------------------------------
 * 关于idea中修改templates之后自动刷新，详见
 * http://bbs.ibeetl.com/bbs/bbs/topic/612-1.html
 * http://bbs.ibeetl.com/bbs/bbs/topic/507-1.html
 * 玄玉补充：Run运行即可，不需要debug运行，且按Ctrl+F9就行
 * --------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2017/9/13 9:38.
 */
@Configuration
public class BeetlConfiguration {
    @Bean(initMethod="init")
    public BeetlGroupUtilConfiguration getBeetlGroupUtilConfiguration() {
        BeetlGroupUtilConfiguration beetlGroupUtilConfiguration = new BeetlGroupUtilConfiguration();
        beetlGroupUtilConfiguration.setResourceLoader(new ClasspathResourceLoader("templates"));
        return beetlGroupUtilConfiguration;
    }

    @Bean
    public BeetlSpringViewResolver getBeetlSpringViewResolver(BeetlGroupUtilConfiguration beetlGroupUtilConfiguration) {
        BeetlSpringViewResolver beetlSpringViewResolver = new BeetlSpringViewResolver();
        beetlSpringViewResolver.setContentType("text/html; charset=UTF-8");
        beetlSpringViewResolver.setViewNames("**/*.html");
        beetlSpringViewResolver.setOrder(0);
        beetlSpringViewResolver.setConfig(beetlGroupUtilConfiguration);
        return beetlSpringViewResolver;
    }
}