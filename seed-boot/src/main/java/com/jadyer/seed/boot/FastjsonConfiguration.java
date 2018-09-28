package com.jadyer.seed.boot;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
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
        //格式化输出（默认只会输出成一行的字符串）
        serializerFeatureList.add(SerializerFeature.PrettyFormat);
        //输出key时是否使用双引号，默认为true
        serializerFeatureList.add(SerializerFeature.QuoteFieldNames);
        //是否输出值为null的字段，默认为true
        serializerFeatureList.add(SerializerFeature.WriteMapNullValue);
        //List字段如果为null，输出为[]，而非null
        serializerFeatureList.add(SerializerFeature.WriteNullListAsEmpty);
        //数值字段如果为null，输出为0，而非null
        serializerFeatureList.add(SerializerFeature.WriteNullNumberAsZero);
        //字符类型字段如果为null，输出为""，而非null
        serializerFeatureList.add(SerializerFeature.WriteNullStringAsEmpty);
        //Boolean字段如果为null，输出为false，而非null
        serializerFeatureList.add(SerializerFeature.WriteNullBooleanAsFalse);
        //使用默认的日期格式[yyyy-MM-dd HH:mm:ss]输出Date类型，未指定该属性则会将java.util.Date类型输出为1484030642746
        serializerFeatureList.add(SerializerFeature.WriteDateUseDateFormat);
        ////测试发现，无论是否设置该属性，都会输出：["age":0]、["age":123.456]
        //serializerFeatureList.add(SerializerFeature.WriteBigDecimalAsPlain);
        ////设置该属性会使得在输出时以字符串来输出非字符串的值，比如["age":"0"]、["abc":"false"]，但是List不是这样，还是会输出["goodsList":[]]
        //serializerFeatureList.add(SerializerFeature.WriteNonStringValueAsString);
        ////假设序列化的实体类为com.jadyer.demo.open.model.ReqData，设置该属性会使得输出的json中增加一个key=["@type":"com.jadyer.demo.open.model.ReqData",]
        //serializerFeatureList.add(SerializerFeature.WriteClassName);
        ////设置该属性会使得序列化时增加特殊处理字符处理，比如原本的["mytime":"2017-01-10 14:56:41",]会输出为["mytime":"2017\u002D01\u002D10\u002014\u003A56\u003A41",]
        //serializerFeatureList.add(SerializerFeature.BrowserSecure);
        SerializerFeature[] serializerFeatures = new SerializerFeature[serializerFeatureList.size()];
        serializerFeatureList.toArray(serializerFeatures);

        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        //Fastjson默认就是UTF-8
        fastJsonConfig.setCharset(StandardCharsets.UTF_8);
        //Fastjson默认就是[yyyy-MM-dd HH:mm:ss]
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        //设置序列化输出时的一些额外属性
        fastJsonConfig.setSerializerFeatures(serializerFeatures);

        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFastJsonConfig(fastJsonConfig);
        return new HttpMessageConverters(fastjson);
    }
}