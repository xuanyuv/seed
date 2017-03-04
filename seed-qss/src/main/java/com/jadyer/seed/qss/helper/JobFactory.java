package com.jadyer.seed.qss.helper;

import com.jadyer.seed.qss.module.ScheduleTask;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * 无状态的任务工厂
 */
public class JobFactory implements Job {
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		ScheduleTask task = (ScheduleTask)context.getMergedJobDataMap().get(ScheduleTask.JOB_DATAMAP_KEY);
		JobHelper.invokMethod(task);
	}
}