package com.jadyer.seed.controller.batch;

import com.alibaba.druid.pool.DruidDataSource;
import com.jadyer.seed.comm.util.LogUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 并行Step测试使用
 * Created by 玄玉<https://jadyer.cn/> on 2018/9/5 11:06.
 */
@Component
public class StepService0005 {
    @Resource
    private DruidDataSource dataSource;
    @Resource
    private StepBuilderFactory stepBuilderFactory;
    @Resource
    private StepExecutionListener stepExecutionListener;

    @Bean
    public Step step0005(){
        return stepBuilderFactory.get("step0005")
                .listener(stepExecutionListener)
                .tasklet(this.nextBizDateTasklet())
                .build();
    }


    private Tasklet nextBizDateTasklet(){
        return new Tasklet() {
            @Override
            public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
                //入参contribution=org.springframework.batch.core.StepContribution@153ef3fc[readCount=0,writeCount=0,filterCount=0,parentSkipCount=0,readSkipCount=0,writeSkipCount=0,processSkipCount=0,exitStatus=exitCode=EXECUTING;exitDescription=]，chunkContext=org.springframework.batch.core.scope.context.ChunkContext@2313a648[stepContext=SynchronizedAttributeAccessor: [], stepExecutionContext={batch.taskletType=com.jadyer.seed.controller.batch.StepService0005$1, batch.stepType=org.springframework.batch.core.step.tasklet.TaskletStep}, jobExecutionContext={}, jobParameters={time=1536145118798},complete=false,attributes={}]，chunkContext.getStepContext=org.springframework.batch.core.scope.context.StepContext@3441dec5[stepExecution=StepExecution: id=23, version=1, name=step0005, status=STARTED, exitStatus=EXECUTING, readCount=0, filterCount=0, writeCount=0 readSkipCount=0, writeSkipCount=0, processSkipCount=0, commitCount=0, rollbackCount=0, exitDescription=,callbacks={},propertyContext=<null>,support=org.springframework.batch.repeat.context.SynchronizedAttributeAccessor$1@0]
                LogUtil.getLogger().info("入参contribution={}，chunkContext={}，chunkContext.getStepContext={}", ReflectionToStringBuilder.toString(contribution), ReflectionToStringBuilder.toString(chunkContext), ReflectionToStringBuilder.toString(chunkContext.getStepContext()));
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return RepeatStatus.FINISHED;
            }
        };
    }
}