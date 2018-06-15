package com.jadyer.seed.qss.helper;

import com.jadyer.seed.comm.annotation.SeedLock;
import com.jadyer.seed.comm.util.HTTPUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.model.ScheduleTask;

class JobExecute {
//    /**
//     * 通过反射调用task中的方法
//     */
//    public static void invokMethod(ScheduleTask task){
//        Object obj = null;
//        Class<?> clazz = null;
//        if(StringUtils.isNotBlank(task.getBeanName())){
//            obj = SpringContextHolder.getBean(task.getBeanName());
//        }else if(StringUtils.isNotBlank(task.getBeanClazz())){
//            try{
//                clazz = Class.forName(task.getBeanClazz());
//                obj = clazz.newInstance();
//            }catch(Exception e){
//                System.err.println("实例化" + task.getBeanClazz() + "失败,堆栈轨迹如下");
//                e.printStackTrace();
//            }
//        }
//        if(null == obj){
//            System.err.println("任务[" + task.getName() +"]启动失败,目标类或目标URL不存在");
//            return;
//        }
//        clazz = obj.getClass();
//        Method method = null;
//        try{
//            method = clazz.getDeclaredMethod(task.getMethodName());
//        }catch(NoSuchMethodException e){
//            System.err.println("任务[" + task.getName() +"]启动失败,目前方法不存在");
//            return;
//        }
//        if(null != method){
//            try{
//                method.invoke(obj);
//                System.out.println("任务[" + task.getName() + "]启动成功...^_^...^_^...^_^...");
//            }catch(Exception e) {
//                System.err.println("任务[" + task.getName() +"]启动失败,目前方法调用异常,堆栈轨迹如下");
//                e.printStackTrace();
//            }
//        }
//    }

    /**
     * 通过HTTP接口调用任务
     * Created by 玄玉<http://jadyer.cn/> on 2015/8/8 20:33.
     */
    @SeedLock(key="#task.jobname")
    // TODO 想办法生效
    static void invokMethod(ScheduleTask task){
        LogUtil.getLogger().info("start-->定时任务：[{}]=[{}]", task.getJobname(), task.getUrl());
        String respData = HTTPUtil.post(task.getUrl(), null);
        LogUtil.getLogger().info("stopp-->定时任务：[{}]=[{}]，return=[{}]", task.getJobname(), task.getUrl(), respData);
    }
}