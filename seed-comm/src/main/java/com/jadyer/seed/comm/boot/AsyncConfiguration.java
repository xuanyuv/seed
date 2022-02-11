package com.jadyer.seed.comm.boot;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
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

/**
 * 使用@Async实现异步调用
 * -------------------------------------------------------------------------------------------------------------
 * Spring提供了异步执行和任务调度的抽象接口TaskExecutor和TaskScheduler，并对其实现类支持线程池和代理
 * 本工程演示的是TaskExecutor的用法
 * 对于TaskScheduler的用法，也是类似的通过@EnableScheduling启用@Scheduled(cron="")
 * 示例代码见{@link com.jadyer.seed.controller.quartz.QuartzDemo#justDoIt()}
 * -------------------------------------------------------------------------------------------------------------
 * @Configuration
 * @EnableScheduling
 * @EnableConfigurationProperties(TaskSchedulingProperties.class)
 * public class QuartzConfiguration {
 *     @Resource
 *     private TaskSchedulingProperties taskSchedulingProperties;
 *     @Bean
 *     public Executor seedQuartzScheduler(){
 *         ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
 *         scheduler.setThreadNamePrefix(taskSchedulingProperties.getThreadNamePrefix()); // 线程名称前缀
 *         scheduler.setPoolSize(taskSchedulingProperties.getPool().getSize());           // 线程池大小
 *         // scheduler.setThreadPriority(2);                                             // 线程优先级
 *         scheduler.initialize();
 *         return scheduler;
 *     }
 * }
 * -------------------------------------------------------------------------------------------------------------
 * 关于自定义线程池
 * @Async()
 *   使用的是本类中getAsyncExecutor()，若未像本类这样自定义Executor，则使用系统默认的ThreadPoolTaskExecutor
 * @Async("seedSimpleExecutor")
 *   使用的是本类中seedSimpleExecutor()
 * -------------------------------------------------------------------------------------------------------------
 * 当@Async或@Transational交叉使用或调用时，需要注意一下
 * 同一个类中，一个方法调用另外一个有注解的方法，注解是不会生效的
 * 可参考http://blog.csdn.net/clementad/article/details/47339519
 * -------------------------------------------------------------------------------------------------------------
 * <ul>
 *     <li>@Async标注的方法不能是static的，否则该方法只会被同步调用</li>
 *     <li>为使@Async生效，还要在SpringBoot启动程序中配置@EnableAsync</li>
 *     <li>当@Async标注在方法上时，该方法会异步执行，标注在类上时，该类的所有方法都会异步执行</li>
 * </ul>
 * -------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2017/6/8 14:06.
 */
@Configuration
@EnableAsync
@EnableConfigurationProperties(TaskExecutionProperties.class)
public class AsyncConfiguration extends AsyncConfigurerSupport {
    @Resource
    private TaskExecutionProperties taskExecutionProperties;

    @Bean
    public Executor seedSimpleExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("SeedSimpleExecutor-");
        executor.setCorePoolSize(10); // 线程池最小数量
        executor.setMaxPoolSize(200); // 线程池最大数量
        executor.setQueueCapacity(5); // 队列大小（最小的线程数被占满后，新任务会放进queue）
        executor.initialize();
        return executor;
    }


    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());     // 线程名称前缀
        executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());         // 线程池最大数量
        executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize());       // 线程池最小数量
        executor.setQueueCapacity(taskExecutionProperties.getPool().getQueueCapacity()); // 队列大小（最小的线程数被占满后，新任务会放进queue）
        executor.setAwaitTerminationSeconds(60 * 15);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        ////饱和策略：自定义
        //executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
        //    @Override
        //    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        //        //---------
        //    }
        //});
        //饱和策略：队列满时，使用预定义的异常处理类
        //ABORT（缺省）  ：不执行，并抛TaskRejectedException异常
        //DISCARD       ：不执行，也不抛异常，直接丢弃任务
        //DISCARD_OLDEST：丢弃queue中最旧的那个任务，并执行当期任务
        //CALLER_RUNS   ：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    /**
     * 自定义异常处理类
     * ----------------------------------------------------------------------------
     * 1.适用于异步方法无返回值（void）的情况
     * 2.对于有返回值的异步方法（Future<String>），直接在调用异步方法处捕获异常即可
     *   try {
     *       Future<String> task11 = this.asyncDemoTask.doTaskAndGetResultOne();
     *       future.get();
     *   } catch (ExecutionException e) {
     *       LogUtil.getLogger().info("异步执行时遇到异常，堆栈轨迹如下", e);
     *   }  catch (InterruptedException e) {
     *       LogUtil.getLogger().info("异步执行时发生异常，堆栈轨迹如下", e);
     *   }
     * ----------------------------------------------------------------------------
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        //noinspection Convert2Lambda
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