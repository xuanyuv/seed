package com.jadyer.seed.open.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class OpenBootRun {
    private static final Logger log = LoggerFactory.getLogger(OpenBootRun.class);
    private static final String ACTIVE_EVN_NAME = "spring.profiles.active";
    private static final String ACTIVE_DEFAULT_VALUE = "local";

    @Bean
    public Filter characterEncodingFilter(){
        return new CharacterEncodingFilter("UTF-8", true);
    }

    private static String getProfile(SimpleCommandLinePropertySource source){
        if(source.containsProperty(ACTIVE_EVN_NAME)){
            log.info("读取到spring变量：{}={}", ACTIVE_EVN_NAME, source.getProperty(ACTIVE_EVN_NAME));
            return source.getProperty(ACTIVE_EVN_NAME);
        }
        if(System.getProperties().containsKey(ACTIVE_EVN_NAME)){
            log.info("读取到java变量：{}={}", ACTIVE_EVN_NAME, System.getProperty(ACTIVE_EVN_NAME));
            return System.getProperty(ACTIVE_EVN_NAME);
        }
        if(System.getenv().containsKey(ACTIVE_EVN_NAME)){
            log.info("读取到系统变量：{}={}", ACTIVE_EVN_NAME, System.getenv(ACTIVE_EVN_NAME));
            return System.getenv(ACTIVE_EVN_NAME);
        }
        log.warn("未读取到{}，默认取环境：{}", ACTIVE_EVN_NAME, ACTIVE_DEFAULT_VALUE);
        return ACTIVE_DEFAULT_VALUE;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(OpenBootRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
    }
}