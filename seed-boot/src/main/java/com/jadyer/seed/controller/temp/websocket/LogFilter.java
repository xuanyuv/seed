package com.jadyer.seed.controller.temp.websocket;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import java.text.DateFormat;
import java.util.Date;

/**
 * 定义Logfilter拦截输出日志（获取logback的日志，塞入日志队列中）
 * Created by 玄玉<http://jadyer.cn/> on 2018/2/13 17:00.
 */
public class LogFilter extends Filter<ILoggingEvent> {
    @Override
    public FilterReply decide(ILoggingEvent event) {
        LogMsg msg = new LogMsg();
        msg.setBody(event.getMessage());
        msg.setLevel(event.getLevel().levelStr);
        msg.setTimestamp(DateFormat.getDateTimeInstance().format(new Date(event.getTimeStamp())));
        msg.setClassName(event.getLoggerName());
        msg.setThreadName(event.getThreadName());
        LogQueue.getInstance().push(msg);
        return FilterReply.ACCEPT;
    }
}