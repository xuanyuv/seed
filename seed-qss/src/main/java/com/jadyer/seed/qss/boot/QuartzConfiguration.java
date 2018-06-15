package com.jadyer.seed.qss.boot;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.repository.ScheduleTaskRepository;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.Properties;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/6 17:25.
 */
@Configuration
public class QuartzConfiguration {
    @Resource
    private ScheduleTaskRepository scheduleTaskRepository;

    @Bean
    public Scheduler getScheduler() throws SchedulerException {
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", "SeedQSSScheduler");
        prop.put("org.quartz.threadPool.threadCount", "5");
        SchedulerFactory schedulerFactory = new StdSchedulerFactory(prop);
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.getListenerManager().addTriggerListener(new TriggerListenerSupport() {
            @Override
            public String getName() {
                return "Seed-QSS-JobTriggerListener";
            }
            /**
             * 任务完成时触发
             */
            @Override
            public void triggerComplete(Trigger trigger, JobExecutionContext context, Trigger.CompletedExecutionInstruction triggerInstructionCode) {
                LogUtil.getLogger().info("任务[{}]被执行，上次执行时间为：{}，下次执行时间为：{}", trigger.getJobKey().getName(), trigger.getPreviousFireTime(), trigger.getNextFireTime());
                if(1 != scheduleTaskRepository.updateTriggerTimeById(trigger.getNextFireTime(), trigger.getPreviousFireTime(), Long.parseLong(trigger.getJobKey().getName().split(":")[0]))){
                    LogUtil.getLogger().error("任务[{}]执行时间落库失败", trigger.getJobKey().getName());
                }
            }
        });
        scheduler.start();
        return scheduler;
    }
}