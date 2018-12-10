package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.SpringContextHolder;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/8/13 20:01.
 */
@RestController
@RequestMapping("/quartz")
@ImportResource("classpath:config/batch/spring-batch.xml")
@EnableBatchProcessing
public class SettleQuartzController {
    @Resource
    private Job settleJob;
    @Resource
    private JobLauncher jobLauncher;

    /**
     * SpringBatch的断点续跑
     * --------------------------------------------------------------------------------------------------------
     * 1、执行Step过程中发生异常时，而该异常又没有被配置为skip，那么整个Job会中断
     * 2、当人工修正完异常数据后，再次调用jobLauncher.run()，SpringBatch会从上次异常的地方开始跑
     * 3、注意：传入的jobParameters必须相同，若第二次跑时传的值不同则会认为是另一个任务，就会从头跑，不会从断点的地方跑
     * 4、假设：10条数据，Step配置chunk=3（表明每三条Write一次），若第5条出现异常，则修复后再跑的时，会从第4条开始读
     * 5、如果：并行Step中的某个Step出现异常，那么并行中的其它Step不会受影响会继续跑完，然后才会中断Job
     *         修复数据后再跑时，会直接从并行中发生异常的该Step开始跑，这一切都建立在jobParameters传值相同的条件下
     * 6、对于JobOperator.start()和restart()两个方法都试过，都没实现断点续跑的功能
     * --------------------------------------------------------------------------------------------------------
     */
    @RequestMapping("/batch")
    //@SeedQSSReg(qssHost="${qss.host}", appHost="${qss.appHost}", appname="${qss.appname}", name="${qss.name}", cron="${qss.cron}")
    CommResult<JobInstance> batch(String bizDate) throws Exception {
        boolean isResume = false;
        if(StringUtils.isBlank(bizDate)){
            bizDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
        }else{
            isResume = true;
        }
        LogUtil.getLogger().info("结算跑批{}：Starting...bizDate={}", isResume?"：断点续跑":"", bizDate);
        JobParameters jobParameters = new JobParametersBuilder().addString("bizDate", bizDate).toJobParameters();
        JobExecution execution = jobLauncher.run(settleJob, jobParameters);
        LogUtil.getLogger().info("结算跑批{}：Ending......", isResume?"：断点续跑":"");
        return CommResult.success(execution.getJobInstance());
    }


    @RequestMapping("/xmlBatch")
    CommResult<JobInstance> xmlBatch(String time) throws Exception {
        boolean isResume = false;
        long timeLong;
        if(StringUtils.isBlank(time)){
            timeLong = SystemClockUtil.INSTANCE.now();
        }else{
            isResume = true;
            timeLong = Long.parseLong(time);
        }
        LogUtil.getLogger().info("结算跑批{}：Starting...time={}", isResume?"：断点续跑":"", timeLong);
        JobParameters jobParameters = new JobParametersBuilder().addLong("time", timeLong).toJobParameters();
        Job xmlSettleJob = (Job)SpringContextHolder.getBean("xmlSettleJob");
        JobExecution execution = jobLauncher.run(xmlSettleJob, jobParameters);
        LogUtil.getLogger().info("结算跑批{}：Ending......", isResume?"：断点续跑":"");
        return CommResult.success(execution.getJobInstance());
    }
}