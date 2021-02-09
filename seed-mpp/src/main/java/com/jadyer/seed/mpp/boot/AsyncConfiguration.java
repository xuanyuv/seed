package com.jadyer.seed.mpp.boot;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableConfigurationProperties(TaskExecutionProperties.class)
public class AsyncConfiguration extends AsyncConfigurerSupport {
    @Resource
    private TaskExecutionProperties taskExecutionProperties;

    @Override
    public Executor getAsyncExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());     // 线程名称前缀
        executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());         // 线程池最大数量
        executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize());       // 线程池最小数量
        executor.setQueueCapacity(taskExecutionProperties.getPool().getQueueCapacity()); // 队列大小（最小的线程数被占满后，新任务会放进queue）
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler(){
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                LogUtil.getLogger().info("异步执行" + method.getDeclaringClass().getSimpleName() + "." + method.getName() + "()方法时发生异常\n" +
                        "方法参数=" + JSON.toJSONString(params) + "\n" +
                        "堆栈轨迹如下", ex);
            }
        };
    }
}