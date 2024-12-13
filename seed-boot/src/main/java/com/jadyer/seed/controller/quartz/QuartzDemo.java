package com.jadyer.seed.controller.quartz;

import com.jadyer.seed.comm.util.ByteUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/8 9:00.
 */
@Component
@EnableScheduling
public class QuartzDemo {
    // @Scheduled(cron="${spring.quartz.cron.demo}")
    // @Scheduled(cron="0 */2 * * * ?")
    void justDoIt(){
        String quartzName = "DEMO";
        LogUtil.getLogger().info("\n定时任务：{}-->启动--------------------------------------------------->", quartzName);
        List<String> dataList = Arrays.asList("1", "2", "3", "4", "5", "6");
        LogUtil.getLogger().info("定时任务：{}-->待处理数据=[{}]条", quartzName, dataList.size());
        for(int i=0,len=dataList.size(); i<len; i++){
            String idx = ByteUtil.leftPadUseZero(i+1+"", (len+"").length());
            LogUtil.getLogger().info("定时任务：{}-->[{}-{}]-->开始处理，读到数据={}", quartzName, len, idx, ReflectionToStringBuilder.toString(dataList.get(i)));
            try {
                System.out.println("do something ...");
                System.out.println("do something ...");
                System.out.println("do something ...");
            } catch (Exception e) {
                LogUtil.getLogger().error("定时任务：{}-->[{}-{}]-->处理时发生异常，本条自动跳过", quartzName, len, idx, e);
            }
            LogUtil.getLogger().info("定时任务：{}-->[{}-{}]-->处理完毕", quartzName, len, idx);
        }
        LogUtil.getLogger().info("定时任务：{}-->结束<---------------------------------------------------", quartzName);
    }
}