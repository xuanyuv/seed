package com.jadyer.seed.boot;

import com.jadyer.seed.boot.event.ApplicationEnvironmentPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationFailedEventListener;
import com.jadyer.seed.boot.event.ApplicationPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationStartingEventListener;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * SpringBoot启动类
 * ---------------------------------------------------------------------------------------------------------------
 * 1.@SpringBootApplication
 *   等效於@Configuration和@EnableAutoConfiguration和@ComponentScan三個注解放在一起
 *   Configuration注解------------注解表示该文件作为Spring配置文件存在
 *   EnableAutoConfiguration注解--用于启动SpringBoot内置的自动配置
 *   ComponentScan注解------------扫描Bean，默认路径为该类所在package及子package，也可手工指定package
 * 2.SpringBoot加载配置文件的优先级
 *   默認配置文件為application.properties or application.yml
 *   classpath:/config/application.yml优先级高于classpath:application.yml
 * 3.SpringProfile环境文件
 *   若应用中包含多个profile，可以为每个profile定义各自的属性文件，按照“application-{profile}.yml”来命名
 *   有个细节：只要存在application.yml，则无论application-{profile}.yml存在与否，application.yml都会被读取
 *   若application-{profile}.yml与application.yml存在同名属性，SpringBoot会以application-{profile}.yml为准
 *   总结：实际完全可以把所有环境配置都写在一个application.yml，通过“---”和“spring.profiles”区分各环境配置区域即可
 * 4.SpringProfile的优先级
 *   若服务启动参数包含spring.profiles.active，那么Spring会自动读取并根据参数值加载application-{profile}.yml
 *   同时根据测试得知：-Dspring.profiles.active=dev的优先级要高于SpringApplicationBuilder.profiles("prod")
 *   另外：@Profile(value="test")的示例使用詳見com.jadyer.seed.boot.RabbitMQConfiguration.java
 * ---------------------------------------------------------------------------------------------------------------
 * 条件注解
 * 它是Sring-boot封装了Spring4.x开始提供的@Conditional注解实现的新注解，主要分以下几类
 * 1.@ConditionalOnClass            ：classpath中出现指定类的条件下
 * 2.@ConditionalOnMissingClass     ：classpath中没有出现指定类的条件下
 * 3.@ConditionalOnBean             ：容器中出现指定Bean的条件下
 * 4.@ConditionalOnMissingBean      ：容器中没有出现指定Bean的条件下
 * 7.@ConditionalOnWebApplication   ：当前应用是Web应用的条件下
 * 8.@ConditionalOnNotWebApplication：当前应用不是Web应用的条件下（指的是SpringWebApplicationContext）
 * 6.@ConditionalOnResource         ：根据资源是否存在作为判断条件（可使用Spring约定命名，比如file:/app/date/test.dat）
 * 5.@ConditionalOnProperty         ：根据SpringEnvironment属性的值作为判断条件
 * 9.@ConditionalOnExpression       ：基于SpEL表达式作为判断条件
 * 10.@ConditionalOnJava            ：基于JVM版本作为判断条件
 * 下面以org.springframework.boot.autoconfigure.amqp.RabbitAutoConfiguration.java举例说明一下
 * 也就是：@ConditionalOnClass({ RabbitTemplate.class, Channel.class })
 * 它表示：RabbitTemplate.class和Channel.class文件存在于classpath里面时，才允许解析RabbitAutoConfiguration.java
 *        否则直接跳过不解析（这也是为什么没有导入RabbitMQ的依赖Jar时，工程仍能正常启动的原因）
 * 再比如：@ConditionalOnProperty(name="seed.lock.enabled", havingValue="true", matchIfMissing=true)
 * 它表示：如果配置文件中没有配置seed.lock.enabled，那么允许加载（因为matchIfMissing=true）
 *        如果配置文件中seed.lock.enabled=true，那么允许加载（因为havingValue="true"二者的值匹配结果为相同）
 *        如果配置文件中seed.lock.enabled=false，那么禁止加载（因为havingValue="true"二者的值匹配结果为不同）
 * ---------------------------------------------------------------------------------------------------------------
 * 发布成war
 * 1、<packaging>war</packaging>
 * 2、mvn clean install -DskipTests
 * 此时就不需要本类了（也不需要配置spring-boot-maven-plugin），这种情况下的启动类，内容如下：
 * package com.jadyer.seed.boot;
 * import com.jadyer.seed.boot.event.ApplicationEnvironmentPreparedEventListener;
 * import com.jadyer.seed.boot.event.ApplicationFailedEventListener;
 * import com.jadyer.seed.boot.event.ApplicationPreparedEventListener;
 * import com.jadyer.seed.boot.event.ApplicationStartingEventListener;
 * import org.springframework.boot.autoconfigure.SpringBootApplication;
 * import org.springframework.boot.builder.SpringApplicationBuilder;
 * import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
 * @SpringBootApplication(scanBasePackages="${scan.base.packages}")
 * public class BootStrap extends SpringBootServletInitializer {
 *     @Override
 *     protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
 *         //此处未设置.profiles()，则打成的war包在Tomcat中启动时，会自动读取Tomcat启动变量中的spring.profiles.active
 *         return builder.sources(getClass())
 *                .listeners(new ApplicationStartingEventListener())
 *                .listeners(new ApplicationEnvironmentPreparedEventListener())
 *                .listeners(new ApplicationPreparedEventListener())
 *                .listeners(new ApplicationFailedEventListener());
 *     }
 * }
 * ---------------------------------------------------------------------------------------------------------------
 * 发布成可执行jar
 * 1、也可以不设置，因为它默认就是jar：<packaging>jar</packaging>
 * 2、打包（mvn clean install -DskipTests）执行到package阶段时，会触发repackage，于是得到可执行的jar
 *    <build>
 *     <plugins>
 *         <plugin>
 *             <groupId>org.springframework.boot</groupId>
 *             <artifactId>spring-boot-maven-plugin</artifactId>
 *             <configuration>
 *                 <mainClass>com.jadyer.seed.boot.BootRun</mainClass>
 *             </configuration>
 *             <executions>
 *                 <execution>
 *                     <goals>
 *                         <goal>repackage</goal>
 *                     </goals>
 *                 </execution>
 *             </executions>
 *         </plugin>
 *     </plugins>
 *    </build>
 * 3、启动应用：java -jar -Dspring.profiles.active=prod seed-boot-2.0.jar
 * ---------------------------------------------------------------------------------------------------------------
 * spring-boot-starter-actuator
 * 1./auditevents------公开当前应用程序的审核事件信息
 *   /autoconfig-------查看自动配置的使用情况
 *   /beans------------查看应用程序中所有SpringBean及其关系列表
 *   /conditions-------显示在配置和自动配置类上评估的条件以及它们匹配或不匹配的原因
 *   /configprops------显示所有@ConfigurationProperties的整理列表
 *   /dump-------------生成一个ThreadDump（即打印线程栈）
 *   /env--------------查看所有环境变量（即显示从ConfigurableEnvironment得到的环境配置信息）
 *   /env/{name}-------查看具体变量值
 *   /flyway-----------显示已应用的任何Flyway数据库迁移
 *   /health-----------查看应用健康指标（实现HealthIndicator接口后还可以自定义Health服务，另外当使用一个未认证连接访问时显示一个简单的’status’，使用认证连接访问则显示全部信息详情）
 *   /heapdump---------返回GZip压缩hprof堆转储文件
 *   /httptrace--------显示HTTP跟踪信息（默认情况下，最后100个HTTP请求 - 响应交换）
 *   /info-------------查看应用信息（会将应用中任何以“info.”开头的配置参数暴露出去，详见application.yml）
 *   /jolokia----------通过HTTP公开JMXbean（当Jolokia在类路径上时，不适用于WebFlux）
 *   /logfile----------返回日志文件的内容（如果已设置logging.file或logging.path属性，支持使用HTTPRange标头来检索部分日志文件的内容）
 *   /loggers----------显示和修改应用程序中loggers的配置（SpringBoot-1.5.x才开始提供的新功能）
 *   /liquibase--------显示已应用的任何Liquibase数据库迁移
 *   /mappings---------查看所有URL映射
 *   /metrics----------查看应用基本指标
 *   /metricss/{name}--查看具体指标
 *   /prometheus-------以可以由Prometheus服务器抓取的格式公开指标
 *   /scheduledtasks---显示应用程序中的计划任务
 *   /sessions---------允许从SpringSession支持的会话存储中检索和删除用户会话（使用SpringSession对响应式Web应用程序的支持时不可用）
 *   /shutdown---------关闭应用（属于敏感操作，故此功能默认是关闭的）
 *   /threaddump-------显示当前应用线程状态信息
 *   /trace------------查看应用相关的跟踪信息
 *   注意：只有当应用程序是Web应用程序（Spring MVC，Spring WebFlux或Jersey），才可以使用这几个端点：heapdump、jolokia、logfile、prometheus
 * 2.http://127.0.0.1/shutdown
 *   通过配置management.endpoints.shutdown.enabled=true即可启用
 *   另外：http://127.0.0.1/shutdown支持POST，但不支持GET（curl -X POST host:port/shutdown）
 *   它在收到请求，关闭应用时，本例中的SpringContextHolder.clearHolder()方法会被调用
 *   并返回该字符串给调用方（含小括号）：{"message":"Shutting down, bye..."}
 *   需要注意的是：该功能需要配置management.security.enabled=false来关闭安全认证校验
 *   其实更好的做法是将应用设置成Unix/Linux的系统服务，这样就能以“service app stop”命令来操作
 *   详见https://www.cnblogs.com/lobo/p/5657684.html & https://my.oschina.net/u/4292220/blog/3236949
 * 3.http://127.0.0.1/loggers/日志端点/新的日志级别
 *   可以动态修改日志级别，示例代码见{@link com.jadyer.seed.controller.DemoController#loglevel(String, String)}
 *   并且，GET请求“http://127.0.0.1/actuator/loggers/日志端点”还可以查看其当前的日志级别
 *   需要注意的是：该功能需要配置management.security.enabled=false来关闭安全认证校验
 * ---------------------------------------------------------------------------------------------------------------
 * 【Spring Boot Admin 配置之Admin端】
 * 引入三个包：spring-boot-admin-starter-server、spring-boot-admin-server-ui、spring-boot-starter-web
 * 然后启动，访问：http://127.0.0.1:8080/即可
 * 【Spring Boot Admin 配置之Client端】
 * 引入三个包：spring-boot-admin-starter-client、spring-boot-starter-actuator、spring-boot-starter-web
 * 增加三以下三个配置后，启动应用即可
 * 1.spring.application.name=SeedBoot
 * 2.spring.boot.admin.client.url=http://127.0.0.1:8080
 * 3.management.endpoints.web.exposure.include=*
 * ---------------------------------------------------------------------------------------------------------------
 * 记录应用进程号到本地文件
 * 除了{@link com.jadyer.seed.comm.util.RequestUtil#getPID()}方法外，也可以在应用启动时把pid写到本地文件中
 * 方法为启动时注册监听器ApplicationPidFileWriter，然后在yml配置spring.pid.file=/data/myboot.pid即可
 * 注意以下几点：
 * 1、默认ApplicationPidFileWriter没有自动配置，所以只有显式配置后，才会把pid写入到本地文件
 * 2、pid写入的文件名，默认为：application.pid，默认路径也是当期路径，只有配置了spring.pid.file之后才会根据配置写入目标文件
 * 3、若写入文件失败，则会将pid值写入系统环境变量PID_FAIL_ON_WRITE_ERROR（不区分大小写），或写入Spring 境变量spring.pid.fail-on-write-error
 * ---------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2015/11/29 15:35.
 */
@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class BootRun {
    private static final Logger log = LoggerFactory.getLogger(BootRun.class);

    private static String getProfile(SimpleCommandLinePropertySource source){
        if(source.containsProperty(SeedConstants.BOOT_ACTIVE_NAME)){
            //补充：IntelliJ IDEA运行时，如果在Run/Debug Configurations为该启动类配置Program arguments的值为"--spring.profiles.active=dev"
            //那么这里就能读取到该配置，同时控制台会打印"读取到spring变量：spring.profiles.active=dev"
            log.info("读取到spring变量：{}={}", SeedConstants.BOOT_ACTIVE_NAME, source.getProperty(SeedConstants.BOOT_ACTIVE_NAME));
            return source.getProperty(SeedConstants.BOOT_ACTIVE_NAME);
        }
        if(System.getProperties().containsKey(SeedConstants.BOOT_ACTIVE_NAME)){
            log.info("读取到java变量：{}={}", SeedConstants.BOOT_ACTIVE_NAME, System.getProperty(SeedConstants.BOOT_ACTIVE_NAME));
            return System.getProperty(SeedConstants.BOOT_ACTIVE_NAME);
        }
        if(System.getenv().containsKey(SeedConstants.BOOT_ACTIVE_NAME)){
            log.info("读取到系统变量：{}={}", SeedConstants.BOOT_ACTIVE_NAME, System.getenv(SeedConstants.BOOT_ACTIVE_NAME));
            return System.getenv(SeedConstants.BOOT_ACTIVE_NAME);
        }
        log.warn("未读取到{}，默认取环境：{}", SeedConstants.BOOT_ACTIVE_NAME, SeedConstants.BOOT_ACTIVE_DEFAULT_VALUE);
        //logback-boot.xml中根据环境变量配置日志是否输出到控制台时，使用此配置
        System.setProperty(SeedConstants.BOOT_ACTIVE_NAME, SeedConstants.BOOT_ACTIVE_DEFAULT_VALUE);
        return SeedConstants.BOOT_ACTIVE_DEFAULT_VALUE;
    }

    public static void main(String[] args) {
        //SpringApplication.run(BootRun.class, args);
        //new SpringApplicationBuilder().sources(BootRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder().sources(BootRun.class)
                .listeners(new ApplicationStartingEventListener())
                .listeners(new ApplicationEnvironmentPreparedEventListener())
                .listeners(new ApplicationPreparedEventListener())
                .listeners(new ApplicationFailedEventListener())
                .listeners(new ApplicationPidFileWriter())
                .profiles(getProfile(new SimpleCommandLinePropertySource(args)))
                .run(args);
        Environment env = applicationContext.getEnvironment();
        String protocol = StringUtils.isBlank(env.getProperty("server.ssl.key-store")) ? "http" : "https";
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        if(!contextPath.endsWith("/")){
            contextPath += "/";
        }
        // contextPath += "doc.html";
        LogUtil.getLogger().info("\n------------------------------------------------------------" +
                        "\n\tApplication '{}' is running! \t\tProfile(s): {}" +
                        "\n\tAccess URLs:" +
                        "\n\tLocal: \t\t{}://{}:{}{}" +
                        "\n\tExternal: \t{}://{}:{}{}" +
                        "\n------------------------------------------------------------",
                env.getProperty("spring.application.name"), env.getActiveProfiles(),
                protocol, "127.0.0.1", serverPort, contextPath,
                protocol, RequestUtil.getServerIP(), serverPort, contextPath);
    }
}