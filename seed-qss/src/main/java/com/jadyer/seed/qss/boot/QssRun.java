package com.jadyer.seed.qss.boot;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.boot.BootRunHelper;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.helper.JobSubscriber;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/3/4 18:39.
 */
@SpringBootApplication(scanBasePackages="com.jadyer.seed")
public class QssRun extends BootRunHelper {
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


    public static void main(String[] args) {
        BootRunHelper.run(args, QssRun.class);
    }
}