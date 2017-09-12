package com.jadyer.seed.seedoc.boot;

import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/10 5:27.
 */
//@EntityScan(basePackages="${scan.base.packages}")
//@EnableJpaRepositories(basePackages="${scan.base.packages}")
@SpringBootApplication(scanBasePackages="${scan.base.packages}", exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class, DruidDataSourceAutoConfigure.class})
public class SeedocRun extends SpringBootServletInitializer {
    //启动方式：http://jadyer.cn/2016/07/29/idea-springboot-jsp/
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(SeedocRun.class).profiles("local").run(args);
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
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastjson);
    }
}