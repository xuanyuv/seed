package com.jadyer.seed.controller.quartz;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/8 9:00.
 */
@Component
@EnableScheduling
public class QuartzDemo {
    @Scheduled(cron="${spring.quartz.cron.demo}")
    void justDoIt(){
        System.out.println("quartz-demo-" + System.currentTimeMillis());
    }
}