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
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

/**
 * -------------------------------------------------------------------------------------------------------
 * 发布成war
 * 1、<packaging>war</packaging>
 * 2、mvn clean install -DskipTests
 * 此时只需要com.jadyer.seed.boot.BootStrap.java，本类就不需要了，也不需要配置spring-boot-maven-plugin
 * -------------------------------------------------------------------------------------------------------
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
 * -------------------------------------------------------------------------------------------------------
 * 关于可执行jar中解析jsp
 * SpringBoot官方称：可执行jar是无法解析jsp的，不过也有人找到一种有理有据的方法，使它能够解析jsp
 * 详细描述见：https://dzone.com/articles/spring-boot-with-jsps-in-executable-jars-1
 * 总结起来就是：将jsp放在/src/main/resources/META-INF/resources/目录下
 * 实际测试发现：执行jar后访问jsp，不好使，还是看到404的那个白板页面
 * 但是在 IntelliJ IDEA（即本地IDE）中测试发现，它是可以访问的，只不过优先级没那么高
 * idea会优先读取/src/main/webapp/下的文件，找不到时才会去找/src/main/resources/META-INF/resources/下的文件
 * -------------------------------------------------------------------------------------------------------
 * IntelliJ IDEA 中通过本类启动应用的方式，有三种
 * 1、http://jadyer.cn/2016/07/29/idea-springboot-jsp/
 * 2、直接运行com.jadyer.seed.test.BootStartup.java
 * 3、直接运行该类（不过会由于该pom配置了spring-boot-starter-tomcat导致tomcat冲突，报告java.lang.ClassNotFoundException: javax.servlet.Filter）
 * -------------------------------------------------------------------------------------------------------
 */
@SpringBootApplication(scanBasePackages="${scan.base.packages}", exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class BootRun {
    private static final Logger log = LoggerFactory.getLogger(BootRun.class);

    @Bean
    public Filter characterEncodingFilter(){
        return new CharacterEncodingFilter("UTF-8", true);
    }

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
        //new SpringApplicationBuilder().sources(BootRun.class)
        //        .profiles(getProfile(new SimpleCommandLinePropertySource(args)))
        //        .run(args);
        new SpringApplicationBuilder().sources(BootRun.class)
                .listeners(new ApplicationStartedEventListener())
                .listeners(new ApplicationEnvironmentPreparedEventListener())
                .listeners(new ApplicationPreparedEventListener())
                .listeners(new ApplicationFailedEventListener())
                .profiles(getProfile(new SimpleCommandLinePropertySource(args)))
                .run(args);
    }
}