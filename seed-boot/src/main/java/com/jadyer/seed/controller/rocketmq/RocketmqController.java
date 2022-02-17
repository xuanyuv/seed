package com.jadyer.seed.controller.rocketmq;

import com.jadyer.seed.comm.constant.CommResult;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2021/2/24 18:57.
 */
@RestController
@RequestMapping("/mq")
public class RocketmqController {
    @Value("${rocketmq.producer.topic}")
    private String topic;
    @Value("${rocketmq.producer.send-message-timeout}")
    private Integer messageTimeOut;
    @Resource
    private RocketMQTemplate rocketMQTemplate;

    @GetMapping("/sendmsg")
    public CommResult<SendResult> sendmsg(){
        // 发送普通消息（convertAndSend()方法没有返回值）
        SendResult sendResult = rocketMQTemplate.syncSend(topic, "hello jadyer rkmq");

        // 发送带tag的消息（直接在topic后面加上":tag"）
        // sendResult = rocketMQTemplate.syncSend("queue_test_topic:tag1", "hello jadyer rkmq");

        // 发送延时消息（start版本中，延时消息一共分为18个等级分别为：1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h）
        // rocketMQTemplate.sendDelayed("test-topic-1", "I'm delayed message", MessageDelayLevel.TIME_1M);
        // sendResult = rocketMQTemplate.syncSend(topic, MessageBuilder.withPayload("hello jadyer rkmq").build(), messageTimeOut, 1);

        // 发送异步消息（SendCallback中可处理相关成功失败时的逻辑）
        // rocketMQTemplate.asyncSend(topic, "hello jadyer rkmq", new SendCallback() {
        //     @Override
        //     public void onSuccess(SendResult sendResult) {
        //         LogUtil.getLogger().info("消息发送成功，发送结果={}", JSON.toJSONString(sendResult));
        //     }
        //     @Override
        //     public void onException(Throwable e) {
        //         LogUtil.getLogger().info("消息发送失败，堆栈轨迹如下", e);
        //     }
        // });

        // 发送顺序消息
        // sendResult = rocketMQTemplate.syncSendOrderly(topic, "hello jadyer rkmq", "1234");

        return CommResult.success(sendResult);
    }
}