package com.jadyer.seed.qss.boot;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;

@Configuration
public class FastjsonConfiguration {
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverter(){
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFastJsonConfig(new FastJsonConfig(){
            {
                setCharset(StandardCharsets.UTF_8);
                setDateFormat("yyyy-MM-dd HH:mm:ss");
                setSerializerFeatures(
                    SerializerFeature.PrettyFormat,
                    SerializerFeature.QuoteFieldNames,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteNullListAsEmpty,
                    SerializerFeature.WriteNullNumberAsZero,
                    SerializerFeature.WriteNullStringAsEmpty,
                    SerializerFeature.WriteNullBooleanAsFalse,
                    SerializerFeature.WriteDateUseDateFormat,
                    SerializerFeature.DisableCircularReferenceDetect
                );
            }
        });
        return new HttpMessageConverters(fastjson);
    }
}