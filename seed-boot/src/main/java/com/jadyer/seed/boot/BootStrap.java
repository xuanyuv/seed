package com.jadyer.seed.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class BootStrap extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //这里的配置与com.jadyer.seed.boot.BootRun.java中的配置是独立的没有关系的
        //return builder.sources(getClass())
        //        .listeners(new ApplicationStartingEventListener())
        //        .listeners(new ApplicationEnvironmentPreparedEventListener())
        //        .listeners(new ApplicationPreparedEventListener())
        //        .listeners(new ApplicationFailedEventListener());
        //此处未设置.profiles()，那么打成的war包在Tomcat中启动时，会自动读取Tomcat启动变量中的spring.profiles.active
        return builder.sources(getClass());
    }
}