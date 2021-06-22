package com.jadyer.seed.controller.batch;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.SpringContextHolder;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.SystemClockUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
     * -----------------------------------------------------------------------------------------------
     * 执行Step过程中发生异常时，而该异常又没有被配置为skip，那么整个Job会中断
     * 当人工修正完异常数据后，再次调用jobLauncher.run()，SpringBatch会从上次异常的地方开始跑
     * 1、当Step为读处理写时，假设10条数据，Step配置chunk=3（表明每三条Write一次），若第5条出现异常
     *    那么前三条可以成功Write，第4条即便处理成功也不会写入数据库，在修复后再跑的时，会从第4条开始读
     * 2、当Step为Tasklet时，仅当Tasklet全部执行完，且未发生异常，才会真正的提交事务，写入数据到数据库
     *    即只要其中某一条数据处理时发生异常，那么无论之前提交了多少次数据到数据库，都不会真正的写入数据库
     * 3、当并行Step中的某个Step出现异常时，那么并行中的其它Step不受影响，会继续跑完，然后才会中断Job
     *    修复数据后再跑时，会直接从并行中发生异常的该Step开始跑，其它未发生异常的并行中的Step不会重复跑
     * 注意：断点续跑时，传入的jobParameters必须相同，否则会认为是另一个任务，会从头跑，不会从断点的地方跑
     *      也就是说，这一切都建立在jobParameters传值相同的条件下
     * 另外：对于JobOperator.start()和restart()两个方法都试过，都没实现断点续跑的功能
     * -----------------------------------------------------------------------------------------------
     */
    @RequestMapping("/batch")
    //@SeedQSSReg(qssHost="${qss.host}", appHost="${qss.appHost}", appname="${qss.appname}", name="${qss.name}", cron="${qss.cron}")
    CommResult<JobInstance> batch(String bizDate) throws Exception {
        //判断是否断点续跑
        boolean isResume = false;
        if(StringUtils.isBlank(bizDate)){
            bizDate = DateFormatUtils.format(new Date(), "yyyyMMdd");
        }else{
            isResume = true;
        }
        LogUtil.getLogger().info("结算跑批{}：Starting...bizDate={}", isResume?"：断点续跑":"", bizDate);
        //构造JobParameters
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addString("bizDate", bizDate);
        //执行job
        JobExecution execution = jobLauncher.run(settleJob, jobParametersBuilder.toJobParameters());
        LogUtil.getLogger().info("结算跑批{}：Ending......", isResume?"：断点续跑":"");
        return CommResult.success(execution.getJobInstance());
    }


    @RequestMapping("/xmlBatch")
    CommResult<JobInstance> xmlBatch(String time) throws Exception {
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap.put("filePath", "/data/seedboot-batch.txt");
        parameterMap.put("birthDay", "20190911");
        JobExecution execution = this.runJob("xmlSettleJob", "结算跑批", time, parameterMap);
        return CommResult.success(execution.getJobInstance());
    }


    /**
     * @param jobNameStr   任务名字符串
     * @param jobNameDesc  任务名描述
     * @param time         随机数：批量的唯一标志（非断点续跑可直接传null）
     * @param parameterMap 其它参数：不会作为批量的唯一标志（无参可传null）
     * Comment by 玄玉<https://jadyer.cn/> on 2019/8/12 18:25.
     */
    private JobExecution runJob(String jobNameStr, String jobNameDesc, String time, Map<String, String> parameterMap) throws Exception {
        //判断是否断点续跑
        boolean isResume = false;
        long timeLong;
        if(StringUtils.isBlank(time)){
            timeLong = SystemClockUtil.INSTANCE.now();
        }else{
            isResume = true;
            timeLong = Long.parseLong(time);
        }
        LogUtil.getLogger().info("{}==>{}：Starting...time={}，parameterMap={}", jobNameDesc, isResume?"：断点续跑":"", timeLong, JSON.toJSONString(parameterMap));
        //构造JobParameters
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobParametersBuilder.addLong("time", timeLong);
        if(MapUtils.isNotEmpty(parameterMap)){
            for(Map.Entry<String,String> entry : parameterMap.entrySet()){
                jobParametersBuilder.addString(entry.getKey(), entry.getValue(), false);
            }
        }
        //执行job
        Job xmlSettleJob = (Job)SpringContextHolder.getBean(jobNameStr);
        JobExecution execution = jobLauncher.run(xmlSettleJob, jobParametersBuilder.toJobParameters());
        LogUtil.getLogger().info("{}==>{}：Ending......jobInstance={}", jobNameDesc, isResume?"：断点续跑":"", execution.getJobInstance());
        return execution;
    }
}