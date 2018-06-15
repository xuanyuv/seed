package com.jadyer.seed.qss.helper;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.log.annotation.EnableLog;
import com.jadyer.seed.qss.QssServiceHelper;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2018/6/13 18:59.
 */
@Component
@EnableScheduling
public class JobQuartz {
    @Resource
    private QssServiceHelper qssServiceHelper;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    /**
     * 同步数据库任务到内存（两分钟一次）
     */
    @EnableLog
    @Scheduled(cron="0 */2 * * * ?")
    void syncTask(){
        // TODO Redis消息订阅
        for(ScheduleTask task : scheduleTaskRepository.findAll()){
            if(task.getStatus() == SeedConstants.QSS_STATUS_STOP){
                continue;
            }
            qssServiceHelper.addJob(task);
        }
    }
}