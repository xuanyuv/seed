package com.jadyer.seed.controller.temp.websocket;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 创建一个阻塞队列，作为日志系统输出的日志的一个临时载体
 * Created by 玄玉<https://jadyer.cn/> on 2018/2/13 16:51.
 */
public class LogQueue {
    //队列大小
    public static final int QUEUE_MAX_SIZE = 10000;
    //阻塞队列
    private BlockingQueue blockingQueue = new LinkedBlockingQueue<>(QUEUE_MAX_SIZE);

    private static LogQueue alarmMsgQueue = new LogQueue();
    private LogQueue() {}
    public static LogQueue getInstance() {
        return alarmMsgQueue;
    }


    /**
     * 消息入队
     */
    public boolean push(LogMsg msg){
        //队列满了就抛出异常，不阻塞
        return this.blockingQueue.add(msg);
    }


    /**
     * 消息出队
     */
    public LogMsg pull(){
        LogMsg msg = null;
        try {
            msg = (LogMsg) this.blockingQueue.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return msg;
    }
}