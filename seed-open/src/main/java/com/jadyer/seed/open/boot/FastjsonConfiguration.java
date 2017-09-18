package com.jadyer.seed.open.boot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class FastjsonConfiguration {
    //@Bean
    //public HttpMessageConverters fastjsonConverter(){
    //    List<SerializerFeature> serializerFeatureList = new ArrayList<>();
    //    serializerFeatureList.add(SerializerFeature.PrettyFormat);
    //    serializerFeatureList.add(SerializerFeature.QuoteFieldNames);
    //    serializerFeatureList.add(SerializerFeature.WriteMapNullValue);
    //    serializerFeatureList.add(SerializerFeature.WriteNullListAsEmpty);
    //    serializerFeatureList.add(SerializerFeature.WriteNullNumberAsZero);
    //    serializerFeatureList.add(SerializerFeature.WriteNullStringAsEmpty);
    //    serializerFeatureList.add(SerializerFeature.WriteNullBooleanAsFalse);
    //    serializerFeatureList.add(SerializerFeature.WriteDateUseDateFormat);
    //    SerializerFeature[] serializerFeatures = new SerializerFeature[serializerFeatureList.size()];
    //    serializerFeatureList.toArray(serializerFeatures);
    //    FastJsonConfig fastJsonConfig = new FastJsonConfig();
    //    fastJsonConfig.setCharset(StandardCharsets.UTF_8);
    //    fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
    //    fastJsonConfig.setSerializerFeatures(serializerFeatures);
    //    FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
    //    fastjson.setFastJsonConfig(fastJsonConfig);
    //    return new HttpMessageConverters(fastjson);
    //}


    @Bean
    public HttpMessageConverters fastjsonConverter(){
        FastJsonHttpMessageConverter fastjson = new FastJsonHttpMessageConverter();
        fastjson.setFeatures(SerializerFeature.PrettyFormat);
        fastjson.setFeatures(SerializerFeature.WriteMapNullValue);
        fastjson.setFeatures(SerializerFeature.WriteNullListAsEmpty);
        fastjson.setFeatures(SerializerFeature.WriteNullNumberAsZero);
        fastjson.setFeatures(SerializerFeature.WriteNullStringAsEmpty);
        fastjson.setFeatures(SerializerFeature.WriteNullBooleanAsFalse);
        fastjson.setFeatures(SerializerFeature.WriteDateUseDateFormat);
        return new HttpMessageConverters(fastjson);
    }


    static class FastJsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {
        final static Charset UTF8     = Charset.forName("UTF-8");
        private Charset             charset  = UTF8;
        private List<SerializerFeature> features = new ArrayList<SerializerFeature>();

        FastJsonHttpMessageConverter(){
            super(new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8));
        }

        @Override
        protected boolean supports(Class<?> clazz) {
            return true;
        }

        public Charset getCharset() {
            return charset;
        }

        public void setCharset(Charset charset) {
            this.charset = charset;
        }

        SerializerFeature[] getFeatures() {
            return this.features.toArray(new SerializerFeature[this.features.size()]);
        }

        void setFeatures(SerializerFeature features) {
            this.features.add(features);
        }

        @Override
        protected Object readInternal(Class<? extends Object> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            InputStream in = inputMessage.getBody();
            byte[] buf = new byte[1024];
            for (;;) {
                int len = in.read(buf);
                if (len == -1) {
                    break;
                }
                if (len > 0) {
                    baos.write(buf, 0, len);
                }
            }
            byte[] bytes = baos.toByteArray();
            return JSON.parseObject(bytes, 0, bytes.length, charset.newDecoder(), clazz);
        }

        @Override
        protected void writeInternal(Object obj, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            OutputStream out = outputMessage.getBody();
            String text = JSON.toJSONString(obj, this.getFeatures());
            byte[] bytes = text.getBytes(charset);
            out.write(bytes);
        }
    }
}