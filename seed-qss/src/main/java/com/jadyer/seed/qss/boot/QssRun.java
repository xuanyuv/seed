package com.jadyer.seed.qss.boot;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.helper.JobSubscriber;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/3/4 18:39.
 */
@EntityScan(basePackages="com.jadyer.seed")
@EnableJpaRepositories(basePackages="com.jadyer.seed")
@SpringBootApplication(scanBasePackages="com.jadyer.seed")
public class QssRun {
    private static final Logger log = LoggerFactory.getLogger(QssRun.class);
    @Resource
    private JedisPool jedisPool;
    @Resource
    private JobSubscriber jobSubscriber;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    @PostConstruct
    public void scheduleReport(){
        Executors.newScheduledThreadPool(1).schedule(new Runnable(){
            @Override
            public void run() {
                try (Jedis jedis = jedisPool.getResource()) {
                    LogUtil.getLogger().info("异步订阅：jedis.subscribe");
                    jedis.subscribe(jobSubscriber, SeedConstants.CHANNEL_SUBSCRIBER);
                }
            }
        }, 3, TimeUnit.SECONDS);
        LogUtil.getLogger().info("同步所有任务到内存：begin...");
        try {
            TimeUnit.SECONDS.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(ScheduleTask task : scheduleTaskRepository.findAll(Condition.<ScheduleTask>and().eq("status", SeedConstants.QSS_STATUS_RUNNING))){
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(SeedConstants.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
            }
        }
        try {
            TimeUnit.SECONDS.sleep(15);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtil.getLogger().info("同步所有任务到内存：end.....");
    }


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
}