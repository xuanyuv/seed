package com.jadyer.seed.mpp.sdk.weixin.filter;

import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
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
 * @create Oct 19, 2015 4:45:35 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinFilter implements Filter {
    @Override
    public void destroy() {}

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    /**
     * 判断是否需要通过网页授权获取粉丝信息
     * @see 1.请求参数需包含appid=wx63ae5326e400cca2&oauth=base&openid=openid
     * @see 2.该Filter常用于自定义菜单跳转URL时获取粉丝的openid,故验证条件较为苛刻
     */
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
             * state=http://www.jadyer.com/seed/user/get/2/uname=玄玉/openid=openid
             */
            String fullURL = request.getRequestURL().toString() + (null==request.getQueryString()?"":"?"+request.getQueryString());
            String state = fullURL.replace("?", "/").replaceAll("&", "/").replace("/oauth=base", "");
            LogUtil.getLogger().info("计算粉丝请求的资源得到state=[{}]", state);
            //String appurl = http://www.jadyer.com/mpp
            StringBuilder appurl = new StringBuilder();
            appurl.append(request.getScheme()).append("://").append(request.getServerName());
            if(80!=request.getServerPort() && 443!=request.getServerPort()){
                appurl.append(":").append(request.getServerPort());
            }
            appurl.append(request.getContextPath());
            String redirectURL = WeixinHelper.buildWeixinOAuthCodeURL(appid, WeixinConstants.WEIXIN_OAUTH_SCOPE_SNSAPI_BASE, state, appurl.toString()+"/weixin/helper/oauth/"+appid);
            LogUtil.getLogger().info("计算请求到微信服务器的redirectURL=[{}]", redirectURL);
            response.sendRedirect(redirectURL);
        }else{
            chain.doFilter(req, resp);
        }
    }
}