package com.jadyer.seed.scs.base;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/6/26 11:03.
 */
@Configuration
@ConfigurationProperties(prefix="auth")
public class AuthConfiguration {
    private List<String> anonymousList = new ArrayList<>();

    public List<String> getAnonymousList() {
        return anonymousList;
    }

    @Bean
    public Filter authenticationFilter(){
        return new AuthFilter("/login.jsp", this.anonymousList);
    }
}