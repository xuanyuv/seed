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
 * Created by 玄玉<http://jadyer.cn/> on 2016/6/26 11:03.
 */
@Configuration
@ConfigurationProperties(prefix="auth")
public class AuthConfiguration {
    private String unauthorizedUrl;
    private List<String> anonymousList = new ArrayList<>();

    public String getUnauthorizedUrl() {
        return unauthorizedUrl;
    }

    public void setUnauthorizedUrl(String unauthorizedUrl) {
        this.unauthorizedUrl = unauthorizedUrl;
    }

    public List<String> getAnonymousList() {
        return anonymousList;
    }


    @Bean
    public Filter authenticationFilter(){
        return new AuthFilter(this.unauthorizedUrl, this.anonymousList);
    }


    /**
     * 权限验证
     * Created by 玄玉<http://jadyer.cn/> on 2014/11/3 10:39.
     */
    private static class AuthFilter extends OncePerRequestFilter {
        private String unauthorizedUrl;
        private List<String> anonymousList = new ArrayList<>();

        AuthFilter(String unauthorizedUrl, List<String> anonymousList){
            this.unauthorizedUrl = unauthorizedUrl;
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
            if(request.getServletPath().contains("/portal/")){
                disallowAnonymousVisit = false;
            }
            if(disallowAnonymousVisit && null==request.getSession().getAttribute(Constants.WEB_SESSION_USER)){
                response.sendRedirect(request.getContextPath() + this.unauthorizedUrl);
            }else{
                filterChain.doFilter(request, response);
            }
        }
    }
}