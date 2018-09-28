package com.jadyer.seed.test;

import com.jadyer.seed.boot.BootStrap;
import com.jadyer.seed.boot.event.ApplicationEnvironmentPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationFailedEventListener;
import com.jadyer.seed.boot.event.ApplicationPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationStartingEventListener;
import com.jadyer.seed.comm.constant.SeedConstants;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class BootStartup {
    public static void main(String[] args) {
        //logback-boot.xml中根据环境变量配置日志是否输出到控制台时，使用此配置
        System.setProperty(SeedConstants.BOOT_ACTIVE_NAME, SeedConstants.BOOT_ACTIVE_DEFAULT_VALUE);
        //SpringApplication.run(BootStarp.class, args);
        //new SpringApplicationBuilder().sources(BootStrap.class).profiles("local").run(args);
        new SpringApplicationBuilder().sources(BootStrap.class)
                .listeners(new ApplicationStartingEventListener())
                .listeners(new ApplicationEnvironmentPreparedEventListener())
                .listeners(new ApplicationPreparedEventListener())
                .listeners(new ApplicationFailedEventListener())
                .profiles("local")
                .run(args);
    }
}