package com.jadyer.seed.admin.boot;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import de.codecentric.boot.admin.server.config.EnableAdminServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/11/15 18:10.
 */
@EnableAdminServer
@SpringBootApplication(scanBasePackages="com.jadyer.seed", exclude={DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class, HibernateJpaAutoConfiguration.class})
public class AdminRun {
    public static void main(String[] args) {
        SpringApplication.run(AdminRun.class, args);
    }
}