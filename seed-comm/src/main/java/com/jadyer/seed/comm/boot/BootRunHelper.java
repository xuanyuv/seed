package com.jadyer.seed.comm.boot;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2022/2/14 16:09.
 */
public class BootRunHelper {
    private static final String BOOT_ACTIVE_NAME = "spring.profiles.active";
    private static final String BOOT_ACTIVE_DEFAULT_VALUE = "local";
    private static final Logger log = LoggerFactory.getLogger(BootRunHelper.class);

    private static String getProfile(SimpleCommandLinePropertySource source){
        if(source.containsProperty(BOOT_ACTIVE_NAME)){
            //补充：IntelliJ IDEA运行时，如果在Run/Debug Configurations为该启动类配置Program arguments的值为"--spring.profiles.active=dev"
            //那么这里就能读取到该配置，同时控制台会打印"读取到spring变量：spring.profiles.active=dev"
            log.info("读取到spring变量：{}={}", BOOT_ACTIVE_NAME, source.getProperty(BOOT_ACTIVE_NAME));
            return source.getProperty(BOOT_ACTIVE_NAME);
        }
        if(System.getProperties().containsKey(BOOT_ACTIVE_NAME)){
            log.info("读取到java变量：{}={}", BOOT_ACTIVE_NAME, System.getProperty(BOOT_ACTIVE_NAME));
            return System.getProperty(BOOT_ACTIVE_NAME);
        }
        if(System.getenv().containsKey(BOOT_ACTIVE_NAME)){
            log.info("读取到系统变量：{}={}", BOOT_ACTIVE_NAME, System.getenv(BOOT_ACTIVE_NAME));
            return System.getenv(BOOT_ACTIVE_NAME);
        }
        log.warn("未读取到{}，默认取环境：{}", BOOT_ACTIVE_NAME, BOOT_ACTIVE_DEFAULT_VALUE);
        //logback-boot.xml中根据环境变量配置日志是否输出到控制台时，使用此配置
        System.setProperty(BOOT_ACTIVE_NAME, BOOT_ACTIVE_DEFAULT_VALUE);
        return BOOT_ACTIVE_DEFAULT_VALUE;
    }


    private static void printRunInfo(ConfigurableApplicationContext applicationContext){
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
                        "\n\tApplication  : [{}] is running" +
                        "\n\tProfile  (s) : {}" +
                        "\n\tLocal    URL : {}://{}:{}{}" +
                        "\n\tExternal URL : {}://{}:{}{}" +
                        "\n------------------------------------------------------------",
                env.getProperty("spring.application.name"), env.getActiveProfiles(),
                protocol, "127.0.0.1", serverPort, contextPath,
                protocol, RequestUtil.getServerIP(), serverPort, contextPath);
    }


    public static void run(String[] args, Class<?> source, ApplicationListener<?>... listeners){
        // ConfigurableApplicationContext applicationContext = SpringApplication.run(BootRun.class, args);
        // ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder().sources(source)
        //         .listeners(new ApplicationStartingEventListener())
        //         .listeners(new ApplicationEnvironmentPreparedEventListener())
        //         .listeners(new ApplicationPreparedEventListener())
        //         .listeners(new ApplicationFailedEventListener())
        //         .listeners(new ApplicationPidFileWriter())
        //         .profiles(getProfile(new SimpleCommandLinePropertySource(args)))
        //         .run(args);
        SpringApplicationBuilder springApplicationBuilder = new SpringApplicationBuilder().sources(source);
        for (ApplicationListener<?> obj : listeners) {
            springApplicationBuilder.listeners(obj);
        }
        ConfigurableApplicationContext applicationContext = springApplicationBuilder.profiles(getProfile(new SimpleCommandLinePropertySource(args))).run(args);
        printRunInfo(applicationContext);
    }
}