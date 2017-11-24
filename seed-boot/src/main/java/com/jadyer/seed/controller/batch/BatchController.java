package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/11/24 16:54.
 */
@RestController
@RequestMapping("/batch")
public class BatchController {
    @Resource
    private Job importPeopleJob;
    @Resource
    private JobLauncher jobLauncher;

    @RequestMapping("/calcBill")
    CommonResult calcBill() throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addDate("date", new Date()).toJobParameters();
        JobExecution execution = jobLauncher.run(importPeopleJob, jobParameters);
        LogUtil.getLogger().info("Job已启动-->{}", execution);
        return new CommonResult(execution);
    }
}