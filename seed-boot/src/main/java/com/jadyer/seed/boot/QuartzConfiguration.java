package com.jadyer.seed.boot;

import com.jadyer.seed.comm.util.LogUtil;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.listeners.TriggerListenerSupport;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/6 17:25.
 */
//@Configuration
public class QuartzConfiguration {
    ///**
    // * 也可引入spring-context-support.jar
    // * 然后返回FactoryBean<Scheduler>的实例
    // * 调用时一样可以直接注入并使用org.quartz.Scheduler.java
    // */
    //@Bean
    //public FactoryBean<Scheduler> getSchedulerFactoryBean(){
    //    return new SchedulerFactoryBean();
    //}

    @Bean
    public Scheduler getScheduler() throws SchedulerException {
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", "SeedBootScheduler");
        prop.put("org.quartz.threadPool.threadCount", "5");
        //prop.put("org.quartz.scheduler.instanceId", "AUTO");
        //prop.put("org.quartz.scheduler.skipUpdateCheck", "true");
        //prop.put("org.quartz.scheduler.instanceId", "NON_CLUSTERED");
        //prop.put("org.quartz.scheduler.jobFactory.class", "org.quartz.simpl.SimpleJobFactory");
        //prop.put("org.quartz.jobStore.class", "org.quartz.impl.jdbcjobstore.JobStoreTX");
        //prop.put("org.quartz.jobStore.driverDelegateClass", "org.quartz.impl.jdbcjobstore.StdJDBCDelegate");
        //prop.put("org.quartz.jobStore.dataSource", "quartzDataSource");
        //prop.put("org.quartz.jobStore.tablePrefix", "QRTZ_");
        //prop.put("org.quartz.jobStore.isClustered", "true");
        //prop.put("org.quartz.threadPool.class", "org.quartz.simpl.SimpleThreadPool");
        //prop.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.jdbc.Driver");
        //prop.put("org.quartz.dataSource.quartzDataSource.URL", "jdbc:mysql://localhost:3306/demo-schema");
        //prop.put("org.quartz.dataSource.quartzDataSource.user", "root");
        //prop.put("org.quartz.dataSource.quartzDataSource.password", "123456");
        //prop.put("org.quartz.dataSource.quartzDataSource.maxConnections", "10");
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
            }
        });
        scheduler.start();
        return scheduler;
    }
}