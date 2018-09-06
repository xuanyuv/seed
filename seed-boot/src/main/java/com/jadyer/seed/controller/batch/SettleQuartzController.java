package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/8/13 20:01.
 */
@RestController
@RequestMapping("/quartz")
public class SettleQuartzController {
    @Resource
    private Job settleJob;
    @Resource
    private JobLauncher jobLauncher;
    @Resource
    private JobExplorer jobExplorer;
    @Resource
    private JobOperator jobOperator;

    @RequestMapping("/batch")
    //@SeedQSSReg(qssHost="${qss.host}", appHost="${qss.appHost}", appname="${qss.appname}", name="${qss.name}", cron="${qss.cron}")
    CommResult<JobExecution> batch() throws Exception {
        LogUtil.getLogger().info("结算跑批：Starting...");
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", SystemClockUtil.INSTANCE.now()).toJobParameters();
        JobExecution execution = null;
        try {
            execution = jobLauncher.run(settleJob, jobParameters);
        } catch (JobInstanceAlreadyCompleteException e) {
            //org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException: A job instance already exists and is complete for parameters={time=1534750955369}.  If you want to run this job again, change the parameters.
            LogUtil.getLogger().info("结算跑批：该任务已存在且执行成功，如需重复执行，请更换JobParameters");
        }
        LogUtil.getLogger().info("结算跑批：Ending......");
        return CommResult.success(execution);
    }


    @RequestMapping("/getinfo")
    CommResult<Boolean> test() throws Exception {
        System.out.println(ReflectionToStringBuilder.toString(jobExplorer.getJobInstance(7L)));
        //Set<Long> executions = jobOperator.getRunningExecutions("sampleJob");
        //jobOperator.stop(executions.iterator().next());
        System.out.println(ReflectionToStringBuilder.toString(jobOperator.getRunningExecutions("job001")));
        return CommResult.success(Boolean.TRUE);
    }
}