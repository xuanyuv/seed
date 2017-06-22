package com.jadyer.seed.boot;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;

/**
 * RabbitMQ之TopicExchange发送和接收的演示
 * ----------------------------------------------------------------------------------------------------
 * 下面是几个SpringBoot整合RabbitMQ的例子，个人觉得要么太单一，要么太复杂
 * https://segmentfault.com/a/1190000004401870
 * http://blog.didispace.com/spring-boot-rabbitmq/
 * http://www.ityouknow.com/springboot/2016/11/30/springboot(八)-RabbitMQ详解.html
 * ----------------------------------------------------------------------------------------------------
 * 关于本类中定义的两个Bean的说明（RabbitTemplate、SimpleRabbitListenerContainerFactory）
 * https://jira.spring.io/browse/AMQP-573
 * http://www.cnblogs.com/lazio10000/p/5559999.html
 * ----------------------------------------------------------------------------------------------------
 * 自定义日志切面的时候（比如本工程中的com.jadyer.seed.comm.base.LogAspect.java）
 * 如果切面中用到了ServletRequestAttributes，则还需判断其是否为null
 * 因为RabbitMq的消息队列在发送消息给订阅者时，日志切了拦截到的ServletRequestAttributes==null
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
 * 关于消息确认机制（acknowledgments）
 * 1、多个消费者监听同一个queue时，默认的rabbitmq会采用round-robin（轮流）的方式，每条消息只会发给一个消费者
 * 2、消费者处理消息后，可以通过两种方式向rabbitmq发送确认（ACK）
 *    2.1、监听quque时设置auto_ack，这样消费者收到消息时，rabbitmq便自动认为该消息已经ack，并将之从queue删除
 *    2.2、第二种就是显式的发送basic.ack消息给rabbitmq
 * 若以上两种都没有做，rabbitmq会认为该消息未被正确处理，在当前消费者退出后（或断开连接）它会再次发给其它消费者
 * ----------------------------------------------------------------------------------------------------
 * 接收端写法如下（接收从RabbitMQ订阅的消息）
 * @Component
 * class ApplyService {
 *     //spring.rabbitmq.queues=myapi.get.apply.status
 *     //containerFactory指定的是com.jadyer.seed.boot.RabbitMQConfiguration.java里面声明的Bean
 *     @RabbitListener(queues="${spring.rabbitmq.queues}", containerFactory="jadyerRabbitListenerContainerFactory")
 *     public void receive(User user){
 *         System.out.println("收到从RabbitMQ订阅过来的消息-->[" + ReflectionToStringBuilder.toString(user) + "]");
 *     }
 *
 *     @RabbitListener(queues="myapi.get.apply.status22", containerFactory="jadyerRabbitListenerContainerFactory")
 *     public void receive22(User user){
 *         System.out.println("收到从RabbitMQ订阅过来的消息22-->[" + ReflectionToStringBuilder.toString(user) + "]");
 *     }
 * }
 * ----------------------------------------------------------------------------------------------------
 * 发送端写法如下（发送一个消息到RabbitMQ）
 * @Controller
 * @RequestMapping("/apply")
 * class ApplyController {
 *     @Resource
 *     private RabbitTemplate rabbitTemplate;
 *
 *     @ResponseBody
 *     @GetMapping("/mq/send")
 *     public CommonResult send(){
 *         User user = new User(2, "玄玉", "http://jadyer.cn/");
 *         this.rabbitTemplate.convertAndSend("apply.status", "apply.status.1101.123", user);
 *         return new CommonResult();
 *     }
 * }
 * ----------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2017/6/5 15:19.
 */
//@Configuration
public class RabbitMQConfiguration {
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(new Jackson2JsonMessageConverter());
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