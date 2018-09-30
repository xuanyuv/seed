package com.jadyer.seed.boot;

import com.jadyer.seed.comm.util.LogUtil;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2018/9/26 16:49.
 */
//@Configuration
@ConditionalOnClass(Scheduler.class)
public class QuartzListenerConfiguration {
    @Resource
    private Scheduler scheduler;

    @PostConstruct
    public void bindingTriggerListener() throws SchedulerException {
        scheduler.getListenerManager().addTriggerListener(new TriggerListenerSupport() {
            @Override
            public String getName() {
                return "Seed-QSS-JobTriggerListener";
            }
            @Override
            public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
                LogUtil.getLogger().info("任务[{}]已被触发且已执行完Job，上次执行时间为：{}，下次执行时间为：{}", trigger.getJobKey().getName(), trigger.getPreviousFireTime(), trigger.getNextFireTime());
            }
            @Override
            public void triggerFired(Trigger trigger, JobExecutionContext context) {
                LogUtil.getLogger().info("任务[{}]已被触发，即将调用Job.execute()方法", trigger.getJobKey().getName());
            }
            @Override
            public void triggerMisfired(Trigger trigger) {
                LogUtil.getLogger().info("任务[{}]错过触发", trigger.getJobKey().getName());
            }
            @Override
            public boolean vetoJobExecution(Trigger trigger, JobExecutionContext context) {
                //LogUtil.getLogger().info("任务[{}]即将被执行，但此时校验业务发现资源准备不足，不便开展任务，故否决此次任务执行", trigger.getJobKey().getName());
                //return true;
                LogUtil.getLogger().info("任务[{}]即将被执行，且此时校验业务发现资源准备完毕，可以开展任务，故同意此次任务执行", trigger.getJobKey().getName());
                return super.vetoJobExecution(trigger, context);
            }
        });
    }
}