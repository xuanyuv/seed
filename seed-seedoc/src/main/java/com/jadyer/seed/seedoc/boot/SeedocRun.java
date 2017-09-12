package com.jadyer.seed.seedoc.boot;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.jadyer.seed.comm.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/10 5:27.
 */
//@EntityScan(basePackages="${scan.base.packages}")
//@EnableJpaRepositories(basePackages="${scan.base.packages}")
@SpringBootApplication(scanBasePackages="${scan.base.packages}", exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class SeedocRun{
    private static final Logger log = LoggerFactory.getLogger(SeedocRun.class);

    @Bean
    public Filter characterEncodingFilter(){
        return new CharacterEncodingFilter("UTF-8", true);
    }

    private static String getProfile(SimpleCommandLinePropertySource source){
        if(source.containsProperty(Constants.BOOT_ACTIVE_NAME)){
            log.info("读取到spring变量：{}={}", Constants.BOOT_ACTIVE_NAME, source.getProperty(Constants.BOOT_ACTIVE_NAME));
            return source.getProperty(Constants.BOOT_ACTIVE_NAME);
        }
        if(System.getProperties().containsKey(Constants.BOOT_ACTIVE_NAME)){
            log.info("读取到java变量：{}={}", Constants.BOOT_ACTIVE_NAME, System.getProperty(Constants.BOOT_ACTIVE_NAME));
            return System.getProperty(Constants.BOOT_ACTIVE_NAME);
        }
        if(System.getenv().containsKey(Constants.BOOT_ACTIVE_NAME)){
            log.info("读取到系统变量：{}={}", Constants.BOOT_ACTIVE_NAME, System.getenv(Constants.BOOT_ACTIVE_NAME));
            return System.getenv(Constants.BOOT_ACTIVE_NAME);
        }
        log.warn("未读取到{}，默认取环境：{}", Constants.BOOT_ACTIVE_NAME, Constants.BOOT_ACTIVE_DEFAULT_VALUE);
        return Constants.BOOT_ACTIVE_DEFAULT_VALUE;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(SeedocRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
    }
}