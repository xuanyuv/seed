package com.jadyer.seed.mpp.sdk.weixin.helper;

import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.comm.util.RequestUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
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
 * 用于处理微信相关的Filter
 * <ul>
 *     目前该Filter主要用来网页授权获取粉丝信息，这里有两种方式（第二种不经过该Filter适用于前端应用与mpp是分离的情况）
 *     <li>经过该Filter进行处理：菜单地址配置示例为http://jadyer.cn/mpp/portal/user/reg.html?appid=wx63ae5326e400cca2&oauth=base&openid=openid</li>
 *     <li>不经过该Filter的处理：菜单地址配置示例为https://open.weixin.qq.com/connect/oauth2/authorize?appid={appid}&redirect_uri=http://jadyer.cn/mpp/weixin/helper/oauth/{appid}&response_type=code&scope=snsapi_base&state=http://jadyer.cn/mpp/portal/user/reg.html/uid=50/openid=openid#wechat_redirect</li>
 * </ul>
 * Created by 玄玉<http://jadyer.cn/> on 2015/10/19 16:45.
 */
public class WeixinFilter implements Filter {
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
            if(!RequestUtil.isWechatBrowser(request)){
                response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
                response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);
                PrintWriter out = response.getWriter();
                out.print("请于iPhone或Android微信端访问");
                out.flush();
                out.close();
                return;
            }
            /*
             * state=http://jadyer.cn/mpp/user/get/2/uname=玄玉/openid=openid
             */
            String fullURL = request.getRequestURL().toString() + (null==request.getQueryString()?"":"?"+request.getQueryString());
            String state = fullURL.replace("?", "/").replaceAll("&", "/").replace("/oauth=base", "");
            LogUtil.getLogger().info("计算粉丝请求的资源得到state=[{}]", state);
            String redirectURL = WeixinHelper.buildWeixinOAuthCodeURL(appid, WeixinConstants.WEIXIN_OAUTH_SCOPE_SNSAPI_BASE, state, RequestUtil.getFullContextPath(request)+"/weixin/helper/oauth/"+appid);
            LogUtil.getLogger().info("计算请求到微信服务器的redirectURL=[{}]", redirectURL);
            response.sendRedirect(redirectURL);
        }else{
            chain.doFilter(req, resp);
        }
    }
}