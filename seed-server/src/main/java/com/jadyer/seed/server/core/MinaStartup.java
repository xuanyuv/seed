package com.jadyer.seed.server.core;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;

/**
 * 用于启动Mina2.x服务
 * Created by 玄玉<https://jadyer.github.io/> on 2012/12/19 10:01.
 */
public class MinaStartup {
	private IoHandler handler;                   //处理器
	private List<SocketAddress> socketAddresses; //监听地址
	private int bothIdleTime;                    //双向发呆时间
	private int writeTimeout;                    //写操作超时时间
	private boolean reuseAddress;                //监听的端口是否可重用

	public final void bind() throws IOException {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		//这里并未配置backlog，那么它会采用操作系统默认的连接请求队列长度50
		//详见org.apache.mina.core.polling.AbstractPollingIoAcceptor类源码的96行
		//acceptor.setBacklog(0);
		acceptor.setReuseAddress(this.reuseAddress);
		acceptor.getSessionConfig().setWriteTimeout(this.writeTimeout);
		acceptor.getSessionConfig().setBothIdleTime(this.bothIdleTime);
		//这里有个鱼和熊掌不可兼得的情景
		//若将codec定义在executor的前面，则codec由NioProcessor-1线程处理，IoHandler由pool-1-thread-1线程处理
		//若将codec定义在executor的后面，则codec和IoHandler都由pool-1-thread-1线程处理
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerProtocolCodecFactory()));
		acceptor.getFilterChain().addLast("executor", new ExecutorFilter());
		//注意：无论如何executor都要定义在NioSocketConnector.setHandler()的前面
		acceptor.setHandler(this.handler);
		if(null==this.socketAddresses || this.socketAddresses.size()<1){
			throw new RuntimeException("监听SocketAddress数不得小于1");
		}
		acceptor.bind(this.socketAddresses);
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

	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}

	public void setSocketAddresses(List<SocketAddress> socketAddresses) {
		this.socketAddresses = socketAddresses;
	}

	public void setBothIdleTime(int bothIdleTime) {
		this.bothIdleTime = bothIdleTime;
	}

	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}
}