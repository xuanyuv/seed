package com.jadyer.seed.controller.batch;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2021/11/16 17:57.
 */
@Component
public class BatchComponent {
    @Resource
    private JobLauncher jobLauncher;

    /**
     * 启动Spring-Batch任务
     * @param job      任务
     * @param jobDesc  任务描述
     * @param time     随机数字：批量的唯一标志（非断点续跑可直接传null）
     * @param paramMap 其它参数：不会作为批量的唯一标志（无参可传null）
     * Comment by 玄玉<https://jadyer.cn/> on 2019/8/12 18:25.
     */
    public JobExecution runJob(Job job, String jobDesc, String time, Map<String, String> paramMap) {
        //判断是否断点续跑
        boolean isResume = false;
        long timeLong;
        if(StringUtils.isBlank(time)){
            timeLong = SystemClockUtil.INSTANCE.now();
        }else{
            isResume = true;
            timeLong = Long.parseLong(time);
        }
        LogUtil.getLogger().info("{}==>{}：Starting...time={}，paramMap={}", jobDesc, isResume?"：断点续跑":"", timeLong, JSON.toJSONString(paramMap));
        //构造JobParameters
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addLong("time", timeLong);
        if(MapUtils.isNotEmpty(paramMap)){
            for(Map.Entry<String,String> entry : paramMap.entrySet()){
                jobParametersBuilder.addString(entry.getKey(), entry.getValue(), false);
            }
        }
        //执行job
        try{
            JobExecution execution = jobLauncher.run(job, jobParametersBuilder.toJobParameters());
            LogUtil.getLogger().info("{}==>{}：Ending......jobInstance={}", jobDesc, isResume?"：断点续跑":"", execution.getJobInstance());
            return execution;
        }catch(JobInstanceAlreadyCompleteException e){
            throw new SeedException("A job instance already exists and is complete");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}