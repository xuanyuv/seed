package com.jadyer.seed.mpp.sdk.weixin.filter;

import com.jadyer.seed.comm.util.HttpUtil;
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
 * 用于处理微信相关的Filter
 * @see 自行提供appid和appurl,请求参数需包含appid=wx63ae5326e400cca2&oauth=base&openid=openid
 * @create Oct 19, 2015 4:45:35 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinFilterDemo implements Filter {
	private static final Logger logger = LoggerFactory.getLogger(WeixinFilter.class);

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
			if(this.isAjaxRequest(request)){
				throw new RuntimeException("请不要通过Ajax获取粉丝信息");
			}
			String userAgent = request.getHeader("User-Agent");
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
			String fullURL = request.getRequestURL().toString() + (null==request.getQueryString()?"":"?"+request.getQueryString());
			String state = fullURL.replace("?", "/").replaceAll("&", "/").replace("/oauth=base", "");
			logger.info("计算粉丝请求的资源得到state=[{}]", state);
			//String appurl = ConfigUtil.INSTANCE.getProperty("appurl");
			String appurl = null;
			String redirectURL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + (appurl+"/weixin/helper/oauth/"+appid) + "&response_type=code&scope=snsapi_base&state=" + state + "#wechat_redirect";
			logger.info("计算请求到微信服务器的redirectURL=[{}]", redirectURL);
			response.sendRedirect(redirectURL);
		}else{
			chain.doFilter(req, resp);
		}
	}


	/**
	 * 判断是否为Ajax请求
	 * @create Nov 1, 2015 1:30:55 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	private boolean isAjaxRequest(HttpServletRequest request){
		String requestType = request.getHeader("X-Requested-With");
		if(null!=requestType && "XMLHttpRequest".equals(requestType)){
			return true;
		}
		requestType = request.getHeader("x-requested-with");
		if(null!=requestType && "XMLHttpRequest".equals(requestType)){
			return true;
		}else{
			return false;
		}
	}
}