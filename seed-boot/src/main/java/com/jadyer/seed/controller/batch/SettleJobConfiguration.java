package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * 结算批量任务
 * ----------------------------------------------------------------------
 * https://spring.io/guides/gs/batch-processing/
 * https://docs.spring.io/spring-batch/4.0.x/reference/html/index.html
 * ----------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/8/15 17:00.
 */
@Configuration
@EnableBatchProcessing
public class SettleJobConfiguration {
    @Resource
    private Step step0001;
    @Resource
    private Step step0002;
    @Resource
    private Step step0003;
    @Resource
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job settleJob() {
        return jobBuilderFactory.get("settleJob")
                .incrementer(new RunIdIncrementer())
                .listener(this.jobExecutionListener())
                .flow(step0001)
                .next(step0002)
                .next(step0003)
                .end()
                .build();
    }


    private JobExecutionListener jobExecutionListener() {
        final long[] startTime = new long[1];
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                startTime[0] = SystemClockUtil.INSTANCE.now();
                LogUtil.getLogger().info("ID=[{}]的任务处理开始，JobParameters=[{}]", jobExecution.getJobId(), jobExecution.getJobParameters());
            }
            @Override
            public void afterJob(JobExecution jobExecution) {
                if(jobExecution.getStatus() == BatchStatus.COMPLETED){
                    LogUtil.getLogger().info("ID=[{}]的任务处理结束，耗时[{}]ms", jobExecution.getJobId(), SystemClockUtil.INSTANCE.now()- startTime[0]);
                }
            }
        };
    }
}