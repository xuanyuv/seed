package com.jadyer.seed.controller.temp.websocket;

/**
 * 日志消息实体
 * Created by 玄玉<https://jadyer.cn/> on 2018/2/13 16:49.
 */
public class LogMsg {
    private String body;
    private String level;
    private String timestamp;
    //private String loggerName;
    private String className;
    private String threadName;

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }
}