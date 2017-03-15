package com.jadyer.seed.server.core;

import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.List;

/**
 * 用于启动Mina2.x服务
 * <p>
 *     这里并没有配置backlog，那么它会采用操作系统默认的连接请求队列长度50
 *     详见org.apache.mina.core.polling.AbstractPollingIoAcceptor类源码的96行
 * </p>
 * Created by 玄玉<https://jadyer.github.io/> on 2012/12/19 10:01.
 */
public class MinaStartup {
	private IoHandler handler;                       //处理器
	private List<SocketAddress> socketAddresses;     //监听地址
	private IoFilterChainBuilder filterChainBuilder; //过滤器链
	private int writeTimeout;                        //写超时时间
	private int bothIdleTime;                        //双向发呆时间
	private boolean reuseAddress;                    //端口是否可重用
	
	public final void bind() throws IOException {
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.setBacklog(0);
		acceptor.setReuseAddress(this.reuseAddress);
		acceptor.getSessionConfig().setWriteTimeout(this.writeTimeout);
		acceptor.getSessionConfig().setBothIdleTime(this.bothIdleTime);
		acceptor.setFilterChainBuilder(this.filterChainBuilder);
		acceptor.setHandler(this.handler);
		if(this.socketAddresses==null || this.socketAddresses.size()<1){
			throw new RuntimeException("监听SocketAddress数不得小于1");
		}
		acceptor.bind(this.socketAddresses);
		if(acceptor.isActive()){
			System.out.println("写 超 时: " + this.writeTimeout + "ms");
			System.out.println("发呆配置: Both Idle " + this.bothIdleTime + "s");
			System.out.println("端口重用: " + this.reuseAddress);
			System.out.println("服务端初始化完成......");
			System.out.println("服务已启动....开始监听...." + acceptor.getLocalAddresses());
		}else{
			System.out.println("服务端初始化失败......");
		}
	}
	
	public void setHandler(IoHandler handler) {
		this.handler = handler;
	}
	public void setSocketAddresses(List<SocketAddress> socketAddresses) {
		this.socketAddresses = socketAddresses;
	}
	public void setFilterChainBuilder(IoFilterChainBuilder filterChainBuilder) {
		this.filterChainBuilder = filterChainBuilder;
	}
	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}
	public void setBothIdleTime(int bothIdleTime) {
		this.bothIdleTime = bothIdleTime;
	}
	public void setReuseAddress(boolean reuseAddress) {
		this.reuseAddress = reuseAddress;
	}
}