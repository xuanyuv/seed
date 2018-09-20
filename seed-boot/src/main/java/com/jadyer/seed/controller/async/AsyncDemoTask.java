package com.jadyer.seed.controller.async;

import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/7 10:26.
 */
@Component
@Async
class AsyncDemoTask {
    void doTaskOne(){
        try{
            LogUtil.getLogger().info("任务一开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(1);
            LogUtil.getLogger().info("任务一执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
        }catch(Exception e){
            LogUtil.getLogger().info("任务一执行期间发生异常，堆栈轨迹如下：", e);
        }
    }

    void doTaskTwo() {
        try{
            LogUtil.getLogger().info("任务二开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(2);
            LogUtil.getLogger().info("任务二执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
            throw new RuntimeException("测试异常");
        }catch(Exception e){
            LogUtil.getLogger().info("任务二执行期间发生异常，堆栈轨迹如下：", e);
        }
    }

    void doTaskThree(){
        try{
            LogUtil.getLogger().info("任务三开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(3);
            LogUtil.getLogger().info("任务三执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
        }catch(Exception e){
            LogUtil.getLogger().info("任务三执行期间发生异常，堆栈轨迹如下：", e);
        }
    }

    @Async("seedSimpleExecutor")
    Future<String> doTaskAndGetResultOne(){
        try{
            LogUtil.getLogger().info("任务一开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(1);
            LogUtil.getLogger().info("任务一执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
            return new AsyncResult<>("任务一执行完毕");
        }catch(Exception e){
            LogUtil.getLogger().info("任务一执行期间发生异常，堆栈轨迹如下：", e);
            return new AsyncResult<>("任务一执行期间发生异常");
        }
    }

    @Async("seedSimpleExecutor")
    Future<Integer> doTaskAndGetResultTwo(){
        try{
            LogUtil.getLogger().info("任务二开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(2);
            LogUtil.getLogger().info("任务二执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
            return new AsyncResult<>(2);
        }catch(Exception e){
            LogUtil.getLogger().info("任务二执行期间发生异常，堆栈轨迹如下：", e);
            return new AsyncResult<>(-2);
        }
    }

    @Async("seedSimpleExecutor")
    Future<Long> doTaskAndGetResultThree(){
        try{
            LogUtil.getLogger().info("任务三开始执行");
            long start = System.currentTimeMillis();
            TimeUnit.SECONDS.sleep(3);
            LogUtil.getLogger().info("任务三执行完毕，耗时：" + (System.currentTimeMillis() - start) + "ms");
            return new AsyncResult<>(3L);
        }catch(Exception e){
            LogUtil.getLogger().info("任务三执行期间发生异常，堆栈轨迹如下：", e);
            return new AsyncResult<>(-3L);
        }
    }
}