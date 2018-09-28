package com.jadyer.seed.mpp.boot;

import com.jadyer.seed.comm.constant.SeedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages="com.jadyer.seed")
@EnableJpaRepositories(basePackages="com.jadyer.seed")
@SpringBootApplication(scanBasePackages="com.jadyer.seed")
public class MppRun {
    private static final Logger log = LoggerFactory.getLogger(MppRun.class);

    private static String getProfile(SimpleCommandLinePropertySource source){
        if(source.containsProperty(SeedConstants.BOOT_ACTIVE_NAME)){
            log.info("读取到spring变量：{}={}", SeedConstants.BOOT_ACTIVE_NAME, source.getProperty(SeedConstants.BOOT_ACTIVE_NAME));
            return source.getProperty(SeedConstants.BOOT_ACTIVE_NAME);
        }
        if(System.getProperties().containsKey(SeedConstants.BOOT_ACTIVE_NAME)){
            log.info("读取到java变量：{}={}", SeedConstants.BOOT_ACTIVE_NAME, System.getProperty(SeedConstants.BOOT_ACTIVE_NAME));
            return System.getProperty(SeedConstants.BOOT_ACTIVE_NAME);
        }
        if(System.getenv().containsKey(SeedConstants.BOOT_ACTIVE_NAME)){
            log.info("读取到系统变量：{}={}", SeedConstants.BOOT_ACTIVE_NAME, System.getenv(SeedConstants.BOOT_ACTIVE_NAME));
            return System.getenv(SeedConstants.BOOT_ACTIVE_NAME);
        }
        log.warn("未读取到{}，默认取环境：{}", SeedConstants.BOOT_ACTIVE_NAME, SeedConstants.BOOT_ACTIVE_DEFAULT_VALUE);
        return SeedConstants.BOOT_ACTIVE_DEFAULT_VALUE;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(MppRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
    }
}