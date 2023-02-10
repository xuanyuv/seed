package com.jadyer.seed.comm.boot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsFilterConfiguration {
    private CorsConfiguration corsConfig() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        // 设置访问源地址
        corsConfig.addAllowedOrigin(CorsConfiguration.ALL);
        // // SpringBoot-2.4.0开始，要像下面这样配置
        // corsConfig.addAllowedOriginPattern(CorsConfiguration.ALL);
        // 设置访问源请求方法
        corsConfig.addAllowedMethod(CorsConfiguration.ALL);
        // 设置访问源请求头
        corsConfig.addAllowedHeader(CorsConfiguration.ALL);
        // // 暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
        // // corsConfig.addExposedHeader(CorsConfiguration.ALL);
        // corsConfig.addExposedHeader("Content-Type");
        // corsConfig.addExposedHeader( "X-Requested-With");
        // corsConfig.addExposedHeader("accept");
        // corsConfig.addExposedHeader("Origin");
        // corsConfig.addExposedHeader( "Access-Control-Request-Method");
        // corsConfig.addExposedHeader("Access-Control-Request-Headers");
        // 是否允许请求带有验证信息
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);
        return corsConfig;
    }


    @Bean
    @Order(1)
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对接口配置跨域设置
        source.registerCorsConfiguration("/**", this.corsConfig());
        return new CorsFilter(source);
    }
}