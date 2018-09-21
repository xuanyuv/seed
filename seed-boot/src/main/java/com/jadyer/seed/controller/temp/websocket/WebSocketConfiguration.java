package com.jadyer.seed.controller.temp.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 开启webSocket消息代理功能，并推送日志信息
 * -----------------------------------------------------------------------------------------------------------------
 * 【待定】做一个这样的博文：我想看日志、我想自定接收日志
 * 【待定】本demo其余牵连文件为：logback-boot.xml 和 viewlog.html 和 ViewController
 * -----------------------------------------------------------------------------------------------------------------
 * http://blog.csdn.net/qq_28988969/article/details/78113463
 * http://blog.csdn.net/shuaicihai/article/details/75210704
 * -----------------------------------------------------------------------------------------------------------------
 * WebSocket（stopmp服务端），stomp协议，sockjs.min.js，stomp.min.js（stomp客户端），
 * 本文使用到的其实就是使用spring boot自带的webSocket模块提供stomp的服务端，前端使用stomp.min.js做stomp的客户端，使用sockjs来链接，
 * 前端订阅后端日志端点的消息，后端实时推送，达到日志实时输出到web页面的目的
 * -----------------------------------------------------------------------------------------------------------------
 * STOMP即Simple (or Streaming) Text Orientated Messaging Protocol，简单(流)文本定向消息协议，
 * 它提供了一个可互操作的连接格式，允许STOMP客户端与任意STOMP消息代理（Broker）进行交互。
 * STOMP协议由于设计简单，易于开发客户端，因此在多种语言和多种平台上得到广泛地应用。
 * STOMP协议的前身是TTMP协议（一个简单的基于文本的协议），专为消息中间件设计。
 * STOMP是一个非常简单和容易实现的协议，其设计灵感源自于HTTP的简单性。
 * 尽管STOMP协议在服务器端的实现可能有一定的难度，但客户端的实现却很容易。例如，可以使用Telnet登录到任何的STOMP代理，并与STOMP代理进行交互。
 * -----------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2018/2/13 17:11.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer {
    @Resource
    private SimpMessagingTemplate messagingTemplate;

    /**
     * 配置WebSocket消息代理端点，即stomp服务端
     * -----------------------------------------------------------------------------------------------
     * 注意：为了连接安全，setAllowedOrigins设置的允许连接的源地址，如果在非这个配置的地址下发起连接会报403，
     * 进一步还可以使用addInterceptors设置拦截器，来做相关的鉴权操作
     * -----------------------------------------------------------------------------------------------
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/seed-boot-websocket").setAllowedOrigins("http://localhost").withSockJS();
    }


    @PostConstruct
    public void pushLog(){
        ExecutorService threadPool = Executors.newSingleThreadExecutor();
        threadPool.execute(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        LogMsg msg = LogQueue.getInstance().pull();
                        if(null != msg){
                            messagingTemplate.convertAndSend("/topic/pullLogMsg", msg);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        threadPool.shutdown();
    }
}