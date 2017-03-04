package com.jadyer.seed.boot.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import redis.clients.jedis.JedisCluster;

import javax.annotation.Resource;

/**
 * 由于Spring-Session暂不支持Redis3.0，故写此配置实现通过Redis集中管理HttpSession
 * 这里拦截的是[/*]，对于使用者而言是透明的，就像使用普通的HttpSession一样像以往那样使用就行
 * Created by 玄玉<https://jadyer.github.io/> on 2016/6/19 18:15.
 */
//@Configuration
public class SessionConfiguration {
    /**
     * SessionID储存到Cookie里面时的key完整名称
     */
    @Value("${session.key:mysessionid}")
    private String sessionKey;
    /**
     * Session过期时间，小于或等于零则表示永久有效，单位：秒
     */
    @Value("${session.expireSeconds:1800}")
    private int sessionExpireSeconds;
    @Resource
    private JedisCluster jedisCluster;

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public FilterRegistrationBean addRedisSessionFilter(){
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        filterRegistrationBean.setFilter(new SessionRedisFilter(new SessionRedisRepository(this.jedisCluster, this.sessionExpireSeconds), this.sessionKey));
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return filterRegistrationBean;
    }
}