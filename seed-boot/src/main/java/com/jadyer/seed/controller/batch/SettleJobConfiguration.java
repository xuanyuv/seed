package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
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
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                LogUtil.getLogger().info("=======================================================================");
                LogUtil.getLogger().info("批量任务-->[{}-{}]-->開始处理，JobParameters=[{}]", jobExecution.getJobId(), "step0000", jobExecution.getJobParameters());
            }
            @Override
            public void afterJob(JobExecution jobExecution) {
                if(jobExecution.getStatus() == BatchStatus.COMPLETED){
                    LogUtil.getLogger().info("批量任务-->[{}-{}]-->处理完成，TotalDuration[{}]ms", jobExecution.getJobId(), "step0000", SystemClockUtil.INSTANCE.now()-jobExecution.getStartTime().getTime());
                    LogUtil.getLogger().info("=======================================================================");
                }
            }
        };
    }


    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                LogUtil.getLogger().info("-----------------------------------------------------------------------");
                LogUtil.getLogger().info("批量任务-->[{}-{}]-->開始处理", stepExecution.getJobExecutionId(), stepExecution.getStepName());
            }
            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                if(stepExecution.getStatus() == BatchStatus.COMPLETED){
                    LogUtil.getLogger().info("批量任务-->[{}-{}]-->处理完成，ReadCount=[{}]，WriteCount=[{}]，CommitCount==[{}]，Duration[{}]ms", stepExecution.getJobExecutionId(), stepExecution.getStepName(), stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getCommitCount(), SystemClockUtil.INSTANCE.now()-stepExecution.getStartTime().getTime());
                    LogUtil.getLogger().info("-----------------------------------------------------------------------");
                }
                return null;
            }
        };
    }
}