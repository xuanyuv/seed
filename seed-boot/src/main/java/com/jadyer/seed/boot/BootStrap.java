package com.jadyer.seed.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

/**
 * SpringBoot啟動類
 * -----------------------------------------------------------------------------------------------------------
 * 1.@SpringBootApplication
 *   等效於@Configuration和@EnableAutoConfiguration和@ComponentScan三個注解放在一起
 *   Configuration注解------------注解表示该文件作为Spring配置文件存在
 *   EnableAutoConfiguration注解--用于启动SpringBoot内置的自动配置
 *   ComponentScan注解------------扫描Bean,默认路径为该类所在package及子package,也可手工指定package
 * 2.SpringBoot加载配置文件的优先级
 *   默認配置文件為application.properties or application.yml
 *   classpath:/config/application.yml优先级高于classpath:application.yml
 * 3.SpringProfile环境文件
 *   若应用中包含多个profile,可以为每个profile定义各自的属性文件,按照"application-{profile}.yml"来命名
 *   有个细节,只要存在application.yml,则无论application-{profile}.yml存在与否,application.yml都会被读取
 *   若application-{profile}.yml与application.yml存在同名属性,SpringBoot会以application-{profile}.yml为准
 *   总结:实际完全可以把所有环境配置都写在一个application.yml,通过"---"和"spring.profiles"区分各环境配置区域即可
 * 4.SpringProfile的优先级
 *   若服务启动参数包含spring.profiles.active,那么Spring会自动读取并根据参数值加载application-{profile}.yml
 *   同时根据测试得知,-Dspring.profiles.active=dev的优先级要高于SpringApplicationBuilder.profiles("prod")
 *   另外，@Profile(value="test")的示例使用詳見com.jadyer.demo.boot.h2.H2Configuration.java
 * -----------------------------------------------------------------------------------------------------------
 * spring-boot-starter-actuator
 * 1.autoconfig---显示SpringBoot自动配置的信息
 *   beans--------显示应用中包含的SpringBean的信息
 *   configprops--显示应用中的配置参数的实际值
 *   dump---------生成一个ThreadDump
 *   env----------显示从ConfigurableEnvironment得到的环境配置信息
 *   health-------显示应用的健康状态信息
 *   info---------显示应用的基本信息
 *   metrics------显示应用的性能指标
 *   mappings-----显示SpringMVC应用中通过@RequestMapping添加的路径映射
 *   trace--------显示应用相关的跟踪信息
 *   shutdown-----关闭应用
 * 2.http://127.0.0.1/health可以查看应用的健康状态,实现HealthIndicator接口后还可以自定义Health服务
 * 3.http://127.0.0.1/info会将应用中任何以"info."开头的配置参数暴露出去,详见application.properties写法
 * 4.http://127.0.0.1/shutdown属于敏感操作，故此功能默认是关闭的（其它的actuator都不是敏感操作，所以默认都是开启的）
 *   通过配置endpoints.shutdown.enabled=true即可启用
 *   另外，http://127.0.0.1/shutdown支持POST，但不支持GET请求
 *   它在收到请求，关闭应用时，本例中的SpringContextHolder.clearHolder()方法会被调用
 *   并返回该字符串给调用方（包括小括号）：{"message":"Shutting down, bye..."}
 * -----------------------------------------------------------------------------------------------------------
 * 条件注解
 * 它是Sring-boot封装了Spring4.x开始提供的@Conditional注解实现的新注解，主要分以下几类
 * 1.@ConditionalOnClass            ：classpath中出现指定类的条件下
 * 2.@ConditionalOnMissingClass     ：classpath中没有出现指定类的条件下
 * 3.@ConditionalOnBean             ：容器中出现指定Bean的条件下
 * 4.@ConditionalOnMissingBean      ：容器中没有出现指定Bean的条件下
 * 7.@ConditionalOnWebApplication   ：当前应用是Web应用的条件下
 * 8.@ConditionalOnNotWebApplication：当前应用不是Web应用的条件下（指的是SpringWebApplicationContext）
 * 6.@ConditionalOnResource         ：根据资源是否存在作为判断条件（可使用Spring约定命名，比如file:/home/test.dat）
 * 5.@ConditionalOnProperty         ：根据SpringEnvironment属性的值作为判断条件
 * 9.@ConditionalOnExpression       ：基于SpEL表达式作为判断条件
 * 10.@ConditionalOnJava            ：基于JVM版本作为判断条件
 * 下面以org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.java举例说明一下
 * 其中的@ConditionalOnClass()就表示RabbitTemplate.class和Channel.class文件存在于classpath里面时
 * 才会解析RabbitAutoConfiguration.java，否则直接跳过不解析
 * 这也是为什么没有导入RabbitMQ的依赖Jar时，工程仍能正常启动的原因
 * -----------------------------------------------------------------------------------------------------------
 * SpringBoot-1.3.x 到 1.4.x 的变化之一就是：
 * org.springframework.boot.context.embedded.ErrorPage 变为了 org.springframework.boot.web.servlet.ErrorPage
 * org.springframework.boot.context.embedded.FilterRegistrationBean 变为了 org.springframework.boot.web.servlet.FilterRegistrationBean
 * org.springframework.boot.context.embedded.ServletRegistrationBean 变为了 org.springframework.boot.web.servlet.ServletRegistrationBean
 * org.springframework.boot.context.web.SpringBootServletInitializer 变为了 org.springframework.boot.web.support.SpringBootServletInitializer
 * org.springframework.boot.test.SpringApplicationConfiguration 变为了 org.springframework.boot.test.context.SpringBootTest
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/11/29 15:35.
 */
@SpringBootApplication(scanBasePackages="${scan.base.packages}", exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class BootStrap extends SpringBootServletInitializer {
	//启动时不能直接执行main
	//具体启动方式见https://jadyer.github.io/2016/07/29/idea-springboot-jsp/
	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(BootStrap.class).profiles("local").run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        //这里的配置与com.jadyer.demo.test.ApiBootStarp.java中的配置是没有关系的
		//return builder.sources(getClass())
		//        .listeners(new ApplicationStartedEventListener())
		//        .listeners(new ApplicationEnvironmentPreparedEventListener())
		//        .listeners(new ApplicationPreparedEventListener())
		//        .listeners(new ApplicationFailedEventListener());
		return builder.sources(getClass());
	}

	@Bean
	public Filter characterEncodingFilter(){
		return new CharacterEncodingFilter("UTF-8", true);
	}
}