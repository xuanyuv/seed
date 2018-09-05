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
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

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
    private Step step0004;
    @Resource
    private Step step0005;
    @Resource
    private Step step0006;
    @Resource
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    public Job settleJob() {
        return jobBuilderFactory.get("settleJob")
                .incrementer(new RunIdIncrementer())
                .listener(this.jobExecutionListener())
                //先执行step0001
                .flow(step0001)
                //然后step0004、step0005、step0006开始执行且三者是并行执行互不影响的
                .next(this.splitFlow())
                //接着等三者全都执行完毕才会去执行step0002
                .next(step0002)
                //最后执行step0003
                .next(step0003)
                .end()
                .build();
    }


    //https://docs.spring.io/spring-batch/4.0.x/reference/html/index-single.html#scalabilityParallelSteps
    private Flow splitFlow(){
        Flow flow04 = new FlowBuilder<SimpleFlow>("flow04").start(step0004).build();
        Flow flow05 = new FlowBuilder<SimpleFlow>("flow05").start(step0005).build();
        Flow flow06 = new FlowBuilder<SimpleFlow>("flow06").start(step0006).build();
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(new SimpleAsyncTaskExecutor("spring_batch_seedboot"))
                .add(flow04, flow05, flow06)
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