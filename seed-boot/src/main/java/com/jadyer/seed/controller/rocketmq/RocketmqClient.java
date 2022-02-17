package com.jadyer.seed.controller.rocketmq;

import com.jadyer.seed.comm.util.LogUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * --------------------------------------------------------------------------------
 * <dependency>
 *     <groupId>org.apache.rocketmq</groupId>
 *     <artifactId>rocketmq-spring-boot-starter</artifactId>
 *     <version>2.2.1</version>
 * </dependency>
 * --------------------------------------------------------------------------------
 * rocketmq:
 *   # name-server: http://xxxxx.mq-internet-access.mq-internet.aliyuncs.com:80
 *   name-server: 127.0.0.1:9876;127.0.0.2:9876
 *   producer:
 *     # access-key: your aliyun accessKey
 *     # secret-key: your aliyun secretKey
 *     group: JadyerGroup
 *     topic: jadyer_test_topic
 *     send-message-timeout: 5000
 *     retry-times-when-send-failed: 2
 *     max-message-size: 4194304
 *   consumer:
 *     group: ${rocketmq.producer.group}
 *     topic: ${rocketmq.producer.topic}
 * --------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2021/2/24 19:00.
 */
@Component
@RocketMQMessageListener(topic="${rocketmq.consumer.topic}", consumerGroup="${rocketmq.consumer.group}")
public class RocketmqClient implements RocketMQListener<String> {
    @Override
    public void onMessage(String message) {
        LogUtil.getLogger().info("收到MQ消息：{}", message);
    }
}