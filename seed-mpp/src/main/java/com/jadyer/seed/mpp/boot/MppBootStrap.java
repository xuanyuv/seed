package com.jadyer.seed.mpp.boot;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter4;
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

@EntityScan(basePackages="${scan.base.packages}")
@EnableJpaRepositories(basePackages="${scan.base.packages}")
@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class MppBootStrap extends SpringBootServletInitializer {
    //启动时不能直接执行main
    //具体启动方式见https://jadyer.github.io/2016/07/29/idea-springboot-jsp/
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(MppBootStrap.class).profiles("local").run(args);
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
        FastJsonHttpMessageConverter4 fastjson = new FastJsonHttpMessageConverter4();
        fastjson.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastjson);
    }


    /*
    <!-- 网页授权 -->
	<!-- http://www.jadyer.com/mpp/qq/getopenid?test=7645&appid=123456789&oauth=base&openid=openid -->
	<!-- http://www.jadyer.com/mpp/weixin/getopenid?test=7645&appid=wx63ae5326e400cca2&oauth=base&openid=openid -->
	<filter>
		<filter-name>QQFilter</filter-name>
		<filter-class>com.jadyer.sdk.qq.filter.QQFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>QQFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
	<filter>
		<filter-name>WeixinFilter</filter-name>
		<filter-class>com.jadyer.sdk.weixin.filter.WeixinFilter</filter-class>
	</filter>
	<filter-mapping>
		<filter-name>WeixinFilter</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
    */
}