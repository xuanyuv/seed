package com.jadyer.seed.controller.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.util.LogUtil;
import com.rabbitmq.client.Channel;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 接收从RabbitMQ订阅的消息
 * Created by 玄玉<https://jadyer.cn/> on 2018/3/23 12:11.
 */
@Component
public class ReceiveService {
    @RabbitListener(queues="myapi.get.apply.status02", containerFactory="jadyerRabbitListenerContainerFactory")
    public void receive02(UserMsg userMsg){
        LogUtil.getLogger().info("收到消息02-->[{}]", ReflectionToStringBuilder.toString(userMsg));
    }


    //containerFactory指定的是com.jadyer.seed.boot.RabbitMQConfiguration.java里面声明的Bean
    @RabbitListener(queues="${spring.rabbitmq.queues}", containerFactory="jadyerRabbitListenerContainerFactory")
    public void receive(UserMsg userMsg, Channel channel, Message message){
        try {
            LogUtil.getLogger().info("收到消息-->[{}]", ReflectionToStringBuilder.toString(userMsg));
            //确认消费成功（第一个参数：消息编号。第二个参数：是否确认多条消息，false为确认当前消息，true为确认deliveryTag编号以前的所有消息）
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }catch(Exception e){
            LogUtil.getLogger().error("消息处理异常，消息ID={}, 消息体=[{}]", message.getMessageProperties().getCorrelationIdString(), JSON.toJSONString(userMsg), e);
            try {
                //拒绝当前消息，并把消息返回原队列（第三个参数：是否将消息放回队列，true表示放回队列，false表示丢弃消息）
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                //basicReject只能拒绝一条消息，而basicNack能够拒绝多条消息
                //channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
            } catch (IOException e1) {
                LogUtil.getLogger().error("消息basicNack时发生异常，消息ID={}", message.getMessageProperties().getCorrelationIdString(), e);
            }
        }
    }
}