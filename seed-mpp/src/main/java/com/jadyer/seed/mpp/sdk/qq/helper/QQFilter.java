package com.jadyer.seed.mpp.sdk.qq.helper;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import com.jadyer.seed.mpp.sdk.qq.constant.QQConstants;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 用于处理QQ相关的Filter
 * <ul>
 *     目前该Filter主要用来网页授权获取粉丝信息，这里有两种方式（第二种不经过该Filter适用于前端应用与mpp是分离的情况）
 *     <li>经过该Filter进行处理：菜单地址配置示例为https://jadyer.cn/mpp/portal/user/reg.html?appid=wx63ae5326e400cca2&oauth=base&openid=openid</li>
 *     <li>不经过该Filter的处理：菜单地址配置示例为https://open.mp.qq.com/connect/oauth2/authorize?appid={appid}&redirect_uri=https://jadyer.cn/mpp/qq/helper/oauth/{appid}&response_type=code&scope=snsapi_base&state=https://jadyer.cn/mpp/portal/user/reg.html/uid=50/openid=openid#qq_redirect</li>
 * </ul>
 * Created by 玄玉<https://jadyer.cn/> on 2015/12/24 12:00.
 */
public class QQFilter implements Filter {
    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String appid = request.getParameter("appid");
        if(StringUtils.isNotBlank(appid) && "base".equals(request.getParameter("oauth")) && "openid".equals(request.getParameter("openid"))){
            if(RequestUtil.isAjaxRequest(request)){
                throw new RuntimeException("请不要通过Ajax获取粉丝信息");
            }
            if(RequestUtil.isQQBrowser(request)){
                response.setCharacterEncoding(SeedConstants.DEFAULT_CHARSET);
                response.setContentType("text/plain; charset=" + SeedConstants.DEFAULT_CHARSET);
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                PrintWriter out = response.getWriter();
                out.print("请于iPhone或Android手机QQ端访问");
                out.flush();
                out.close();
                return;
            }
            /*
             * state=https://jadyer.cn/mpp/user/get/2/uname=玄玉/openid=openid
             */
            String fullURL = request.getRequestURL().toString() + (null==request.getQueryString()?"":"?"+request.getQueryString());
            String state = fullURL.replace("?", "/").replaceAll("&", "/").replace("/oauth=base", "");
            LogUtil.getLogger().info("计算粉丝请求的资源得到state=[{}]", state);
            String redirectURL = QQHelper.buildQQOAuthCodeURL(appid, QQConstants.QQ_OAUTH_SCOPE_SNSAPI_BASE, state, RequestUtil.getFullContextPath(request)+"/qq/helper/oauth/"+appid);
            LogUtil.getLogger().info("计算请求到QQ服务器地址redirectURL=[{}]", redirectURL);
            response.sendRedirect(redirectURL);
        }else{
            chain.doFilter(req, resp);
        }
    }
}