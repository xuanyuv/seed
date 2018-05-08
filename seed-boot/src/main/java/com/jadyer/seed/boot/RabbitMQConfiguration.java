package com.jadyer.seed.boot;

import com.alibaba.fastjson.JSON;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.LogUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ之TopicExchange发送和接收的演示
 * ----------------------------------------------------------------------------------------------------
 * 解决方法：No method found for class [B
 * https://jira.spring.io/browse/AMQP-573
 * http://www.cnblogs.com/lazio10000/p/5559999.html
 * 解决方法：ServletRequestAttributes==null
 * 自定义日志切面时，比如com.jadyer.seed.comm.base.LogAspectOld.java，若用到了ServletRequestAttributes
 * 则需判断是否为null，因为RabbitMq在发送消息给订阅者时，日志切面所拦截到的ServletRequestAttributes==null
 * ----------------------------------------------------------------------------------------------------
 * 测试时，先在RabbitMQ管理界面做如下准备工作
 * 1、Admin菜单下，新建Users：xuanyu
 * 2、Admin菜单下，新建Virtual Hosts：myvhost，并设置用户xuanyu可以访问myvhost
 * 3、Exchanges菜单下，新建Exchanges：apply.status，且设置Type=topic，并让它在myvhost下面
 * 4、Queues菜单下，新建Queues：myapi.get.apply.status，并让它在myvhost下面
 * 5、进入myapi.get.apply.status队列，然后在Bindings处设定消息路由规则
 *    From exchange=apply.status，Routing key=apply.status.1101.*
 * 消息是先被发送到交换器（Exchange），然后交换器再根据路由键（RoutingKey）把消息投递到对应的队列（Queue）
 * 而接收方只需要指定从队列接收消息即可（注：同一消息可以根据不同的Routingkey被投递到多个队列）
 * ----------------------------------------------------------------------------------------------------
 * 关于消息确认机制（acknowledgments）
 * 1、多个消费者监听同一个queue时，默认的rabbitmq会采用round-robin（轮流）的方式，每条消息只会发给一个消费者
 * 2、消费者处理消息后，可以通过两种方式向rabbitmq发送确认（ACK）
 *    2.1、监听quque时设置no_ack，这样消费者收到消息时，rabbitmq便自动认为该消息已经ack，并将之从queue删除
 *    2.2、第二种就是消费者显式的发送basic.ack消息给rabbitmq，示例见本demo
 * 更详细介绍可参考：https://my.oschina.net/dengfuwei/blog/1595047
 * ----------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/5 15:19.
 */
@Configuration
public class RabbitMQConfiguration {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
        template.setEncoding(SeedConstants.DEFAULT_CHARSET);
        //消息发送失败时，返回到队列中（需要spring.rabbitmq.publisherReturns=true）
        template.setMandatory(true);
        //消息成功到达exchange，但没有queue与之绑定时触发的回调（即消息发送不到任何一个队列中）
        template.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                LogUtil.getLogger().error("消息发送失败，replyCode={}，replyText={}，exchange={}，routingKey={}，消息体=[{}]", replyCode, replyText, exchange, routingKey, JSON.toJSONString(message.getBody()));
            }
        });
        //消息成功到达exchange后触发的ack回调（需要spring.rabbitmq.publisherConfirms=true）
        template.setConfirmCallback((correlationData, ack, cause) -> {
            if(ack){
                LogUtil.getLogger().info("消息发送成功，消息ID={}", correlationData.getId());
            }else{
                LogUtil.getLogger().error("消息发送失败，消息ID={}，cause={}", correlationData.getId(), cause);
            }
        });
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory jadyerRabbitListenerContainerFactory(ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }
}