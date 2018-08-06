package com.jadyer.seed.boot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 使用@Async实现异步调用
 * -----------------------------------------------------------------------------------------------------
 * Spring提供了异步执行和任务调度的抽象接口TaskExecutor和TaskScheduler，并对其实现类支持线程池和代理
 * 本工程演示的是TaskExecutor的用法
 * 对于TaskScheduler的用法，也是类似的通过@EnableScheduling启用@Scheduled(cron="")
 * 示例代码见{@link com.jadyer.seed.controller.quartz.QuartzDemo#justDoIt()}
 * -----------------------------------------------------------------------------------------------------
 * 关于自定义线程池
 * 如果没有像本类中那样自定义Executor，那么就会使用系统默认的ThreadPoolTaskExecutor
 * 同样@Async()注解也就不用指定线程池了，直接写成“@Async()”就可以了
 * -----------------------------------------------------------------------------------------------------
 * 当@Async或@Transational交叉使用或调用时，需要注意一下
 * 同一个类中，一个方法调用另外一个有注解的方法，注解是不会生效的
 * 可参考http://blog.csdn.net/clementad/article/details/47339519
 * -----------------------------------------------------------------------------------------------------
 * <ul>
 *     <li>@Async标注的方法不能是static的，否则该方法只会被同步调用</li>
 *     <li>为使@Async生效，还要在SpringBoot启动程序中配置@EnableAsync</li>
 *     <li>当@Async标注在方法上时，该方法会异步执行，标注在类上时，该类的所有方法都会异步执行</li>
 * </ul>
 * -----------------------------------------------------------------------------------------------------
 * 【待测试】
 * 还有一种写法：本类直接继承org.springframework.scheduling.annotation.AsyncConfigurer再实现其方法即可
 * -----------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/8 14:06.
 */
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
    public Executor mySimpleExecutor(){
        //ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        //scheduler.setThreadNamePrefix("MyScheduler-"); //线程名称前缀
        //scheduler.setPoolSize(1000);                   //线程池大小
        //scheduler.setThreadPriority(2);                //线程优先级
        //scheduler.initialize();
        //return scheduler;
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("MySimpleExecutor-");
        executor.setCorePoolSize(10); //线程池最小数量
        executor.setMaxPoolSize(200); //线程池最大数量
        executor.setQueueCapacity(5); //队列大小（最小的线程数被占满后，新任务会放进queue）
        executor.initialize();
        return executor;
    }


    @Bean
    public Executor myExecutor(){
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("MyExecutor-");
        executor.setMaxPoolSize(this.maxPoolSize);
        executor.setCorePoolSize(this.corePoolSize);
        executor.setQueueCapacity(this.queueCapacity);
        executor.setAwaitTerminationSeconds(60 * 15);
        executor.setWaitForTasksToCompleteOnShutdown(true);
        ////拒绝策略：自定义
        //executor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
        //    @Override
        //    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        //        //---------
        //    }
        //});
        //拒绝策略：队列满时，使用预定义的异常处理类
        //ABORT（缺省）  ：不执行，并抛TaskRejectedException异常
        //DISCARD       ：不执行，也不抛异常
        //DISCARD_OLDEST：丢弃queue中最旧的那个任务
        //CALLER_RUNS   ：不在新线程中执行任务，而是由调用者所在的线程来执行
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }


    ///**
    // * 自定义异常处理类
    // * ----------------------------------------------------------------------------
    // * 1.适用于异步方法无返回值（void）的情况
    // * 2.对于有返回值的异步方法（Future<String>），直接在调用异步方法处捕获异常即可
    // *   try {
    // *       Future<String> task11 = this.asyncDemoTask.doTaskAndGetResultOne();
    // *       future.get();
    // *   } catch (ExecutionException e) {
    // *       LogUtil.getLogger().info("异步执行时遇到异常，堆栈轨迹如下", e);
    // *   }  catch (InterruptedException e) {
    // *       LogUtil.getLogger().info("异步执行时发生异常，堆栈轨迹如下", e);
    // *   }
    // * ----------------------------------------------------------------------------
    // */
    //@Bean
    //public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    //    return new AsyncUncaughtExceptionHandler(){
    //        @Override
    //        public void handleUncaughtException(Throwable ex, Method method, Object... params) {
    //            LogUtil.getLogger().info("异步执行["+method.getName()+"]方法时发生异常，堆栈轨迹如下", ex);
    //            for(Object obj : params){
    //                LogUtil.getLogger().info("方法参数值为：[]" + obj);
    //            }
    //        }
    //    };
    //}
}