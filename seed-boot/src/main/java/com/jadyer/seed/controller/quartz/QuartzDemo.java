package com.jadyer.seed.controller.quartz;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/8 9:00.
 */
@Component
@EnableScheduling
public class QuartzDemo {
    //@Scheduled(cron="${spring.quartz.cron.demo}")
    void justDoIt(){
        System.out.println("quartz-demo-" + System.currentTimeMillis());
        List<String> dataList = Arrays.asList("1", "2", "3", "4", "5", "6");
        String idx = "1";
        int len = dataList.size();
        LogUtil.getLogger().info("定时任务：xxxx-->查到记录[{}]条", len);
        for(String obj : dataList){
            idx = JadyerUtil.leftPadUseZero(idx, String.valueOf(len).length());
            LogUtil.getLogger().info("定时任务：xxxx-->開始处理[{}-{}]条，id={}", len, idx, obj);
            LogUtil.getLogger().info("读到数据：{}", obj);
            LogUtil.getLogger().info("定时任务：xxxx-->处理完毕[{}-{}]条", len, idx);
            idx = String.valueOf(Integer.parseInt(idx) + 1);
        }
    }
}