package com.jadyer.seed.boot;

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
    public HttpMessageConverters fastjsonConverter(){
        FastJsonConfig config = new FastJsonConfig();
        config.setCharset(StandardCharsets.UTF_8);      // 默认就是com.alibaba.fastjson.util.IOUtils.UTF8
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");    // 默认就是[yyyy-MM-dd HH:mm:ss]
        config.setSerializerFeatures(                   // 设置序列化输出时的一些额外属性
                SerializerFeature.PrettyFormat,             // 格式化输出（默认只会输出成一行的字符串）
                SerializerFeature.QuoteFieldNames,          // 输出key时是否使用双引号，默认为true
                SerializerFeature.WriteMapNullValue,        // 是否输出值为null的字段
                SerializerFeature.WriteNullListAsEmpty,     // Collection字段如果为null，输出为[]，而非null
                SerializerFeature.WriteNullNumberAsZero,    // 数值字段如果为null，输出为0，而非null
                SerializerFeature.WriteNullStringAsEmpty,   // 字符类型字段如果为null，输出为""，而非null
                SerializerFeature.WriteNullBooleanAsFalse,  // Boolean字段如果为null，输出为false，而非null
                SerializerFeature.WriteDateUseDateFormat,   // 使用默认的日期格式[yyyy-MM-dd HH:mm:ss]输出Date类型，未配置则会输出为1484030642746
                // SerializerFeature.WriteBigDecimalAsPlain,      // 测试发现，无论是否设置该属性，都会输出：["age":0]、["age":123.456]
                // SerializerFeature.WriteNonStringValueAsString, // 设置该属性会使得在输出时以字符串来输出非字符串的值，比如["age":"0"]、["abc":"false"]，但是List不是这样，还是会输出["goodsList":[]]
                // SerializerFeature.WriteClassName,              // 假设序列化的实体类为com.jadyer.demo.open.model.ReqData，设置该属性会使得输出的json中增加一个key=["@type":"com.jadyer.demo.open.model.ReqData"]
                // SerializerFeature.BrowserSecure,               // 设置该属性会使得序列化时增加特殊处理字符处理，比如原本的["mytime":"2017-01-10 14:56:41"]会输出为["mytime":"2017\u002D01\u002D10\u002014\u003A56\u003A41"]
                SerializerFeature.DisableCircularReferenceDetect  // 禁用循环引用
        );
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFastJsonConfig(config);
        return new HttpMessageConverters(fastjson);
    }
}
/*
/**
 * https://stackoverflow.com/questions/44121648/controlleradvice-responsebodyadvice-failed-to-enclose-a-string-response
 *
// @Configuration
public class FastjsonConfiguration implements WebMvcConfigurer {
    /**
     * 用Fastjson替换掉Jackson
     *
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // converters.add(0, fastJsonHttpMessageConverter);
        //转换器是List，新加的fastjson会在最后，为防止先匹配到jackson转换器，就先删掉它
        for(int i=converters.size()-1; i>=0; i--){
            if(converters.get(i) instanceof MappingJackson2HttpMessageConverter){
                converters.remove(i);
            }
        }
        FastJsonConfig config = new FastJsonConfig();
        config.setCharset(StandardCharsets.UTF_8);      // 默认就是com.alibaba.fastjson.util.UTF8
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");    // 默认就是[yyyy-MM-dd HH:mm:ss]
        config.setSerializerFeatures(                   // 设置序列化输出时的一些额外属性
                SerializerFeature.PrettyFormat,             // 格式化输出（默认只会输出成一行的字符串）
                SerializerFeature.QuoteFieldNames,          // 输出key时是否使用双引号，默认为true
                SerializerFeature.WriteMapNullValue,        // 是否输出值为null的字段
                SerializerFeature.WriteNullListAsEmpty,     // Collection字段如果为null，输出为[]，而非null
                SerializerFeature.WriteNullNumberAsZero,    // 数值字段如果为null，输出为0，而非null
                SerializerFeature.WriteNullStringAsEmpty,   // 字符类型字段如果为null，输出为""，而非null
                SerializerFeature.WriteNullBooleanAsFalse,  // Boolean字段如果为null，输出为false，而非null
                SerializerFeature.WriteDateUseDateFormat,   // 使用默认的日期格式[yyyy-MM-dd HH:mm:ss]输出Date类型，未配置则会输出为1484030642746
                // SerializerFeature.WriteBigDecimalAsPlain,      // 测试发现，无论是否设置该属性，都会输出：["age":0]、["age":123.456]
                // SerializerFeature.WriteNonStringValueAsString, // 设置该属性会使得在输出时以字符串来输出非字符串的值，比如["age":"0"]、["abc":"false"]，但是List不是这样，还是会输出["goodsList":[]]
                // SerializerFeature.WriteClassName,              // 假设序列化的实体类为com.jadyer.demo.open.model.ReqData，设置该属性会使得输出的json中增加一个key=["@type":"com.jadyer.demo.open.model.ReqData"]
                // SerializerFeature.BrowserSecure,               // 设置该属性会使得序列化时增加特殊处理字符处理，比如原本的["mytime":"2017-01-10 14:56:41"]会输出为["mytime":"2017\u002D01\u002D10\u002014\u003A56\u003A41"]
                SerializerFeature.DisableCircularReferenceDetect  // 禁用循环引用
        );
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFastJsonConfig(config);
        converters.add(fastjson);
    }
}
*/