package com.jadyer.seed.mpp.sdk.qq.filter;

import com.jadyer.seed.comm.util.JadyerUtil;
import com.jadyer.seed.mpp.sdk.util.HttpUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * <p>
 *     自行提供appid和appurl，请求参数需包含appid=123456789&oauth=base&openid=openid
 * </p>
 * Created by 玄玉<https://jadyer.github.io/> on 2015/12/24 12:12.
 */
public class QQFilterDemo implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(QQFilterDemo.class);

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
			String userAgent = request.getHeader("User-Agent");
			if(!userAgent.contains("QQ") || (!userAgent.contains("iPhone") && !userAgent.contains("Android"))){
				response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
				response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
				response.setHeader("Cache-Control", "no-cache");
				response.setHeader("Pragma", "no-cache");
				response.setDateHeader("Expires", 0);
				PrintWriter out = response.getWriter();
				out.print("请于iPhone或Android手机QQ端访问");
				out.flush();
				out.close();
				return;
			}
			String fullURL = request.getRequestURL().toString() + (null==request.getQueryString()?"":"?"+request.getQueryString());
			String state = fullURL.replace("?", "/").replaceAll("&", "/").replace("/oauth=base", "");
			logger.info("计算粉丝请求的资源得到state=[{}]", state);
			//String appurl = ConfigUtil.INSTANCE.getProperty("appurl");
			String appurl = null;
			String redirectURL = "https://open.mp.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + (appurl+"/qq/helper/oauth/"+appid) + "&response_type=code&scope=snsapi_base&state=" + state + "#qq_redirect";
			logger.info("计算请求到QQ服务器地址redirectURL=[{}]", redirectURL);
			response.sendRedirect(redirectURL);
		}else{
			chain.doFilter(req, resp);
		}
	}
}