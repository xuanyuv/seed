package com.jadyer.seed.controller;

import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.controller.swagger.SwaggerDemoUser;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * RabbitMQ之TopicExchange发送和接收的演示
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/5 15:29.
 */
//@Controller
@RequestMapping("/mq")
public class RabbitMQController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 接收从RabbitMQ订阅的消息
     */
    //containerFactory指定的是com.jadyer.seed.boot.RabbitMQConfiguration.java里面声明的Bean
    @RabbitListener(queues="myapi.get.apply.status", containerFactory="jadyerRabbitListenerContainerFactory")
    public void get(SwaggerDemoUser user){
        System.out.println("收到从RabbitMQ订阅过来的消息-->[" + ReflectionToStringBuilder.toString(user) + "]");
    }


    /**
     * 发送一个消息到RabbitMQ
     */
    @ResponseBody
    @GetMapping("/send")
    public CommonResult send(){
        SwaggerDemoUser user = new SwaggerDemoUser(2, "玄玉", "http://jadyer.cn/");
        this.rabbitTemplate.convertAndSend("apply.status", "apply.status.1101.123", user);
        return new CommonResult();
    }
}
