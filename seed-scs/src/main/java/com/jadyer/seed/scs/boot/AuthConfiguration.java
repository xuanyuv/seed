package com.jadyer.seed.scs.boot;

import com.jadyer.seed.comm.constant.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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
        return new AuthFilter("/", this.anonymousList);
    }


    /**
     * 权限验证
     * Created by 玄玉<https://jadyer.github.io/> on 2014/11/3 10:39.
     */
    private static class AuthFilter extends OncePerRequestFilter {
        private String url;
        private List<String> anonymousList = new ArrayList<>();

        AuthFilter(String url, List<String> anonymousList){
            this.url = url;
            this.anonymousList = anonymousList;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            boolean disallowAnonymousVisit = true;
            for(String anonymousResource : anonymousList){
                if(anonymousResource.equals(request.getServletPath())){
                    disallowAnonymousVisit = false;
                    break;
                }
                if(anonymousResource.endsWith("/**") && request.getServletPath().startsWith(anonymousResource.replace("/**", ""))){
                    disallowAnonymousVisit = false;
                    break;
                }
            }
            if(disallowAnonymousVisit && null==request.getSession().getAttribute(Constants.WEB_SESSION_USER)){
                response.sendRedirect(request.getContextPath() + this.url);
            }else{
                filterChain.doFilter(request, response);
            }
        }
    }
}