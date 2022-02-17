package com.jadyer.seed.qss.helper;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.jpa.Condition;
import com.jadyer.seed.comm.util.LogUtil;
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
import org.quartz.TriggerUtils;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/13 18:59.
 */
@Component
public class JobComponent {
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
    private ScheduleTaskRepository scheduleTaskRepository;

    @PostConstruct
    public void initJob() {
        Condition<ScheduleTask> spec = Condition.<ScheduleTask>and().in("status", Arrays.asList(SeedConstants.QSS_STATUS_RUN, SeedConstants.QSS_STATUS_RESUME));
        List<ScheduleTask> taskList = scheduleTaskRepository.findAll(spec);
        LogUtil.getLogger().info("Quartz内存启动初始化：数据库读取到待处理Job={}个", taskList.size());
        for(ScheduleTask task : taskList){
            this.upsertJob(task);
        }
        LogUtil.getLogger().info("Quartz内存启动初始化：处理完毕");
    }


    /**
     * 添加一个Job
     */
    public void upsertJob(ScheduleTask task){
        if(null==task || (SeedConstants.QSS_STATUS_RUN!=task.getStatus() && SeedConstants.QSS_STATUS_RESUME!=task.getStatus())){
            return;
        }
        String jobName = task.getId() + ":" + task.getAppname() + ":" + task.getName();
        try{
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
            CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
            if(null != trigger){
                if(task.getCron().equals(trigger.getCronExpression())){
                    LogUtil.getLogger().info("Quartz内存：相同的Cron，故无需更新任务=[{}]", jobName);
                    return;
                }
                //Trigger已存在则更新相应的定时设置（withMisfireHandlingInstructionDoNothing不触发立即执行，等待下次Cron触发时再开始按频率执行）
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron()).withMisfireHandlingInstructionDoNothing();
                //按新的cronExpression表达式重新构建Trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                //按新的Trigger重新设置Job执行
                scheduler.rescheduleJob(triggerKey, trigger);
                LogUtil.getLogger().info("Quartz内存：已更新任务=[{}]", jobName);
            }else{
                //Trigger不存在就创建一个
                Class<? extends Job> clazz = SeedConstants.QSS_CONCURRENT_YES==task.getConcurrent() ? JobAllowConcurrentFactory.class : JobDisallowConcurrentFactory.class;
                JobDetail jobDetail = JobBuilder.newJob(clazz).withIdentity(jobName).build();
                jobDetail.getJobDataMap().put(SeedConstants.QSS_JOB_DATAMAP_KEY, task);
                //表达式调度构建器
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron()).withMisfireHandlingInstructionDoNothing();
                //按新的cronExpression表达式构建一个新的Trigger
                trigger = TriggerBuilder.newTrigger().withIdentity(jobName).withSchedule(scheduleBuilder).build();
                scheduler.scheduleJob(jobDetail, trigger);
                LogUtil.getLogger().info("Quartz内存：已新增任务=[{}]", jobName);
            }
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "添加任务失败："+ReflectionToStringBuilder.toString(task), e);
        }
    }


    /**
     * 删除一个Job（删除任务后，所对应的Trigger也将被删除）
     */
    public void deleteJob(ScheduleTask task){
        String jobName = task.getId() + ":" + task.getAppname() + ":" + task.getName();
        try{
            scheduler.deleteJob(JobKey.jobKey(jobName));
            LogUtil.getLogger().info("Quartz内存：已删除任务=[{}]", jobName);
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "删除任务失败："+ReflectionToStringBuilder.toString(task), e);
        }
    }


    /**
     * 挂起一个Job
     */
    public void pauseJob(ScheduleTask task){
        String jobName = task.getId() + ":" + task.getAppname() + ":" + task.getName();
        try{
            scheduler.pauseJob(JobKey.jobKey(jobName));
            LogUtil.getLogger().info("Quartz内存：已挂起任务=[{}]", jobName);
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "挂起任务失败："+ReflectionToStringBuilder.toString(task), e);
        }
    }


    /**
     * 恢复一个Job
     */
    public void resumeJob(ScheduleTask task){
        String jobName = task.getId() + ":" + task.getAppname() + ":" + task.getName();
        try{
            TriggerKey triggerKey = TriggerKey.triggerKey(jobName);
            CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
            if(null == trigger){
                // 挂起任务后，关闭应用，然后再启动应用，这时的恢复指令就需要在Quartz内存中添加任务
                this.upsertJob(task);
                LogUtil.getLogger().info("Quartz内存：已恢复(添加)任务=[{}]", jobName);
            }else{
                scheduler.resumeJob(JobKey.jobKey(jobName));
                LogUtil.getLogger().info("Quartz内存：已恢复任务=[{}]", jobName);
            }
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "恢复任务失败："+ReflectionToStringBuilder.toString(task), e);
        }
    }


    /**
     * 立即执行一个Job（只会运行一次）
     * ----------------------------------------------------------------------------
     * Quartz是通过临时生成一个Trigger（TriggerKey是随机生成的）的方式实现的
     * 该临时Trigger将在本次任务运行完成之后自动删除
     * ----------------------------------------------------------------------------
     */
    public void triggerJob(ScheduleTask task){
        String jobName = task.getId() + ":" + task.getAppname() + ":" + task.getName();
        try{
            scheduler.triggerJob(JobKey.jobKey(jobName));
            LogUtil.getLogger().info("Quartz内存：已立即执行任务=[{}]", jobName);
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "立即执行任务失败："+ReflectionToStringBuilder.toString(task), e);
        }
    }


    /**
     * 根据Cron表达式，获取，接下来的最近几次触发时间
     * @param numTimes The number of next fire times to produce
     * Comment by 玄玉<https://jadyer.cn/> on 2018/11/15 11:02.
     */
    public List<Date> getNextFireTimes(String cron, int numTimes) {
        if(!CronExpression.isValidExpression(cron)){
            throw new IllegalArgumentException("CronExpression不正确");
        }
        List<String> list = new ArrayList<>();
        CronTriggerImpl cronTriggerImpl = new CronTriggerImpl();
        try {
            cronTriggerImpl.setCronExpression(cron);
        } catch (ParseException e) {
            throw new SeedException(CodeEnum.SYSTEM_BUSY.getCode(), "使用表达式["+cron+"]初始化CronTrigger时出错", e);
        }
        return TriggerUtils.computeFireTimes(cronTriggerImpl, null, numTimes);
    }


    /**
     * 获取所有正在运行的Job
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
    public List<Map<String, Object>> getAllRunningJob(){
        List<Map<String, Object>> dataList = new ArrayList<>();
        try{
            List<JobExecutionContext> executingJobs = scheduler.getCurrentlyExecutingJobs();
            for(JobExecutionContext obj : executingJobs){
                JobKey jobKey = obj.getJobDetail().getKey();
                Trigger trigger = obj.getTrigger();
                Map<String, Object> dataMap = new HashMap<>();
                dataMap.put("jobName", jobKey.getName());
                dataMap.put("triggerKey", trigger.getKey().toString());
                dataMap.put("nextFireTime", trigger.getNextFireTime());
                dataMap.put("previousFireTime", trigger.getPreviousFireTime());
                dataMap.put("status", scheduler.getTriggerState(trigger.getKey()).name());
                if(trigger instanceof CronTrigger){
                    dataMap.put("cron", ((CronTrigger)trigger).getCronExpression());
                }
                dataList.add(dataMap);
            }
            return dataList;
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "获取所有正在运行的Job列表失败", e);
        }
    }


    /**
     * 获取所有计划中的Job列表
     */
    public List<Map<String, Object>> getAllJob(){
        List<Map<String, Object>> dataList = new ArrayList<>();
        try{
            Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyJobGroup());
            for(JobKey jobKey : jobKeys){
                List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
                for(Trigger trigger : triggers){
                    Map<String, Object> dataMap = new HashMap<>();
                    dataMap.put("jobName", jobKey.getName());
                    dataMap.put("triggerKey", trigger.getKey().toString());
                    dataMap.put("nextFireTime", trigger.getNextFireTime());
                    dataMap.put("previousFireTime", trigger.getPreviousFireTime());
                    dataMap.put("status", scheduler.getTriggerState(trigger.getKey()).name());
                    if(trigger instanceof CronTrigger){
                        dataMap.put("cron", ((CronTrigger)trigger).getCronExpression());
                    }
                    dataList.add(dataMap);
                }
            }
            return dataList;
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "获取所有计划中的Job列表失败", e);
        }
    }
}