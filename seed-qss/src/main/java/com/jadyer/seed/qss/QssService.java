package com.jadyer.seed.qss;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.quartz.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class QssService {
    @Resource
    private QssServiceHelper qssServiceHelper;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    /**
     * 通过注解注册任务
     */
    boolean reg(ScheduleTask task){
        ScheduleTask taskFromDB = scheduleTaskRepository.getByAppnameAndName(task.getAppname(), task.getName());
        //新增任务
        if(null == taskFromDB){
            qssServiceHelper.addJob(scheduleTaskRepository.saveAndFlush(task));
            return true;
        }
        return true;
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
        qssServiceHelper.deleteJob(scheduleTaskRepository.findOne(taskId).getJobname());
        scheduleTaskRepository.delete(taskId);
    }


    /**
     * 更改QuartzJob和数据库任务状态
     */
    @Transactional(rollbackFor=Exception.class)
    boolean updateStatus(long taskId, int status){
        ScheduleTask task = scheduleTaskRepository.findOne(taskId);
        task.setStatus(status);
        if(SeedConstants.QSS_STATUS_RUNNING == status){
            qssServiceHelper.addJob(task);
        }else{
            qssServiceHelper.deleteJob(task.getJobname());
        }
        return 1==scheduleTaskRepository.updateStatusById(status, taskId);
    }


    /**
     * 更新CronExpression
     */
    @Transactional(rollbackFor=Exception.class)
    boolean updateCron(long taskId, String cron){
        if(!CronExpression.isValidExpression(cron)){
            throw new IllegalArgumentException("CronExpression不正确");
        }
        ScheduleTask task = scheduleTaskRepository.findOne(taskId);
        if(SeedConstants.QSS_STATUS_RUNNING == task.getStatus()){
            qssServiceHelper.updateJobCron(task.getJobname(), cron);
        }
        return 1==scheduleTaskRepository.updateCronById(cron, taskId);
    }


    /**
     * 立即执行一个QuartzJOB
     */
    void triggerJob(long taskId) {
        qssServiceHelper.triggerJob(scheduleTaskRepository.findOne(taskId).getJobname());
    }
}