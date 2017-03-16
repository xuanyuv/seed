package com.jadyer.seed.server;

import com.jadyer.seed.comm.util.ConfigUtil;
import com.jadyer.seed.server.core.ServerHandler;
import com.jadyer.seed.server.core.ServerProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
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
	private ServerHandler serverHandler;

	@PostConstruct
	public final void bind() throws IOException {
		int tcpPort = ConfigUtil.INSTANCE.getPropertyForInt("server.port.tcp");
		int httpPort = ConfigUtil.INSTANCE.getPropertyForInt("server.port.http");
		int writeTimeout = ConfigUtil.INSTANCE.getPropertyForInt("server.timeout.write");
		int bothIdleTime = ConfigUtil.INSTANCE.getPropertyForInt("server.timeout.bothidle");
		boolean reuseAddress = Boolean.parseBoolean(ConfigUtil.INSTANCE.getProperty("server.port.reuse"));
		NioSocketAcceptor acceptor = new NioSocketAcceptor();
		acceptor.setBacklog(0);
		//端口是否可重用
		acceptor.setReuseAddress(reuseAddress);
		//写超时时间，单位：毫秒
		acceptor.getSessionConfig().setWriteTimeout(writeTimeout);
		//双向发呆时间，单位：秒
		acceptor.getSessionConfig().setBothIdleTime(bothIdleTime);
		//过滤器链
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ServerProtocolCodecFactory()));
		acceptor.getFilterChain().addLast("executor", new ExecutorFilter());
		//处理器
		acceptor.setHandler(this.serverHandler);
		List<SocketAddress> socketAddresses = new ArrayList<>();
		//监听的TCP服务端口
		socketAddresses.add(new InetSocketAddress(tcpPort));
		//监听的HTTP服务端口
		socketAddresses.add(new InetSocketAddress(httpPort));
		acceptor.bind(socketAddresses);
		if(acceptor.isActive()){
			System.out.println("写 超 时: " + writeTimeout + "ms");
			System.out.println("发呆配置: Both Idle " + bothIdleTime + "s");
			System.out.println("端口重用: " + reuseAddress);
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