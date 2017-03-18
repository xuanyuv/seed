package com.jadyer.seed.boot;

import com.jadyer.seed.comm.constant.Constants;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
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
//@Configuration
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
     * <p>
     *     Session超时后，Ajax请求会被"Status Code:302 Found"到超时后的登录页面
     *     此时前台js中收到的Ajax结果是undefined，其实是没有得到应答，所以alert(jsonData.msg)时会弹出undefined
     * </p>
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
        /*
		 * http://121.199.22.212/JadyerWeb/user/get/2?uname=玄玉
		 * 121.199.22.212是服务器的IP，119.85.113.63是我的IP，即我本地访问服务器接口时本地的IP
         * 下面列出的是HttpServletRequest属性的控制台输出，输出值前后手工增加了[]
		 */
            //fullURL=request.getRequestURI() + (null==request.getQueryString()?"":"?"+request.getQueryString());
            //fullURL=[/JadyerWeb/user/get/2?uname=%E7%8E%84%E7%8E%89]
            //request.getRequestURI()=[/JadyerWeb/user/get/2]
            //request.getQueryString()=[uname=%E7%8E%84%E7%8E%89]
            //request.getContextPath()=[/JadyerWeb]
            //request.getServletPath()=[/user/get/2]
            //request.getRequestURL()=[http://121.199.22.212/JadyerWeb/user/get/2
            //request.getScheme()=[http]
            //request.getServerName()=[121.199.22.212]
            //request.getServerPort()=[80]
            //request.getLocalAddr()=[121.199.22.212]
            //request.getLocalName()=[AY140324164327Z]
            //request.getLocalPort()=[80]
            //request.getRemoteAddr()=[119.85.113.63]
            //request.getRemoteHost()=[119.85.113.63]
            //request.getRemotePort()=[18458]
            //request.getRemoteUser()=[null]
            //request.getRequestedSessionId()=[0812467359CA7599CDF09AD780F0804A]
            /*
             * 增加对[/js/**]模式的资源控制
             */
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
            //前后台同时存在的项目，默认/portal/为所有前台资源的前缀
            if(request.getServletPath().contains("/portal/")){
                disallowAnonymousVisit = false;
            }
            //处理权限访问
            if(disallowAnonymousVisit && null==request.getSession().getAttribute(Constants.WEB_SESSION_USER)){
                response.sendRedirect(request.getContextPath() + this.url);
            }else{
                filterChain.doFilter(request, response);
            }
        }
    }
}