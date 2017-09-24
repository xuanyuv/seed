package com.jadyer.seed.qss.boot;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/6 17:25.
 */
@Configuration
public class QuartzConfiguration {
    private Properties buildQuartzProperties(){
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", "SeedQSSServerScheduler");
        prop.put("org.quartz.threadPool.threadCount", "5");
        return prop;
    }

    @Bean
    public Scheduler getScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory(this.buildQuartzProperties());
        //SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        return scheduler;
    }
}