package com.jadyer.seed.server;

import com.jadyer.seed.server.core.ServerHandler;
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
	@Value("${server.listening.timeout.write}")
	private int writeTimeout;                          //写超时时间
	@Value("${server.listening.timeout.bothidle}")
	private int bothIdleTime;                          //双向发呆时间
	@Value("${server.listening.port.tcp}")
	private int listeningTcpPort;                      //监听地址：TCP
	@Value("${server.listening.port.http}")
	private int listeningHttpPort;                     //监听地址：HTTP

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
/*
下面是非SpringBoot应用的applicationContext.xml写法
*/
/*
<!-- 构造Server端 -->
<bean id="jadyerServer" class="com.jadyer.server.core.MinaStartup" init-method="bind">
	<property name="reuseAddress" value="true"/>
	<property name="writeTimeout" value="${sys.timeout.tcp.write}"/>
	<property name="bothIdleTime" value="${sys.timeout.tcp.bothIdle}"/>
	<property name="filterChainBuilder" ref="ioFilterChainBuilder"/>
	<property name="handler" ref="jadyerServerHandler"/>
	<property name="socketAddresses">
		<list>
			<bean class="java.net.InetSocketAddress">
				<constructor-arg index="0" value="${server.port.tcp}"/>
			</bean>
			<bean class="java.net.InetSocketAddress">
				<constructor-arg index="0" value="${server.port.http}"/>
			</bean>
		</list>
	</property>
</bean>
<!-- 构造过滤器链 -->
<!-- 这里有个鱼和熊掌不可兼得的情景，注意：无论如何executor都要定义在NioSocketConnector.setHandler()的前面 -->
<!-- 若将codec定义在executor的前面，则codec由NioProcessor-1线程处理，IoHandler由pool-1-thread-1线程处理 -->
<!-- 若将codec定义在executor的后面，则codec和IoHandler都由pool-1-thread-1线程处理 -->
<bean id="ioFilterChainBuilder" class="org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder">
	<property name="filters">
		<map>
			<entry key="codec">
				<bean class="org.apache.mina.filter.codec.ProtocolCodecFilter">
					<constructor-arg>
						<bean class="com.jadyer.server.core.ServerProtocolCodecFactory"/>
					</constructor-arg>
				</bean>
			</entry>
			<entry key="executor">
				<bean class="org.apache.mina.filter.executor.ExecutorFilter"/>
			</entry>
		</map>
	</property>
</bean>
<!-- 构造业务处理类 -->
<bean id="jadyerServerHandler" class="com.jadyer.server.core.ServerHandler">
	<property name="busiProcessMap">
		<map key-type="java.lang.String" value-type="com.jadyer.server.core.GenericAction">
			<entry key="10005" value-ref="orderResultNotifyAction"/>
			<entry key="/notify_boc" value-ref="netBankResultNotifyBOCAction"/>
			<entry key="/notify_yeepay" value-ref="netBankResultNotifyYEEPAYAction"/>
		</map>
	</property>
</bean>
*/