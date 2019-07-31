package com.jadyer.seed.comm.tmp.springbatchparams;

import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

public abstract class BaseProcessor<I, O> implements ItemProcessor<I, O> {
    private StepExecution stepExecution;

    @BeforeStep
    public void beforeStep(StepExecution stepExecution){
        if(null == this.stepExecution){
            this.stepExecution = stepExecution;
            LogUtil.getLogger().info("===>>>Step信息初始化完成");
        }
    }

    @Override
    public O process(I item) throws Exception {
        JobParameters jobParameters = stepExecution.getJobParameters();
        ExecutionContext executionContext = stepExecution.getExecutionContext();
        return this.doProcess(item, jobParameters, executionContext);
    }

    public abstract O doProcess(I item, JobParameters jobParameters, ExecutionContext executionContext) throws Exception;
}