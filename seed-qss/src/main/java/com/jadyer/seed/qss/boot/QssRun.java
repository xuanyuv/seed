package com.jadyer.seed.qss.boot;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.helper.JobSubscriber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import redis.clients.jedis.JedisCluster;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/4 18:39.
 */
@EntityScan(basePackages="${scan.base.packages}")
@EnableJpaRepositories(basePackages="${scan.base.packages}")
@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class QssRun {
    private static final Logger log = LoggerFactory.getLogger(QssRun.class);

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
        new SpringApplicationBuilder().sources(QssRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
    }


    public static final String CHANNEL_SUBSCRIBER = "qss_jedis_pubsub_channel";
    @Resource
    private JedisCluster jedisCluster;
    @Resource
    private JobSubscriber jobSubscriber;
    @PostConstruct
    public void scheduleReport(){
        Executors.newScheduledThreadPool(1).schedule(new Runnable(){
            @Override
            public void run() {
                LogUtil.getLogger().info("JedisSubscribe：开始注册...");
                jedisCluster.subscribe(jobSubscriber, CHANNEL_SUBSCRIBER);
                LogUtil.getLogger().info("JedisSubscribe：注册完毕...");
            }
        }, 10, TimeUnit.SECONDS);
    }
}