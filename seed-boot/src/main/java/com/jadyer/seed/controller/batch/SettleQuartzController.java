package com.jadyer.seed.controller.batch;

import com.jadyer.seed.comm.constant.CommResult;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
    private Job xmlSettleJob;
    @Resource
    private JobLauncher jobLauncher;
    @Resource
    private BatchComponent batchComponent;

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
    CommResult<JobInstance> batch(String bizDate) {
        JobExecution execution = batchComponent.runJob(settleJob, "结算跑批", bizDate, null);
        return CommResult.success(execution.getJobInstance());
    }


    @RequestMapping("/xmlBatch")
    CommResult<JobInstance> xmlBatch(String time) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("filePath", "/data/seedboot-batch.txt");
        paramMap.put("birthDay", "20190911");
        // Job xmlSettleJob = (Job)SpringContextHolder.getBean("xmlSettleJob");
        JobExecution execution = batchComponent.runJob(xmlSettleJob, "结算跑批", time, paramMap);
        return CommResult.success(execution.getJobInstance());
    }
}