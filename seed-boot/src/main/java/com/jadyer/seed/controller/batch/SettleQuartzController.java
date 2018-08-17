package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
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

    @RequestMapping("/batch")
    //@SeedQSSReg(qssHost="${qss.host}", appHost="${qss.appHost}", appname="${qss.appname}", name="${qss.name}", cron="${qss.cron}")
    CommResult<JobExecution> batch() throws Exception {
        LogUtil.getLogger().info("结算跑批：Starting...");
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", SystemClockUtil.INSTANCE.now()).toJobParameters();
        JobExecution execution = jobLauncher.run(settleJob, jobParameters);
        LogUtil.getLogger().info("结算跑批：Ending......");
        return CommResult.success(execution);
    }
}