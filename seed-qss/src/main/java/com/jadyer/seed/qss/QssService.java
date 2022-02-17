package com.jadyer.seed.qss;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.qss.helper.JobComponent;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Optional;

@Service
public class QssService {
    @Resource
    private JobComponent jobComponent;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    public Optional<ScheduleTask> getOptional(Long id){
        return scheduleTaskRepository.findById(id);
    }


    /**
     * 查不到数据时，会抛出IllegalArgumentException异常
     */
    public ScheduleTask get(Long id){
        return this.getOptional(id).orElseThrow(() -> new IllegalArgumentException("无此数据：ScheduleTask.id=[" + id + "]"));
    }


    /**
     * 新增任务到数据库
     */
    @Transactional(rollbackFor=Exception.class)
    ScheduleTask saveTask(ScheduleTask task){
        if(!CronExpression.isValidExpression(task.getCron())){
            throw new IllegalArgumentException("CronExpression不正确");
        }
        return scheduleTaskRepository.saveAndFlush(task);
    }


    /**
     * 移除QuartzJob和数据库中的任务
     */
    @Transactional(rollbackFor=Exception.class)
    void deleteTask(long taskId){
        ScheduleTask task = this.get(taskId);
        scheduleTaskRepository.deleteById(taskId);
        jobComponent.deleteJob(task);
    }


    /**
     * 更改QuartzJob和数据库任务状态
     */
    @Transactional(rollbackFor=Exception.class)
    void updateStatus(long taskId, int status){
        ScheduleTask task = this.get(taskId);
        task.setStatus(status);
        scheduleTaskRepository.updateStatusById(status, taskId);
        switch(status){
            case SeedConstants.QSS_STATUS_STOP   : jobComponent.deleteJob(task); break;
            case SeedConstants.QSS_STATUS_RUN    : jobComponent.upsertJob(task); break;
            case SeedConstants.QSS_STATUS_PAUSE  : jobComponent.pauseJob(task);  break;
            case SeedConstants.QSS_STATUS_RESUME : jobComponent.resumeJob(task); break;
            default: throw new IllegalArgumentException("无法识别的status=[" + status + "]");
        }
    }


    /**
     * 更新CronExpression
     */
    @Transactional(rollbackFor=Exception.class)
    void updateCron(long taskId, String cron){
        if(!CronExpression.isValidExpression(cron)){
            throw new IllegalArgumentException("CronExpression不正确");
        }
        ScheduleTask task = this.get(taskId);
        task.setCron(cron);
        scheduleTaskRepository.updateCronById(cron, taskId);
        jobComponent.upsertJob(task);
    }
}