package com.jadyer.seed.seedoc.boot;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastjsonConfiguration {
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