package com.jadyer.seed.server.core;

import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * 用于启动Mina2.x服务
 * <p>
 *     这里并没有配置backlog，那么它会采用操作系统默认的连接请求队列长度50
 *     详见org.apache.mina.core.polling.AbstractPollingIoAcceptor类源码的96行
 * </p>
 * Created by 玄玉<https://jadyer.github.io/> on 2012/12/19 10:01.
 */
@Component
public class MinaStartup {
	@Resource
	private ServerHandler serverHandler;               //处理器
	@Resource
	private IoFilterChainBuilder ioFilterChainBuilder; //过滤器链
	@Value("${server.listening.port.reuse}")
	private boolean reuseAddress;                      //端口是否可重用
	@Value("#{server.listening.timeout.write}")
	private int writeTimeout;                          //写超时时间
	@Value("#{server.listening.timeout.bothidle}")
	private int bothIdleTime;                          //双向发呆时间
	@Value("${server.listening.port.tcp}")             //监听地址：TCP
	private int listeningTcpPort;
	@Value("${server.listening.port.http}")            //监听地址：HTTP
	private int listeningHttpPort;

	@PostConstruct
	public final void bind() throws IOException {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.setBacklog(0);
		acceptor.setReuseAddress(this.reuseAddress);
		acceptor.getSessionConfig().setWriteTimeout(this.writeTimeout);
		acceptor.getSessionConfig().setBothIdleTime(this.bothIdleTime);
		acceptor.setFilterChainBuilder(this.ioFilterChainBuilder);
		acceptor.setHandler(this.serverHandler);
		List<SocketAddress> socketAddresses = new ArrayList<>();
		socketAddresses.add(new InetSocketAddress(listeningTcpPort));
		socketAddresses.add(new InetSocketAddress(listeningHttpPort));
		acceptor.bind(socketAddresses);
		if(acceptor.isActive()){
			System.out.println("写 超 时: " + this.writeTimeout + "ms");
			System.out.println("发呆配置: Both Idle " + this.bothIdleTime + "s");
			System.out.println("端口重用: " + this.reuseAddress);
			System.out.println("服务端初始化完成...");
			System.out.println("服务已启动...开始监听..." + acceptor.getLocalAddresses());
		}else{
			System.out.println("服务端初始化失败...");
		}
	}
}