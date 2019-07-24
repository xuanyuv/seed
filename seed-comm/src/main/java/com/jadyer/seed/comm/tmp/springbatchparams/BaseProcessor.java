package com.jadyer.seed.comm.tmp.springbatchparams;

import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;

public abstract class BaseProcessor<I, O> implements ItemProcessor<I, O> {
    private StepExecution stepExecution ;

    @BeforeStep
    public void beforStep(StepExecution stepExecution) {
        if(null==this.stepExecution) {
            this.stepExecution = stepExecution;
            LogUtil.getLogger().info("****************初始化Step信息完成****************");
        }
    }

    @Override
    final public O process(I input) throws Exception {

        JobParameters params = stepExecution.getJobParameters();
        ExecutionContext stepContext = stepExecution.getExecutionContext();
        return doProcess(input, params, stepContext);
    }

    public abstract O doProcess(I input, JobParameters params, ExecutionContext stepContext) throws Exception;
}