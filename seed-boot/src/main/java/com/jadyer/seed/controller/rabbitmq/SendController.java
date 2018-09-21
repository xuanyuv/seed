package com.jadyer.seed.controller.rabbitmq;

import com.jadyer.seed.comm.constant.CommResult;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
@RequestMapping("/send")
public class SendController {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送一个消息到RabbitMQ
     * Comment by 玄玉<https://jadyer.cn/> on 2018/3/23 14:53.
     */
    @ResponseBody
    @GetMapping("")
    public CommResult send(){
        int id = 2;
        UserMsg userMsg = new UserMsg(id, "玄玉", "https://jadyer.cn/");
        this.rabbitTemplate.convertAndSend("apply.status", "apply.status.1101.123", userMsg, new CorrelationData(id+""));
        return CommResult.success();
    }
}