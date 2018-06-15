package com.jadyer.seed.qss.helper;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.qss.model.ScheduleTask;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 无状态的任务工厂
 */
public class JobAllowConcurrentFactory implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ScheduleTask task = (ScheduleTask)context.getMergedJobDataMap().get(SeedConstants.QSS_JOB_DATAMAP_KEY);
        JobExecute.invokMethod(task);
    }
}