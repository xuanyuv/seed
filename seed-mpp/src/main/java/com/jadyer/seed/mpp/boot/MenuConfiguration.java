package com.jadyer.seed.mpp.boot;

import com.jadyer.seed.comm.constant.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2017/3/11 11:30.
 */
@Configuration
public class MenuConfiguration {
    @Bean
    public Filter menuFilter(){
        return new MenuFilter();
    }

    /**
     * 后台管理页面菜单高亮焦点处理
     * Created by 玄玉<http://jadyer.cn/> on 2017/3/11 11:32.
     */
    private static class MenuFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_sys");
            if(request.getServletPath().startsWith("/fans")){
                request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_fans");
            }
            if(request.getServletPath().startsWith("/mpp")){
                request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_mpp");
            }
            if(request.getServletPath().startsWith("/view")){
                String url = request.getParameter("url");
                if(url.startsWith("/admin/mpp")){
                    request.getSession().setAttribute(Constants.WEB_CURRENT_MENU, "menu_mpp");
                }
            }
            filterChain.doFilter(request, response);
        }
    }
}