package com.jadyer.seed.qss.helper;

import com.jadyer.seed.comm.SeedLockHelper;
import com.jadyer.seed.comm.SpringContextHolder;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.boot.SeedLockConfiguration;
import com.jadyer.seed.qss.model.ScheduleLog;
import com.jadyer.seed.qss.model.ScheduleTask;
import com.jadyer.seed.qss.repository.ScheduleLogRepository;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class JobExecute {
    private static ExecutorService threadPool = Executors.newCachedThreadPool();

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
     * Created by 玄玉<http://jadyer.cn/> on 2015/8/8 20:33.
     */
    static void invokMethod(ScheduleTask task){
        try {
            if(SeedLockHelper.lock(SeedLockConfiguration.redissonClientList, task.getJobname())){
                //单起线程记录调用请求
                ScheduleLogRepository repository = SpringContextHolder.getBean(ScheduleLogRepository.class);
                ScheduleLog log = new ScheduleLog();
                log.setTaskId(task.getId());
                log.setAppname(task.getAppname());
                log.setName(task.getName());
                log.setUrl(task.getUrl());
                log.setFireTime(new Date());
                log = repository.saveAndFlush(log);
                //正式调用
                long startTime = System.currentTimeMillis();
                LogUtil.getLogger().info("start-->定时任务：[{}]=[{}]", task.getJobname(), task.getUrl());
                String respData = HTTPUtil.post(task.getUrl(), null);
                LogUtil.getLogger().info("stopp-->定时任务：[{}]=[{}]，return=[{}]", task.getJobname(), task.getUrl(), respData);
                //更新耗时及应答结果
                ScheduleLog finalLog = log;
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        finalLog.setDuration(System.currentTimeMillis() - startTime);
                        finalLog.setRespData(respData);
                        repository.saveAndFlush(finalLog);
                    }
                });
            }
        } finally {
            SeedLockHelper.unlock();
        }
    }
}