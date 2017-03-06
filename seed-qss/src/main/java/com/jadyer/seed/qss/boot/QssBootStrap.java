package com.jadyer.seed.qss.boot;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2017/3/4 18:39.
 */
@EntityScan(basePackages="${scan.base.packages}")
@EnableJpaRepositories(basePackages="${scan.base.packages}")
@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class QssBootStrap extends SpringBootServletInitializer {
    //启动时不能直接执行main
    //具体启动方式见https://jadyer.github.io/2016/07/29/idea-springboot-jsp/
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(QssBootStrap.class).profiles("local").run(args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(getClass());
    }

    @Bean
    public Filter characterEncodingFilter(){
        return new CharacterEncodingFilter("UTF-8", true);
    }

    @Bean
    public Scheduler getScheduler() throws SchedulerException {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        Scheduler scheduler = schedulerFactory.getScheduler();
        scheduler.start();
        return scheduler;
    }

    @Bean
    public HttpMessageConverters fastjsonConverter(){
        List<SerializerFeature> serializerFeatureList = new ArrayList<>();
        serializerFeatureList.add(SerializerFeature.PrettyFormat);
        serializerFeatureList.add(SerializerFeature.QuoteFieldNames);
        serializerFeatureList.add(SerializerFeature.WriteMapNullValue);
        serializerFeatureList.add(SerializerFeature.WriteNullListAsEmpty);
        serializerFeatureList.add(SerializerFeature.WriteNullNumberAsZero);
        serializerFeatureList.add(SerializerFeature.WriteNullStringAsEmpty);
        serializerFeatureList.add(SerializerFeature.WriteNullBooleanAsFalse);
        serializerFeatureList.add(SerializerFeature.WriteDateUseDateFormat);
        SerializerFeature[] serializerFeatures = new SerializerFeature[serializerFeatureList.size()];
        serializerFeatureList.toArray(serializerFeatures);
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(serializerFeatures);
        FastJsonHttpMessageConverter4 fastjson = new FastJsonHttpMessageConverter4();
        fastjson.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastjson);
    }
}