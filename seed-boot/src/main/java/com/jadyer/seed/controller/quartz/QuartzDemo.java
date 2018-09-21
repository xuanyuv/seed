package com.jadyer.seed.controller.quartz;

import com.jadyer.seed.comm.util.JadyerUtil;
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
    //@Scheduled(cron="${spring.quartz.cron.demo}")
    void justDoIt(){
        LogUtil.getLogger().info("定时任务：xxxx-->启动--------------------------------------------------->");
        List<String> dataList = Arrays.asList("1", "2", "3", "4", "5", "6");
        for(int i=0, len=dataList.size(); i<len; i++){
            String idx = JadyerUtil.leftPadUseZero(i+"", (len+"").length());
            LogUtil.getLogger().info("定时任务：xxxx-->[{}-{}]-->開始处理，读到数据={}", len, idx, ReflectionToStringBuilder.toString(dataList.get(i)));
            LogUtil.getLogger().info("读到数据：{}", dataList.get(i));
            LogUtil.getLogger().info("定时任务：xxxx-->[{}-{}]-->处理完毕", len, idx);
        }
        LogUtil.getLogger().info("定时任务：xxxx-->结束<---------------------------------------------------");
    }
}