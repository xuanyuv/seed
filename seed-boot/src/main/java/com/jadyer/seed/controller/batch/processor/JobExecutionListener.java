package com.jadyer.seed.controller.batch.processor;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Job执行监听器
 * Created by 玄玉<http://jadyer.cn/> on 2017/11/20 16:32.
 */
@Component
public class JobExecutionListener extends JobExecutionListenerSupport {
    private long startTime;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        startTime = SystemClockUtil.INSTANCE.now();
        LogUtil.getLogger().info("ID=[{}]的任务处理开始，JobParameters=[{}]", jobExecution.getJobId(), jobExecution.getJobParameters());
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            LogUtil.getLogger().info("ID=[{}]的任务处理结束，耗时[{}]ms", jobExecution.getJobId(), SystemClockUtil.INSTANCE.now()-startTime);
        }
    }
}