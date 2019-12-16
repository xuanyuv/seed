package com.jadyer.seed.controller.batch;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ChunkListener;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/12/14 18:53.
 */
@Component
public class SettleJobListeners implements JobExecutionListener, StepExecutionListener, ChunkListener, ItemReadListener, ItemProcessListener, ItemWriteListener {
    private StepExecution stepExecution;

    @BeforeStep
    public void initStepExecution(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }


    @Override
    public void beforeJob(JobExecution jobExecution) {
        LogUtil.getLogger().info("批量任务Job-->[{}-{}]-->開始处理，JobParameters=[{}]", jobExecution.getJobId(), jobExecution.getJobInstance().getJobName(), jobExecution.getJobParameters());
    }


    @Override
    public void afterJob(JobExecution jobExecution) {
        if(jobExecution.getStatus() == BatchStatus.COMPLETED){
            LogUtil.getLogger().info("批量任务Job-->[{}-{}]-->处理完成，TotalDuration[{}]ms", jobExecution.getJobId(), jobExecution.getJobInstance().getJobName(), SystemClockUtil.INSTANCE.now()-jobExecution.getStartTime().getTime());
            LogUtil.getLogger().info("=======================================================================");
        }
    }


    @Override
    public void beforeStep(StepExecution stepExecution) {
        LogUtil.getLogger().info("批量任务Step-->[{}-{}]-->開始处理", stepExecution.getJobExecutionId(), stepExecution.getStepName());
    }


    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        if(stepExecution.getStatus() == BatchStatus.COMPLETED){
            LogUtil.getLogger().info("批量任务Step-->[{}-{}]-->处理完成，ReadCount=[{}]，WriteCount=[{}]，CommitCount==[{}]，Duration[{}]ms", stepExecution.getJobExecutionId(), stepExecution.getStepName(), stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getCommitCount(), SystemClockUtil.INSTANCE.now()-stepExecution.getStartTime().getTime());
            LogUtil.getLogger().info("-----------------------------------------------------------------------");
        }
        return stepExecution.getExitStatus();
    }


    @Override
    public void beforeChunk(ChunkContext context) {
        StepExecution stepExecution = context.getStepContext().getStepExecution();
        LogUtil.getLogger().info("批量任务Step-->[{}-{}]-->Chunk开始处理", stepExecution.getJobExecutionId(), stepExecution.getStepName());
    }


    @Override
    public void afterChunk(ChunkContext context) {
        StepExecution stepExecution = context.getStepContext().getStepExecution();
        LogUtil.getLogger().info("批量任务Step-->[{}-{}]-->Chunk处理完成，ReadCount=[{}]，WriteCount=[{}]，CommitCount==[{}]，Duration[{}]ms", stepExecution.getJobExecutionId(), stepExecution.getStepName(), stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getCommitCount(), SystemClockUtil.INSTANCE.now()-stepExecution.getStartTime().getTime());
    }


    @Override
    public void afterChunkError(ChunkContext context) {

    }


    /**
     * 假如chunkSize=2，那么调用顺序如下
     * beforeRead--afterRead--beforeRead--afterRead--beforeProcess...
     * Comment by 玄玉<https://jadyer.cn/> on 2019/12/16 16:45.
     */
    @Override
    public void beforeRead() {
        LogUtil.getLogger().info("beforeRead() is invoked");
    }


    @Override
    public void afterRead(Object item) {
        LogUtil.getLogger().info("afterRead() is invoked");
    }


    @Override
    public void onReadError(Exception ex) {

    }


    @Override
    public void beforeProcess(Object item) {
        LogUtil.getLogger().info("beforeProcess() is invoked");
    }


    @Override
    public void afterProcess(Object item, Object result) {
        LogUtil.getLogger().info("afterProcess() is invoked");
    }


    @Override
    public void onProcessError(Object item, Exception e) {

    }


    @Override
    public void beforeWrite(List items) {
        LogUtil.getLogger().error("批量任务Step-->[{}-{}]-->beforeWrite() is invoked, the items is {}", stepExecution.getJobExecutionId(), stepExecution.getStepName(), JSON.toJSONString(items));
    }


    @Override
    public void afterWrite(List items) {
        LogUtil.getLogger().error("批量任务Step-->[{}-{}]-->afterWrite() is invoked, the items is {}", stepExecution.getJobExecutionId(), stepExecution.getStepName(), JSON.toJSONString(items));
    }


    @Override
    public void onWriteError(Exception exception, List items) {
        LogUtil.getLogger().error("批量任务Step-->[{}-{}]-->write error, because {}", stepExecution.getJobExecutionId(), stepExecution.getStepName(), exception.getMessage());
    }
}