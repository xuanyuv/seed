package com.jadyer.seed.admin.boot;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * -----------------------------------------------------------------------------------------------------------
 * 集成SpringBootAdmin之服务端配置
 * 1. 引入三个包：spring-boot-admin-starter-server、spring-boot-admin-server-ui、spring-boot-starter-web
 * 2. 直接启动，访问：http://127.0.0.1:8080/即可
 * 集成SpringBootAdmin之客户端配置
 * 1. 引入三个包：spring-boot-admin-starter-client、spring-boot-starter-actuator、spring-boot-starter-web
 * 2. 增加以下配置后，启动应用即可
 * management:
 *   endpoint:
 *     health:
 *       show-details: ALWAYS
 *   endpoints:
 *     web:
 *       exposure:
 *         include: '*'
 * spring:
 *   application:
 *     name: SeedBoot
 *   boot:
 *     admin:
 *       client:
 *         # 指定SpringBootAdmin的服务端URL
 *         url: http://127.0.0.1:8080
 *         # 接入SpringBootAdmin的客户端URL（通常用于服务端和客户端不在同一个服务器的情况）
 *         instance:
 *           service-url: http://127.0.0.1
 * -----------------------------------------------------------------------------------------------------------
 * 服务端安全性设置（增加登录页）
 * 1. 引入spring-boot-starter-security
 * 2. 配置spring.security.user.name='admin'/password='admin'
 * 3. 配置SecurityConfiguration（此时访问服务端，就会显示登录页）
 * 4. 同时，客户端的配置中，也要指定服务端用户名/密码，否则连接不上
 *    spring.boot.admin.client.username='admin'/password='admin'
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/11/15 18:10.
 */
@EnableAdminServer
@SpringBootApplication(scanBasePackages="com.jadyer.seed", exclude={DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class, HibernateJpaAutoConfiguration.class})
public class AdminRun {
    public static void main(String[] args) {
        SpringApplication.run(AdminRun.class, args);
    }
}