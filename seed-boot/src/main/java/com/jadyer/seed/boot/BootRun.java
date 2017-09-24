package com.jadyer.seed.boot;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.jadyer.seed.boot.event.ApplicationEnvironmentPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationFailedEventListener;
import com.jadyer.seed.boot.event.ApplicationPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationStartedEventListener;
import com.jadyer.seed.comm.constant.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * SpringBoot啟動類
 * ---------------------------------------------------------------------------------------------------------------
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
 *   另外，@Profile(value="test")的示例使用詳見com.jadyer.seed.boot.H2Configuration.java
 * ---------------------------------------------------------------------------------------------------------------
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
 *   loggers------显示日志级别（SpringBoot-1.5.x才开始提供的新功能）
 *   shutdown-----关闭应用
 * 2.http://127.0.0.1/health可以查看应用的健康状态，实现HealthIndicator接口后还可以自定义Health服务
 * 3.http://127.0.0.1/info会将应用中任何以"info."开头的配置参数暴露出去，详见application.properties写法
 * 4.http://127.0.0.1/shutdown属于敏感操作，故此功能默认是关闭的（其它的actuator都不是敏感操作，所以默认都是开启的）
 *   通过配置endpoints.shutdown.enabled=true即可启用
 *   另外，http://127.0.0.1/shutdown支持POST，但不支持GET请求
 *   它在收到请求，关闭应用时，本例中的SpringContextHolder.clearHolder()方法会被调用
 *   并返回该字符串给调用方（包括小括号）：{"message":"Shutting down, bye..."}
 *   需要注意的是：该功能需要配置management.security.enabled=false来关闭安全认证校验
 * 5.http://127.0.0.1/loggers/日志端点/新的日志级别
 *   可以动态修改日志级别，示例代码见{@link com.jadyer.seed.controller.DemoController#loglevel(String, String)}
 *   并且，GET请求“http://127.0.0.1/loggers/日志端点”还可以查看其当前的日志级别
 *   需要注意的是：该功能需要配置management.security.enabled=false来关闭安全认证校验
 * 并且：除了“/health”和”/info”以外，其它actuator监控都需要配置management.security.enabled=false
 * 另外：可以通过spring-boot-admin实现应用监控，相关示例见https://my.oschina.net/u/1266221/blog/805596
 * ---------------------------------------------------------------------------------------------------------------
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
 * ---------------------------------------------------------------------------------------------------------------
 * 发布成war
 * 1、<packaging>war</packaging>
 * 2、mvn clean install -DskipTests
 * 此时只需要com.jadyer.seed.boot.BootStrap.java，本类就不需要了，也不需要配置spring-boot-maven-plugin
 * ---------------------------------------------------------------------------------------------------------------
 * 关于可执行jar中解析jsp
 * SpringBoot官方称：可执行jar是无法解析jsp的，不过也有人找到一种有理有据的方法，使它能够解析jsp
 * 详细描述见：https://dzone.com/articles/spring-boot-with-jsps-in-executable-jars-1
 * 总结起来就是：将jsp放在/src/main/resources/META-INF/resources/目录下
 * 实际测试发现：执行jar后访问jsp，不好使，还是看到404的那个白板页面
 * 但是在 IntelliJ IDEA（即本地IDE）中测试发现，它是可以访问的，只不过优先级没那么高
 * idea会优先读取/src/main/webapp/下的文件，找不到时才会去找/src/main/resources/META-INF/resources/下的文件
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
 * 3、启动应用：java -jar -Dspring.profiles.active=prod seed-boot-1.1.jar
 * ---------------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2015/11/29 15:35.
 */
@SpringBootApplication(scanBasePackages="${scan.base.packages}", exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class BootRun {
    //@Bean
    //public Filter characterEncodingFilter(){
    //    return new CharacterEncodingFilter("UTF-8", true);
    //}

    private static final Logger log = LoggerFactory.getLogger(BootRun.class);

    private static String getProfile(SimpleCommandLinePropertySource source){
        if(source.containsProperty(Constants.BOOT_ACTIVE_NAME)){
            log.info("读取到spring变量：{}={}", Constants.BOOT_ACTIVE_NAME, source.getProperty(Constants.BOOT_ACTIVE_NAME));
            return source.getProperty(Constants.BOOT_ACTIVE_NAME);
        }
        if(System.getProperties().containsKey(Constants.BOOT_ACTIVE_NAME)){
            log.info("读取到java变量：{}={}", Constants.BOOT_ACTIVE_NAME, System.getProperty(Constants.BOOT_ACTIVE_NAME));
            return System.getProperty(Constants.BOOT_ACTIVE_NAME);
        }
        if(System.getenv().containsKey(Constants.BOOT_ACTIVE_NAME)){
            log.info("读取到系统变量：{}={}", Constants.BOOT_ACTIVE_NAME, System.getenv(Constants.BOOT_ACTIVE_NAME));
            return System.getenv(Constants.BOOT_ACTIVE_NAME);
        }
        log.warn("未读取到{}，默认取环境：{}", Constants.BOOT_ACTIVE_NAME, Constants.BOOT_ACTIVE_DEFAULT_VALUE);
        //logback-boot.xml中根据环境变量配置日志是否输出到控制台时，使用此配置
        System.setProperty(Constants.BOOT_ACTIVE_NAME, Constants.BOOT_ACTIVE_DEFAULT_VALUE);
        return Constants.BOOT_ACTIVE_DEFAULT_VALUE;
    }

    public static void main(String[] args) {
        //SpringApplication.run(BootRun.class, args);
        //new SpringApplicationBuilder().sources(BootRun.class).profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
        new SpringApplicationBuilder().sources(BootRun.class)
                .listeners(new ApplicationStartedEventListener())
                .listeners(new ApplicationEnvironmentPreparedEventListener())
                .listeners(new ApplicationPreparedEventListener())
                .listeners(new ApplicationFailedEventListener())
                .profiles(getProfile(new SimpleCommandLinePropertySource(args)))
                .run(args);
    }
}