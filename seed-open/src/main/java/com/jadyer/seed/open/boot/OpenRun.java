package com.jadyer.seed.open.boot;

import com.jadyer.seed.comm.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.SimpleCommandLinePropertySource;

@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class OpenRun {
    private static final Logger log = LoggerFactory.getLogger(OpenRun.class);

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
        new SpringApplicationBuilder().sources(OpenRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
    }
}