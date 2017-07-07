package com.jadyer.seed.mpp.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
public class AsyncConfiguration {
    @Value("${spring.async.corePoolSize}")
    private int corePoolSize;
    @Value("${spring.async.maxPoolSize}")
    private int maxPoolSize;
    @Value("${spring.async.queueCapacity}")
    private int queueCapacity;

    @Bean
    public Executor mppExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("MppExecutor-");
        executor.setCorePoolSize(this.corePoolSize);
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setQueueCapacity(this.queueCapacity);
        //队列满的时候，使用的拒绝策略
        //ABORT（缺省）  ：不执行，并抛TaskRejectedException异常
        //DISCARD       ：不执行，也不抛异常
        //DISCARD_OLDEST：丢弃queue中最旧的那个任务
        //CALLER_RUNS   ：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}