package com.jadyer.seed.comm.exception;

import com.jadyer.seed.comm.constant.CodeEnum;

/**
 * 关于构造方法是否增加Throwable参数的区别
 * -----------------------------------------------------------------------------------------------------------
 * 总结：增加Throwable参数可以在打印异常堆栈轨迹日志时，打印异常的堆栈轨迹（也就是Caused by...）
 * -----------------------------------------------------------------------------------------------------------
 * try{
 *         CronScheduleBuilder.cronSchedule(task.getCron());
 * }catch(Exception e){
 *         //throw new IllegalArgumentException("CronExpression不正确");
 *         throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "CronExpression不正确");
 * }
 * 上面这段代码最终得到的异常堆栈轨迹日志如下
 * [20150828 10:35:54][qtp308855416-18][GlobalExceptionHandler.process]Exception Occured URL=http://127.0.0.1:8088/engine/quartz/schedule/task/add,堆栈轨迹如下
 * //java.lang.IllegalArgumentException: CronExpression不正确
 * com.jadyer.engine.common.exception.EngineException: CronExpression不正确
 *     at com.jadyer.engine.quartz.ScheduleTaskService.addTask(ScheduleTaskService.java:103)
 *     at com.jadyer.engine.quartz.ScheduleTaskController.add(ScheduleTaskController.java:37)
 *     at com.jadyer.engine.quartz.ScheduleTaskController$$FastClassBySpringCGLIB$$d11639c7.invoke(<generated>)
 *     ......
 *     at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:608)
 *     at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:543)
 *     at java.lang.Thread.run(Thread.java:745)
 * [20150828 10:35:54][qtp308855416-18][AbstractHandlerExceptionResolver.logException]Handler execution resulted in exception: CronExpression不正确
 * -----------------------------------------------------------------------------------------------------------
 * try{
 *         CronScheduleBuilder.cronSchedule(task.getCron());
 * }catch(Exception e){
 *         //throw new IllegalArgumentException("CronExpression不正确");
 *         throw new SeedException(CodeEnum.SYSTEM_ERROR.getCode(), "CronExpression不正确", e);
 * }
 * 上面这段代码最终得到的异常堆栈轨迹日志如下
 * [20150828 10:34:59][qtp594160358-16][GlobalExceptionHandler.process]Exception Occured URL=http://127.0.0.1:8088/engine/quartz/schedule/task/add,堆栈轨迹如下
 * com.jadyer.engine.common.exception.EngineException: CronExpression不正确
 *     at com.jadyer.engine.quartz.ScheduleTaskService.addTask(ScheduleTaskService.java:104)
 *     at com.jadyer.engine.quartz.ScheduleTaskController.add(ScheduleTaskController.java:37)
 *     at com.jadyer.engine.quartz.ScheduleTaskController$$FastClassBySpringCGLIB$$d11639c7.invoke(<generated>)
 *     ......
 *     at org.eclipse.jetty.util.thread.QueuedThreadPool.runJob(QueuedThreadPool.java:608)
 *     at org.eclipse.jetty.util.thread.QueuedThreadPool$3.run(QueuedThreadPool.java:543)
 *     at java.lang.Thread.run(Thread.java:745)
 * Caused by: java.lang.RuntimeException: CronExpression 'testcron' is invalid.
 *     at org.quartz.CronScheduleBuilder.cronSchedule(CronScheduleBuilder.java:111)
 *     at com.jadyer.engine.quartz.ScheduleTaskService.addTask(ScheduleTaskService.java:101)
 *     ... 67 more
 * Caused by: java.text.ParseException: Illegal characters for this position: 'TES'
 *     at org.quartz.CronExpression.storeExpressionVals(CronExpression.java:588)
 * @see        at org.quartz.CronExpression.buildExpression(CronExpression.java:487)
 *     at org.quartz.CronExpression.<init>(CronExpression.java:276)
 *     at org.quartz.CronScheduleBuilder.cronSchedule(CronScheduleBuilder.java:107)
 *     ... 68 more
 * [20150828 10:34:59][qtp594160358-16][AbstractHandlerExceptionResolver.logException]Handler execution resulted in exception: CronExpression不正确
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2015/8/28 10:37.
 */
public class SeedException extends RuntimeException {
    private static final long serialVersionUID = 601366631919634564L;
    private int code;
    private String message;

    //public SeedException format(Object... messages){
    //    if(null!=messages && messages.length>0){
    //        this.message = String.format(this.message, messages);
    //    }
    //    return this;
    //}

    public SeedException(CodeEnum codeEnum){
        super(codeEnum.getMsg());
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMsg();
    }

    public SeedException(CodeEnum codeEnum, Throwable cause){
        super(codeEnum.getMsg(), cause);
        this.code = codeEnum.getCode();
        this.message = codeEnum.getMsg();
    }

    public SeedException(int code, String message){
        super(message);
        this.code = code;
        this.message = message;
    }

    public SeedException(int code, String message, Throwable cause){
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}