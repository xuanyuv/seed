package com.jadyer.seed.qss.helper;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.log.annotation.EnableLog;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.qss.QssService;
import com.jadyer.seed.qss.boot.QssRun;
import com.jadyer.seed.qss.model.ScheduleTask;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.Resource;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2018/6/13 18:59.
 */
@Component
public class JobSubscriber extends JedisPubSub {
    @Resource
    private QssService qssService;

    @Override
    @EnableLog
    public void onMessage(String channel, String message) {
        LogUtil.getLogger().info("收到来自渠道[{}]的消息[{}]", channel, message);
        if(QssRun.CHANNEL_SUBSCRIBER.equals(channel)){
            ScheduleTask task = JSON.parseObject(message, ScheduleTask.class);
            qssService.upsertJob(task);
        }
    }
}