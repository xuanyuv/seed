package com.jadyer.seed.mpp.sdk.weixin.helper;

import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
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
            if(JadyerUtil.isAjaxRequest(request)){
                throw new RuntimeException("请不要通过Ajax获取粉丝信息");
            }
            /*
             * @see 1.IE-11.0.9600.17843
             * @see   User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; rv:11.0) like Gecko
             * @see 2.Chrome-46.0.2490.71 m (64-bit)
             * @see   User-Agent: Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.71 Safari/537.36
             * @see 3.Windows-1.5.0.22
             * @see   User-Agent: Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36 MicroMessenger/6.5.2.501 NetType/WIFI WindowsWechat
             * @see 4.IOS-WeChat-6.3.1
             * @see   User-Agent: Mozilla/5.0 (iPhone; CPU iPhone OS 7_1_1 like Mac OS X) AppleWebKit/537.51.2 (KHTML, like Gecko) Mobile/11D201 MicroMessenger/6.3.1 NetType/WIFI Language/en
             * @see 5.Android-WeChat-6.2.6
             * @see   User-Agent: Mozilla/5.0 (Linux; U; Android 4.4.2; zh-cn; H60-L01 Build/HDH60-L01) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 MQQBrowser/5.4 TBS/025469 Mobile Safari/533.1 MicroMessenger/6.2.5.54_re87237d.622 NetType/WIFI Language/zh_CN
             */
            String userAgent = request.getHeader("User-Agent");
            LogUtil.getLogger().info("网页授权获取粉丝信息时请求的User-Agent=[{}]", userAgent);
            if(!userAgent.contains("MicroMessenger") || (!userAgent.contains("iPhone") && !userAgent.contains("Android"))){
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
            String redirectURL = WeixinHelper.buildWeixinOAuthCodeURL(appid, WeixinConstants.WEIXIN_OAUTH_SCOPE_SNSAPI_BASE, state, JadyerUtil.getFullContextPath(request)+"/weixin/helper/oauth/"+appid);
            LogUtil.getLogger().info("计算请求到微信服务器的redirectURL=[{}]", redirectURL);
            response.sendRedirect(redirectURL);
        }else{
            chain.doFilter(req, resp);
        }
    }
}