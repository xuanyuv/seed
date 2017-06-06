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
 * ----------------------------------------------------------------------------------------------------
 * 下面是几个SpringBoot整合RabbitMQ的例子，个人觉得要么太单一，要么太复杂
 * https://segmentfault.com/a/1190000004401870
 * http://blog.didispace.com/spring-boot-rabbitmq/
 * http://www.ityouknow.com/springboot/2016/11/30/springboot(八)-RabbitMQ详解.html
 * ----------------------------------------------------------------------------------------------------
 * 测试时，先在RabbitMQ管理界面做如下准备工作
 * 1.Admin菜单下，新建Users：xuanyu
 * 2.Admin菜单下，新建Virtual Hosts：myvhost，并设置用户xuanyu可以访问myvhost
 * 3.Exchanges菜单下，新建Exchanges：apply.status，并让它在myvhost下面
 * 4.Queues菜单下，新建Queues：myapi.get.apply.status，并让它在myvhost下面
 * 5.进入myapi.get.apply.status队列，在Bind处设定消息路由规则
 *   这里是：From exchange=apply.status，Routing key=apply.status.1101.*
 * 这样一来，发送消息时就会根据routingKey通过上面设定的“apply.status.1101.*”规则把消息路由到指定的队列
 * 而接收方只需要指定从哪个队列接收即可（注：同一消息可以根据不同的Routing key被发到多个队列）
 * ----------------------------------------------------------------------------------------------------
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
    public void receive(SwaggerDemoUser user){
        System.out.println("收到从RabbitMQ订阅过来的消息-->[" + ReflectionToStringBuilder.toString(user) + "]");
    }
    @RabbitListener(queues="myapi.get.apply.status22", containerFactory="jadyerRabbitListenerContainerFactory")
    public void receive22(SwaggerDemoUser user){
        System.out.println("收到从RabbitMQ订阅过来的消息22-->[" + ReflectionToStringBuilder.toString(user) + "]");
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
