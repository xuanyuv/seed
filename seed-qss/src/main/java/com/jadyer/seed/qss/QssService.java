package com.jadyer.seed.qss;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.quartz.CronExpression;
import org.quartz.CronTrigger;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class QssService {
    /**
     * 注入Spring管理的Scheduler
     * ----------------------------------------------------------------------------------------------------
     * 1.原本应该注入applicationContext.xml配置的org.springframework.scheduling.quartz.SchedulerFactoryBean
     * 2.由于SchedulerFactoryBean是一个工厂Bean，得到的不是它本身，而是它负责创建的org.quartz.impl.StdScheduler
     *   所以就要注意：在使用注解注入SchedulerFactoryBean的时候，要通过类型来注入，否则会报告类似下面的异常
     *   Bean named 'schedulerFactoryBean' must be of type [org.springframework.scheduling.quartz.SchedulerFactoryBean], but was actually of type [org.quartz.impl.StdScheduler]
     * 3.在查看SchedulerFactoryBean源码后发现，它的getObject()方法是返回的Scheduler对象
     *   既然如此，我们就不必注入SchedulerFactoryBean再调用getScheduler()这么麻烦了，可以直接声明Scheduler对象
     *   这里涉及到getBean("bean")和getBean("&bean")的区别
     * ----------------------------------------------------------------------------------------------------
     * FactoryBean源代码分析
     * 如果bean实现了FactoryBean接口，那么BeanFactory将把它作为一个bean工厂，而不是直接作为普通bean
     * 正常情况下，BeanFactory的getBean("bean")返回FactoryBean生产的bean实例，也就是getObject()里面的东西
     * 如果要返回FactoryBean本身的实例，需调用getBean("&bean")
     * ----------------------------------------------------------------------------------------------------
     * https://docs.spring.io/spring-boot/docs/2.0.5.RELEASE/reference/htmlsingle/#boot-features-quartz
     * ----------------------------------------------------------------------------------------------------
     */
    @Resource
    private Scheduler scheduler;
    @Resource
    private JedisPool jedisPool;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

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
        Optional<ScheduleTask> taskOptional = scheduleTaskRepository.findById(taskId);
        if(!taskOptional.isPresent()){
            throw new RuntimeException("不存在的任务：taskId=[" + taskId + "]");
        }
        ScheduleTask task = taskOptional.get();
        task.setStatus(SeedConstants.QSS_STATUS_STOP);
        scheduleTaskRepository.deleteById(taskId);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(SeedConstants.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
        }
    }


    /**
     * 更改QuartzJob和数据库任务状态
     */
    @Transactional(rollbackFor=Exception.class)
    boolean updateStatus(long taskId, int status){
        Optional<ScheduleTask> taskOptional = scheduleTaskRepository.findById(taskId);
        if(!taskOptional.isPresent()){
            throw new RuntimeException("不存在的任务：taskId=[" + taskId + "]");
        }
        ScheduleTask task = taskOptional.get();
        task.setStatus(status);
        boolean flag = 1 == scheduleTaskRepository.updateStatusById(status, taskId);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(SeedConstants.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
        }
        return flag;
    }


    /**
     * 更新CronExpression
     */
    @Transactional(rollbackFor=Exception.class)
    boolean updateCron(long taskId, String cron){
        if(!CronExpression.isValidExpression(cron)){
            throw new IllegalArgumentException("CronExpression不正确");
        }
        Optional<ScheduleTask> taskOptional = scheduleTaskRepository.findById(taskId);
        if(!taskOptional.isPresent()){
            throw new RuntimeException("不存在的任务：taskId=[" + taskId + "]");
        }
        ScheduleTask task = taskOptional.get();
        task.setCron(cron);
        boolean flag = 1 == scheduleTaskRepository.updateCronById(cron, taskId);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(SeedConstants.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
        }
        return flag;
    }


    /**
     * 立即执行一个QuartzJob（只会运行一次）
     * ----------------------------------------------------------------------------
     * Quartz是通过临时生成一个Trigger（Trigger的key是随机生成的）的方式实现的
     * 该临时Trigger将在本次任务运行完成之后自动删除
     * ----------------------------------------------------------------------------
     */
    void triggerJob(long taskId) {
        Optional<ScheduleTask> taskOptional = scheduleTaskRepository.findById(taskId);
        if(!taskOptional.isPresent()){
            throw new RuntimeException("不存在的任务：taskId=[" + taskId + "]");
        }
        ScheduleTask task = taskOptional.get();
        JobKey jobKey = JobKey.jobKey(task.getJobname());
        try{
            scheduler.triggerJob(jobKey);
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "立即执行QuartzJob失败：jobname=["+task.getJobname()+"]", e);
        }
    }


    /**
     * 获取所有正在运行的QuartzJob
     * -------------------------------------------------------------------------------------
     * Trigger各状态说明
     * None------Trigger已经完成，且不会再执行，或者找不到该触发器，或者Trigger已被删除
     * NORMAL----正常状态
     * PAUSED----暂停状态
     * COMPLETE--触发器完成，但任务可能还正在执行中
     * BLOCKED---线程阻塞状态
     * ERROR-----出现错误
     * -------------------------------------------------------------------------------------
     */
    List<ScheduleTask> getAllRunningJob(){
        try{
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            List<ScheduleTask> taskList = new ArrayList<>(executingJobs.size());
            for(JobExecutionContext obj : executingJobs){
                taskList.add(this.convertToScheduleTask(obj.getJobDetail().getKey(), obj.getTrigger()));
            }
            return taskList;
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "获取所有正在运行的QuartzJob失败", e);
        }
    }


    /**
     * 获取所有计划中的QuartzJob
     */
    List<ScheduleTask> getAllJob(){
        try{
            List<ScheduleTask> taskList = new ArrayList<>();
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            for(JobKey jobKey : jobKeys){
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for(Trigger trigger : triggers){
                    taskList.add(this.convertToScheduleTask(jobKey, trigger));
                }
            }
            return taskList;
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "获取所有计划中的QuartzJob失败", e);
        }
    }


    private ScheduleTask convertToScheduleTask(JobKey jobKey, Trigger trigger) throws SchedulerException {
        ScheduleTask task = new ScheduleTask();
        String[] jobname = jobKey.getName().split(":");
        task.setId(Long.parseLong(jobname[0]));
        task.setAppname(jobname[1]);
        task.setName(jobname[2]);
        //task.setGroup(jobKey.getGroup());
        //task.setDesc("触发器[" + trigger.getKey() + "]");
        //task.setStartTime(trigger.getStartTime());             //开始时间
        //task.setEndTime(trigger.getEndTime());                 //结束时间
        task.setNextFireTime(trigger.getNextFireTime());         //下次触发时间
        task.setPreviousFireTime(trigger.getPreviousFireTime()); //上次触发时间
        if(trigger instanceof CronTrigger){
            task.setCron(((CronTrigger)trigger).getCronExpression());
        }
        Trigger.TriggerState triggerState = scheduler.getTriggerState(trigger.getKey());
        task.setStatus("N".equals(triggerState.name()) ? SeedConstants.QSS_STATUS_STOP : SeedConstants.QSS_STATUS_RUNNING);
        return task;
    }
}