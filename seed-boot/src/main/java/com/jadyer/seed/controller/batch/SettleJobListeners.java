package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/12/14 18:53.
 */
@Component
public class SettleJobListeners implements JobExecutionListener, StepExecutionListener {
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
}