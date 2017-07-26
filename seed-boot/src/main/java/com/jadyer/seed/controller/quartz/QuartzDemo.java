package com.jadyer.seed.controller.quartz;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/8 9:00.
 */
@Component
@EnableScheduling
public class QuartzDemo {
    @Scheduled(cron="${spring.quartz.cron.demo}")
    void justDoIt(){
        System.out.println("quartz-demo-" + System.currentTimeMillis());
        List<String> dataList = Arrays.asList("1", "2", "3", "4", "5", "6");
        String currIndex = "1";
        int len = dataList.size();
        LogUtil.getLogger().info("定时任务：xxxx-->查到记录[{}]条", len);
        for(String obj : dataList){
            currIndex = JadyerUtil.leftPadUseZero(currIndex, String.valueOf(len).length());
            LogUtil.getLogger().info("读取到数据-->[{}]", obj);
            LogUtil.getLogger().info("定时任务：xxxx-->处理完毕[{}-{}]条", len, currIndex);
            currIndex = String.valueOf(Integer.parseInt(currIndex) + 1);
        }
    }
}