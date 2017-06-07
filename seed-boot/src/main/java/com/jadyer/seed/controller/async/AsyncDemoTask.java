package com.jadyer.seed.controller.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 使用@Async实现异步调用（为使之生效，还要在SpringBoot启动程序中配置@EnableAsync）
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/7 10:26.
 */
@Component
class AsyncDemoTask {
    @Async
    void doTaskOne(){
        try{
            System.out.println("任务一开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(1);
            System.out.println("任务一执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
        }catch(Exception e){
            System.out.println("任务一执行期间发生异常，堆栈轨迹如下：");
            e.printStackTrace();
        }
    }

    @Async
    void doTaskTwo(){
        try{
            System.out.println("任务二开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(2);
            System.out.println("任务二执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
        }catch(Exception e){
            System.out.println("任务二执行期间发生异常，堆栈轨迹如下：");
            e.printStackTrace();
        }
    }

    @Async
    void doTaskThree(){
        try{
            System.out.println("任务三开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(3);
            System.out.println("任务三执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
        }catch(Exception e){
            System.out.println("任务三执行期间发生异常，堆栈轨迹如下：");
            e.printStackTrace();
        }
    }

    @Async
    Future<String> doTaskAndGetResultOne(){
        try{
            System.out.println("任务一开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(1);
            System.out.println("任务一执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
            return new AsyncResult<>("任务一执行完毕");
        }catch(Exception e){
            System.out.println("任务一执行期间发生异常，堆栈轨迹如下：");
            e.printStackTrace();
            return new AsyncResult<>("任务一执行期间发生异常");
        }
    }

    @Async
    Future<Integer> doTaskAndGetResultTwo(){
        try{
            System.out.println("任务二开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(2);
            System.out.println("任务二执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
            return new AsyncResult<>(2);
        }catch(Exception e){
            System.out.println("任务二执行期间发生异常，堆栈轨迹如下：");
            e.printStackTrace();
            return new AsyncResult<>(-2);
        }
    }

    @Async
    Future<Long> doTaskAndGetResultThree(){
        try{
            System.out.println("任务三开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(3);
            System.out.println("任务三执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
            return new AsyncResult<>(3L);
        }catch(Exception e){
            System.out.println("任务三执行期间发生异常，堆栈轨迹如下：");
            e.printStackTrace();
            return new AsyncResult<>(-3L);
        }
    }
}