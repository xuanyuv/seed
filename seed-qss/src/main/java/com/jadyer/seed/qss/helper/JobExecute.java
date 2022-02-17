package com.jadyer.seed.qss.helper;

import com.jadyer.seed.comm.SpringContextHolder;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.qss.model.ScheduleLog;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleLogRepository;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class JobExecute {
    private static final int WAIT_MILLISECONDS = 6000;
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();

    ///**
    // * 通过反射调用task中的方法
    // */
    //public static void invokMethod(ScheduleTask task){
    //    Object obj = null;
    //    Class<?> clazz = null;
    //    if(StringUtils.isNotBlank(task.getBeanName())){
    //        obj = SpringContextHolder.getBean(task.getBeanName());
    //    }else if(StringUtils.isNotBlank(task.getBeanClazz())){
    //        try{
    //            clazz = Class.forName(task.getBeanClazz());
    //            obj = clazz.newInstance();
    //        }catch(Exception e){
    //            System.err.println("实例化" + task.getBeanClazz() + "失败,堆栈轨迹如下");
    //            e.printStackTrace();
    //        }
    //    }
    //    if(null == obj){
    //        System.err.println("任务[" + task.getName() +"]启动失败,目标类或目标URL不存在");
    //        return;
    //    }
    //    clazz = obj.getClass();
    //    Method method = null;
    //    try{
    //        method = clazz.getDeclaredMethod(task.getMethodName());
    //    }catch(NoSuchMethodException e){
    //        System.err.println("任务[" + task.getName() +"]启动失败,目前方法不存在");
    //        return;
    //    }
    //    if(null != method){
    //        try{
    //            method.invoke(obj);
    //            System.out.println("任务[" + task.getName() + "]启动成功...^_^...^_^...^_^...");
    //        }catch(Exception e) {
    //            System.err.println("任务[" + task.getName() +"]启动失败,目前方法调用异常,堆栈轨迹如下");
    //            e.printStackTrace();
    //        }
    //    }
    //}


    /**
     * 通过HTTP接口调用任务
     * Created by 玄玉<https://jadyer.cn/> on 2015/8/8 20:33.
     */
    static void invokMethod(ScheduleTask task){
        String jobName = task.getId() + ":" + task.getAppname() + ":" + task.getName();
        // try {
            // if(SeedLockHelper.lock(SeedLockConfiguration.redissonClientList, jobName, "seed-qss")){
                // 记录调用日志
                ScheduleLogRepository repository = SpringContextHolder.getBean(ScheduleLogRepository.class);
                ScheduleLog log = ScheduleLog.builder().taskId(task.getId()).appname(task.getAppname()).name(task.getName()).url(task.getUrl()).fireTime(new Date()).build();
                log = repository.saveAndFlush(log);
                // 开始调用
                long startTime = System.currentTimeMillis();
                String respData = HTTPUtil.post(task.getUrl().trim(), null);
                long durationTime = System.currentTimeMillis() - startTime;
                // // 服务器时间同步存在误差时的容错处理：最多容错6s，即最多允许服务器之间的时间同步误差为6s
                // if(durationTime < WAIT_MILLISECONDS){
                //     try {
                //         TimeUnit.MILLISECONDS.sleep(WAIT_MILLISECONDS - durationTime);
                //     } catch (InterruptedException e){
                //         LogUtil.getLogger().error("服务器时间误差容错处理时，遇到异常", e);
                //     }
                // }
                // 更新调用日志
                ScheduleLog finalLog = log;
                threadPool.execute(() -> {
                    finalLog.setDuration(durationTime);
                    finalLog.setRespData(respData);
                    repository.saveAndFlush(finalLog);
                });
            // }
        // } finally {
        //     SeedLockHelper.unlock();
        // }
    }
}