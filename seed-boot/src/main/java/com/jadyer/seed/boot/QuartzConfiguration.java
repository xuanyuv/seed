package com.jadyer.seed.boot;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2017/3/6 17:25.
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

    /**
     * 构造Quartz属性
     */
    private Properties buildQuartzProperties(){
        Properties prop = new Properties();
        prop.put("quartz.scheduler.instanceName", "SeedQSSServerScheduler");
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
        //prop.put("org.quartz.threadPool.threadCount", "5");
        //prop.put("org.quartz.dataSource.quartzDataSource.driver", "com.mysql.jdbc.Driver");
        //prop.put("org.quartz.dataSource.quartzDataSource.URL", "jdbc:mysql://localhost:3306/demo-schema");
        //prop.put("org.quartz.dataSource.quartzDataSource.user", "root");
        //prop.put("org.quartz.dataSource.quartzDataSource.password", "123456");
        //prop.put("org.quartz.dataSource.quartzDataSource.maxConnections", "10");
        return prop;
    }

    @Bean
    public Scheduler getScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory(this.buildQuartzProperties());
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        return scheduler;
    }
}