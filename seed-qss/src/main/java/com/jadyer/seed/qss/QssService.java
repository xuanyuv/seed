package com.jadyer.seed.qss;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.annotation.SeedLock;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.boot.QssRun;
import com.jadyer.seed.qss.helper.JobAllowConcurrentFactory;
import com.jadyer.seed.qss.helper.JobDisallowConcurrentFactory;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
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
     */
    @Resource
    private Scheduler scheduler;
    @Resource
    private JedisPool jedisPool;
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    /**
     * 通过注解注册任务
     */
    void reg(ScheduleTask task){
        LogUtil.getLogger().info("收到任务注册请求：{}" + ReflectionToStringBuilder.toString(task));
        if(null == scheduleTaskRepository.getByAppnameAndName(task.getAppname(), task.getName())){
            //注册过来的任务自动启动
            task.setStatus(SeedConstants.QSS_STATUS_RUNNING);
            task = scheduleTaskRepository.saveAndFlush(task);
            //通过Redis发布订阅来同步到所有QSS节点里面，所以这里注释掉
            //this.upsertJob(task);
            try (Jedis jedis = jedisPool.getResource()) {
                jedis.publish(QssRun.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
            }
        }else{
            LogUtil.getLogger().info("收到任务注册请求：任务已存在：{}#{}，自动忽略。" + task.getAppname(), task.getName());
        }
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
        ScheduleTask task = scheduleTaskRepository.findOne(taskId);
        task.setStatus(SeedConstants.QSS_STATUS_STOP);
        scheduleTaskRepository.delete(taskId);
        //this.upsertJob(task);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(QssRun.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
        }
    }


    /**
     * 更改QuartzJob和数据库任务状态
     */
    @Transactional(rollbackFor=Exception.class)
    boolean updateStatus(long taskId, int status){
        ScheduleTask task = scheduleTaskRepository.findOne(taskId);
        task.setStatus(status);
        boolean flag = 1==scheduleTaskRepository.updateStatusById(status, taskId);
        //this.upsertJob(task);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(QssRun.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
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
        ScheduleTask task = scheduleTaskRepository.findOne(taskId);
        task.setCron(cron);
        boolean flag = 1==scheduleTaskRepository.updateCronById(cron, taskId);
        //this.upsertJob(task);
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.publish(QssRun.CHANNEL_SUBSCRIBER, JSON.toJSONString(task));
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
        ScheduleTask task = scheduleTaskRepository.findOne(taskId);
        JobKey jobKey = JobKey.jobKey(task.getJobname());
        try{
            scheduler.triggerJob(jobKey);
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "立即执行QuartzJob失败：jobname=["+task.getJobname()+"]", e);
        }
    }


    /**
     * 添加／更新／删除QuartzJob
     */
    @SeedLock(key="#task.jobname")
    public void upsertJob(ScheduleTask task){
        if(null == task){
            return;
        }
        try{
            if(task.getStatus() == SeedConstants.QSS_STATUS_STOP){
                ////暂停一个QuartzJob
                //scheduler.pauseJob(jobKey);
                ////恢复一个QuartzJob
                //scheduler.resumeJob(jobKey);
                //删除一个QuartzJob（删除任务后，所对应的Trigger也将被删除）
                scheduler.deleteJob(JobKey.jobKey(task.getJobname()));
                LogUtil.getLogger().info("任务[{}]已从Quartz内存中移除", task.getJobname());
                return;
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobname());
            CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
            if(null != trigger){
                if(task.getCron().equals(trigger.getCronExpression())){
                    return;
                }
                //withMisfireHandlingInstructionDoNothing不触发立即执行，等待下次Cron触发时再开始按频率执行
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron()).withMisfireHandlingInstructionDoNothing();
                //按新的cronExpression表达式重新构建Trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                //按新的Trigger重新设置Job执行
                scheduler.rescheduleJob(triggerKey, trigger);
            }else{
                //Trigger不存在就创建一个
                Class<? extends Job> clazz = SeedConstants.QSS_CONCURRENT_YES == task.getConcurrent() ? JobAllowConcurrentFactory.class : JobDisallowConcurrentFactory.class;
                JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(task.getJobname()).build();
                jobDetail.getJobDataMap().put(SeedConstants.QSS_JOB_DATAMAP_KEY, task);
                //表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron()).withMisfireHandlingInstructionDoNothing();
                //按新的cronExpression表达式构建一个新的Trigger
                trigger = TriggerBuilder.newTrigger().withIdentity(task.getJobname()).withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);
            }
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "UpsertJob失败："+ReflectionToStringBuilder.toString(task), e);
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