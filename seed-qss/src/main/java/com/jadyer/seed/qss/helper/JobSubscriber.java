package com.jadyer.seed.qss.helper;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.annotation.log.EnableLog;
import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.exception.SeedException;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.model.ScheduleTask;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.Resource;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/6/13 18:59.
 */
@Component
public class JobSubscriber extends JedisPubSub {
    @Resource
    private Scheduler scheduler;

    @Override
    @EnableLog
    public void onMessage(String channel, String message) {
        LogUtil.getLogger().info("收到来自渠道[{}]的订阅消息[{}]", channel, message);
        if(SeedConstants.CHANNEL_SUBSCRIBER.equals(channel)){
            this.upsertJob(JSON.parseObject(message, ScheduleTask.class));
        }
    }


    /**
     * 添加／更新／删除QuartzJob（目前仅供JobSubscriber.onMessage()调用）
     */
    private void upsertJob(ScheduleTask task){
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
                LogUtil.getLogger().info("Quartz内存：已移除任务[{}]", task.getJobname());
                return;
            }
            TriggerKey triggerKey = TriggerKey.triggerKey(task.getJobname());
            CronTrigger trigger = (CronTrigger)scheduler.getTrigger(triggerKey);
            if(null != trigger){
                if(task.getCron().equals(trigger.getCronExpression())){
                    LogUtil.getLogger().info("Quartz内存：相同的Cron故无需更新任务[{}]", task.getJobname());
                    return;
                }
                //withMisfireHandlingInstructionDoNothing不触发立即执行，等待下次Cron触发时再开始按频率执行
                CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(task.getCron()).withMisfireHandlingInstructionDoNothing();
                //按新的cronExpression表达式重新构建Trigger
                trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
                //按新的Trigger重新设置Job执行
                scheduler.rescheduleJob(triggerKey, trigger);
                LogUtil.getLogger().info("Quartz内存：已更新任务[{}]", task.getJobname());
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
                LogUtil.getLogger().info("Quartz内存：已新增任务[{}]", task.getJobname());
            }
        }catch(SchedulerException e){
            throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "UpsertJob失败："+ReflectionToStringBuilder.toString(task), e);
        }
    }
}