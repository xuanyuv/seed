package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinOAuthAccessToken;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 接收微信服务器回调以及其它的辅助功能
 * @create Oct 19, 2015 8:30:44 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
@Controller
@RequestMapping(value="/weixin/helper")
public class WeixinHelperController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 获取网页access_token
	 * @param appid 微信appid,通过它来支持多用户
	 * @param code  微信服务器发放的,有效期为5分钟的,用于换取网页access_token的code
	 * @param state 重定向到微信服务器时,由开发者服务器携带过去的参数,这里会原样带回
	 * @return 获取失败则返回一个友好的HTML页面,获取成功后直接跳转到用户原本请求的资源
	 */
	@RequestMapping(value="/oauth/{appid}")
	public String oauth(@PathVariable String appid, String code, String state, HttpServletResponse response) throws IOException{
		logger.info("收到微信服务器回调code=[{}], state=[{}]", code, state);
		if(StringUtils.isNotBlank(code)){
			WeixinOAuthAccessToken oauthAccessToken = WeixinTokenHolder.getWeixinOAuthAccessToken(appid, code);
			if(0==oauthAccessToken.getErrcode() && StringUtils.isNotBlank(oauthAccessToken.getOpenid())){
				/**
				 * 还原state携带过来的粉丝请求的原URL
				 * @see state=http://www.jadyer.com/mpp/weixin/getOpenid/openid=openid/test=7645
				 */
				//1.获取到URL中的非参数部分
				String uri = state.substring(0, state.indexOf("="));
				uri = uri.substring(0, uri.lastIndexOf("/"));
				//2.获取到URL中的参数部分(得到openid的方式为截取掉占位的,再追加真正的值)
				String params = state.substring(uri.length()+1);
				params = params.replaceAll("/", "&").replace("openid=openid", "openid="+oauthAccessToken.getOpenid());
				//3.拼接粉丝请求的原URL并跳转过去
				String fullURI = uri + "?" + params;
				logger.info("还原粉丝请求的资源得到state=[{}]", fullURI);
				response.sendRedirect(fullURI);
			}
		}
		response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
		response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter out = response.getWriter();
		out.print("系统繁忙Unauthorized\r\n请联系您关注的微信公众号");
		out.flush();
		out.close();
		return null;
	}


	/**
	 * JS-SDK权限验证的签名
	 * @param url 当前网页的URL,不包含#及其后面部分
	 */
	@ResponseBody
	@RequestMapping(value="/jssdk/sign")
	public Map<String, String> jssdkSign(String appid, String url) throws UnsupportedEncodingException{
		Map<String, String> resultMap = new HashMap<>();
		String noncestr = RandomStringUtils.randomNumeric(16);
		long timestamp = System.currentTimeMillis() / 1000;
		url = URLDecoder.decode(url, "UTF-8");
		resultMap.put("appid", appid);
		resultMap.put("timestamp", String.valueOf(timestamp));
		resultMap.put("noncestr", noncestr);
		resultMap.put("signature", WeixinHelper.signWeixinJSSDK(appid, noncestr, String.valueOf(timestamp), url));
		resultMap.put("url", url);
		return resultMap;
	}


	/**
	 * 下载微信临时媒体文件
	 * @param mediaId 媒体文件ID
	 * @create Nov 9, 2015 5:06:19 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	@RequestMapping(value="/tempMediaFile/get/{appid}/{mediaId}")
	public void tempMediaFileGet(@PathVariable String appid, @PathVariable String mediaId, HttpServletResponse response) throws Exception {
		String fullPath = WeixinHelper.downloadWeixinTempMediaFile(WeixinTokenHolder.getWeixinAccessToken(appid), mediaId);
		WeixinTokenHolder.setMediaIdFilePath(appid, mediaId, fullPath);
		response.setContentType("application/octet-stream");
		response.setHeader("Content-disposition", "attachment; filename=" + new String(("get_"+FilenameUtils.getName(fullPath)).getBytes("UTF-8"), "ISO8859-1"));
		InputStream is = FileUtils.openInputStream(new File(fullPath));
		OutputStream os = new BufferedOutputStream(response.getOutputStream());
		byte[] buff = new byte[1024];
		int len;
		while((len=is.read(buff)) != -1){
			os.write(buff, 0, len);
		}
		is.close();
		os.close();
	}


	/**
	 * 删除存储在本地的微信临时媒体文件
	 * @param fileFullPath 存储在本地的微信临时媒体文件的完整路径
	 * @create Nov 9, 2015 9:06:35 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	@ResponseBody
	@RequestMapping(value="/tempMediaFile/delete/{appid}/{mediaId}")
	public boolean tempMediaFileDelete(@PathVariable String appid, @PathVariable String mediaId){
		String localFileFullPath = WeixinTokenHolder.getMediaIdFilePath(appid, mediaId);
		try {
			return new File(localFileFullPath).delete();
		} catch (Exception e) {
			logger.info("删除存储在本地的微信临时媒体文件mediaId=["+mediaId+"],fullPath=["+localFileFullPath+"]失败,堆栈轨迹如下", e);
			return false;
		}
	}


	/**
	 * 获取微信二维码图片URL
	 * @param type          二维码类型,0--临时二维码,1--永久二维码,2--永久字符串二维码
	 * @param expireSeconds 二维码临时有效的时间,单位为秒,最大不超过2592000s,即30天,不填则默认有效期为30s
	 * @param sceneId       二维码参数场景值ID,临时二维码时为32位非0整型,永久二维码时值为1--100000
	 * @param sceneStr      二维码参数场景值ID,字符串形式的ID,字符串类型,长度限制为1到64,仅永久二维码支持此字段
	 * @create Feb 22, 2016 11:15:08 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	@RequestMapping(value="/getQrcodeURL")
	public void getQrcodeURL(String appid, int type, String expireSeconds, String sceneId, String sceneStr, HttpServletResponse response) throws IOException{
		if(StringUtils.isBlank(expireSeconds)){
			expireSeconds = "2";
		}
		if(StringUtils.isBlank(sceneId)){
			sceneId = "2";
		}
		String ticket = WeixinHelper.createQrcodeTicket(WeixinTokenHolder.getWeixinAccessToken(appid), type, Integer.parseInt(expireSeconds), Long.parseLong(sceneId), sceneStr);
		String qrcodeURL = WeixinConstants.URL_WEIXIN_GET_QRCODE.replace(WeixinConstants.URL_PLACEHOLDER_QRCODE_TICKET, ticket);
		response.setCharacterEncoding(HttpUtil.DEFAULT_CHARSET);
		response.setContentType("text/plain; charset=" + HttpUtil.DEFAULT_CHARSET);
		response.setHeader("Cache-Control", "no-cache");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		PrintWriter out = response.getWriter();
		out.print(qrcodeURL);
		out.flush();
		out.close();
	}
}