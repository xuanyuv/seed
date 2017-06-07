package com.jadyer.seed.controller.async;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 使用@Async实现异步调用
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/7 10:26.
 */
@Component
public class AsyncDemoTask {
    @Async
    public void doTaskOne(){
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
    public void doTaskTwo(){
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
    public void doTaskThree(){
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
}