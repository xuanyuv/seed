package com.jadyer.seed.server.boot;

import com.jadyer.seed.server.core.ServerProtocolCodecFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilterChainBuilder;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

@SpringBootApplication(scanBasePackages="${scan.base.packages}")
public class ServerBootStrap extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(getClass());
    }

    @Bean
    public Filter characterEncodingFilter(){
        return new CharacterEncodingFilter("UTF-8", true);
    }

    @Bean
    public IoFilterChainBuilder getIoFilterChainBuilder(){
        DefaultIoFilterChainBuilder builder = new DefaultIoFilterChainBuilder();
        builder.addLast("logger", new LoggingFilter());
        builder.addLast("codec", new ProtocolCodecFilter(new ServerProtocolCodecFactory()));
        builder.addLast("executor", new ExecutorFilter());
        return builder;
    }
}