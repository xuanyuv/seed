package com.jadyer.seed.comm;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * https://stackoverflow.com/questions/44121648/controlleradvice-responsebodyadvice-failed-to-enclose-a-string-response
 */
@Configuration
public class FastjsonConfiguration implements WebMvcConfigurer {
    /**
     * 用Fastjson替换掉Jackson
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //新加的fastjson会在转换器List的尾部，为防止先匹配到jackson转换器，就先删掉它
        for(int i=converters.size()-1; i>=0; i--){
            if(converters.get(i) instanceof MappingJackson2HttpMessageConverter){
                converters.remove(i);
            }
        }
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFastJsonConfig(new FastJsonConfig(){
            {
                setCharset(StandardCharsets.UTF_8);             // 默认就是com.alibaba.fastjson.util.IOUtils.UTF8
                setDateFormat("yyyy-MM-dd HH:mm:ss");           // 默认就是[yyyy-MM-dd HH:mm:ss]
                setSerializerFeatures(getSerializerFeatures()); // 设置序列化输出时的一些额外属性
            }
        });
        // fastjson放在转换器List的首位
        converters.add(0, fastjson);
    }

    public static SerializerFeature[] getSerializerFeatures(){
        SerializerFeature[] sfs = new SerializerFeature[9];
        sfs[0] = SerializerFeature.PrettyFormat;            // 格式化输出（默认只会输出成一行的字符串）
        sfs[1] = SerializerFeature.QuoteFieldNames;         // 输出key时是否使用双引号，默认为true
        sfs[2] = SerializerFeature.WriteMapNullValue;       // 是否输出值为null的字段
        sfs[3] = SerializerFeature.WriteNullListAsEmpty;    // Collection字段如果为null，输出为[]，而非null
        sfs[4] = SerializerFeature.WriteNullNumberAsZero;   // 数值字段如果为null，输出为0，而非null
        sfs[5] = SerializerFeature.WriteNullStringAsEmpty;  // 字符类型字段如果为null，输出为""，而非null
        sfs[6] = SerializerFeature.WriteNullBooleanAsFalse; // Boolean字段如果为null，输出为false，而非null
        sfs[7] = SerializerFeature.WriteDateUseDateFormat;  // 使用默认的日期格式[yyyy-MM-dd HH:mm:ss]输出Date类型，未配置则会输出为1484030642746
        sfs[8] = SerializerFeature.DisableCircularReferenceDetect; // 禁用循环引用
        // SerializerFeature.WriteBigDecimalAsPlain,               // 测试发现，无论是否设置该属性，都会输出：["age":0]、["age":123.456]
        // SerializerFeature.WriteNonStringValueAsString,          // 设置该属性会使得在输出时以字符串来输出非字符串的值，比如["age":"0"]、["abc":"false"]，但是List不是这样，还是会输出["goodsList":[]]
        // SerializerFeature.WriteClassName,                       // 假设序列化的实体类为com.jadyer.demo.open.model.ReqData，设置该属性会使得输出的json中增加一个key=["@type":"com.jadyer.demo.open.model.ReqData"]
        // SerializerFeature.BrowserSecure,                        // 设置该属性会使得序列化时增加特殊处理字符处理，比如原本的["mytime":"2017-01-10 14:56:41"]会输出为["mytime":"2017\u002D01\u002D10\u002014\u003A56\u003A41"]
        return sfs;
    }
}