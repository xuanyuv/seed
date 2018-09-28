package com.jadyer.seed.mpp.boot;

import com.jadyer.seed.comm.constant.SeedConstants;
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
 * Created by 玄玉<https://jadyer.cn/> on 2016/6/26 11:03.
 */
@Configuration
@ConfigurationProperties(prefix="auth")
public class AuthConfiguration {
    private String unauthUrl;
    private List<String> anonyList= new ArrayList<>();

    public String getUnauthUrl() {
        return unauthUrl;
    }

    public void setUnauthUrl(String unauthUrl) {
        this.unauthUrl = unauthUrl;
    }

    public List<String> getAnonyList() {
        return anonyList;
    }

    @Bean
    public Filter authFilter(){
        return new AuthFilter(this.unauthUrl, this.anonyList);
    }


    /**
     * 权限验证
     * Created by 玄玉<https://jadyer.cn/> on 2014/11/3 10:39.
     */
    private static class AuthFilter extends OncePerRequestFilter {
        private String unauthUrl;
        private List<String> anonyList;

        AuthFilter(String unauthUrl, List<String> anonyList){
            this.unauthUrl = unauthUrl;
            this.anonyList = anonyList;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            boolean disallowAnonymousVisit = true;
            for(String anonymousResource : this.anonyList){
                if(anonymousResource.equals(request.getServletPath())){
                    disallowAnonymousVisit = false;
                    break;
                }
                if(anonymousResource.endsWith("/**") && request.getServletPath().startsWith(anonymousResource.replace("/**", ""))){
                    disallowAnonymousVisit = false;
                    break;
                }
            }
            if(disallowAnonymousVisit && null==request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER)){
                response.sendRedirect(request.getContextPath() + this.unauthUrl);
            }else{
                filterChain.doFilter(request, response);
            }
        }
    }
}