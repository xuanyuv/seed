package com.jadyer.seed.scs.boot;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.jadyer.seed.comm.constant.SeedConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/3/10 5:27.
 */
//@EntityScan(basePackages="${scan.base.packages}")
//@EnableJpaRepositories(basePackages="${scan.base.packages}")
@SpringBootApplication(scanBasePackages="${scan.base.packages}", exclude={DataSourceAutoConfiguration.class, DruidDataSourceAutoConfigure.class, HibernateJpaAutoConfiguration.class})
public class ScsRun {
    private static final Logger log = LoggerFactory.getLogger(ScsRun.class);

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
        new SpringApplicationBuilder().sources(ScsRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
    }
}