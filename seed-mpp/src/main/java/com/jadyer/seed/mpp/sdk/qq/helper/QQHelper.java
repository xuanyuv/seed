package com.jadyer.seed.mpp.sdk.qq.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.jadyer.seed.comm.util.HttpUtil;
import com.jadyer.seed.mpp.sdk.qq.constant.QQCodeEnum;
import com.jadyer.seed.mpp.sdk.qq.constant.QQConstants;
import com.jadyer.seed.mpp.sdk.qq.model.QQErrorInfo;
import com.jadyer.seed.mpp.sdk.qq.model.QQFansInfo;
import com.jadyer.seed.mpp.sdk.qq.model.QQOAuthAccessToken;
import com.jadyer.seed.mpp.sdk.qq.model.custom.QQCustomMsg;
import com.jadyer.seed.mpp.sdk.qq.model.menu.QQMenu;
import com.jadyer.seed.mpp.sdk.qq.model.template.QQTemplateMsg;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

public final class QQHelper {
	private static final Logger logger = LoggerFactory.getLogger(QQHelper.class);

	private QQHelper(){}

	/**
	 * 获取QQ的access_token
	 * @see 默认修饰符default即只有同包中的类才可使用
	 * @see {"errcode":0,"errmsg":"ok","access_token":"ace43d3290372b1a412ec4272ffb3893","expire":7200}
	 * @see {"errcode":40001,"errmsg":"\u83b7\u53d6access_token\u65f6AppSecret\u9519\u8bef\uff0c\u6216\u8005access_token\u65e0\u6548"}
	 * @return 获取失败时将抛出RuntimeException
	 * @create Nov 28, 2015 8:40:36 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	static String getQQAccessToken(String appid, String appsecret) throws IllegalAccessException {
		String reqURL = QQConstants.URL_QQ_GET_ACCESSTOKEN.replace(QQConstants.URL_PLACEHOLDER_APPID, appid).replace(QQConstants.URL_PLACEHOLDER_APPSECRET, appsecret);
		String respData = HttpUtil.post(reqURL, null, null);
		logger.info("获取QQaccess_token,QQ应答报文为-->{}", respData);
		Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
		if("0".equals(map.get("errcode")) && StringUtils.isNotBlank(map.get("access_token"))){
			return map.get("access_token");
		}
		String errmsg = QQCodeEnum.getMessageByCode(Integer.parseInt((map.get("errcode"))));
		if(StringUtils.isBlank(errmsg)){
			errmsg = map.get("errmsg");
		}
		throw new IllegalAccessException(errmsg);
	}


	/**
	 * 获取QQjsapi_ticket
	 * @return 获取失败时将抛出RuntimeException
	 * @create Nov 28, 2015 8:40:46 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	static String getQQJSApiTicket(String accesstoken) throws IllegalAccessException {
		String reqURL = QQConstants.URL_QQ_GET_JSAPI_TICKET.replace(QQConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
		String respData = HttpUtil.post(reqURL, null, null);
		logger.info("获取QQjsapi_ticket,QQ应答报文为-->{}", respData);
		Map<String, String> map = JSON.parseObject(respData, new TypeReference<Map<String, String>>(){});
		if("0".equals(map.get("errcode"))){
			return map.get("ticket");
		}
		String errmsg = QQCodeEnum.getMessageByCode(Integer.parseInt((map.get("errcode"))));
		if(StringUtils.isBlank(errmsg)){
			errmsg = map.get("errmsg");
		}
		throw new IllegalAccessException(errmsg);
	}


	/**
	 * 通过code换取QQ网页授权access_token
	 * @param appid     QQ公众号AppID
	 * @param appsecret QQ公众号AppSecret
	 * @param code      换取access_token的有效期为5分钟的票据
	 * @return 返回获取到的网页access_token(获取失败时的应答码也在该返回中)
	 * @create Nov 28, 2015 8:41:39 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	static QQOAuthAccessToken getQQOAuthAccessToken(String appid, String appsecret, String code){
		String reqURL = QQConstants.URL_QQ_OAUTH2_GET_ACCESSTOKEN.replace(QQConstants.URL_PLACEHOLDER_APPID, appid)
																	  .replace(QQConstants.URL_PLACEHOLDER_APPSECRET, appsecret)
																	  .replace(QQConstants.URL_PLACEHOLDER_CODE, code);
		String respData = HttpUtil.post(reqURL, null, null);
		logger.info("获取QQ网页access_token,QQ应答报文为-->{}", respData);
		QQOAuthAccessToken qqOauthAccessToken = JSON.parseObject(respData, QQOAuthAccessToken.class);
		if(qqOauthAccessToken.getErrcode() != 0){
			String errmsg = QQCodeEnum.getMessageByCode(qqOauthAccessToken.getErrcode());
			if(StringUtils.isNotBlank(errmsg)){
				qqOauthAccessToken.setErrmsg(errmsg);
			}
		}
		return qqOauthAccessToken;
	}


	/**
	 * 构建网页授权获取用户信息的获取Code地址
	 * @param appid       QQ公众号AppID
	 * @param scope       应用授权作用域(snsapi_base或snsapi_userinfo)
	 * @param state       重定向后会带上state参数(开发者可以填写a-zA-Z0-9的参数值,最多128字节)
	 * @param redirectURI 授权后重定向的回调链接地址(请使用urlencode对链接进行处理)
	 * @create Nov 28, 2015 8:42:01 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String buildQQOAuthCodeURL(String appid, String scope, String state, String redirectURI){
		try {
			return QQConstants.URL_QQ_OAUTH2_GET_CODE.replace(QQConstants.URL_PLACEHOLDER_APPID, appid)
														  .replace(QQConstants.URL_PLACEHOLDER_SCOPE, scope)
														  .replace(QQConstants.URL_PLACEHOLDER_STATE, state)
														  .replace(QQConstants.URL_PLACEHOLDER_REDIRECT_URI, URLEncoder.encode(redirectURI, HttpUtil.DEFAULT_CHARSET));
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}


	/**
	 * 创建自定义菜单
	 * @see -----------------------------------------------------------------------------------------------------------
	 * @see 1.自定义菜单最多包括3个一级菜单,每个一级菜单最多包含5个二级菜单
	 * @see 2.一级菜单最多4个汉字,二级菜单最多7个汉字,多出来的部分将会以"..."代替
	 * @see 3.由于QQ客户端缓存,创建的菜单需XX小时QQ客户端才会展现,测试时可尝试取消关注公众账号后再次关注后查看效果
	 * @see 4.修改菜单时(修改内容或菜单数量等)不需要删除菜单,直接调用创建接口即可,QQ会自动覆盖以前创建的菜单
	 * @see -----------------------------------------------------------------------------------------------------------
	 * @create Nov 28, 2015 8:43:13 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static QQErrorInfo createQQMenu(String accesstoken, QQMenu menu){
		String reqURL = QQConstants.URL_QQ_GET_CREATE_MENU.replace(QQConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
		String reqData = JSON.toJSONString(menu);
		logger.info("自定义菜单创建-->发送的JSON为{}", reqData);
		String respData = HttpUtil.post(reqURL, reqData, "application/json; charset="+HttpUtil.DEFAULT_CHARSET);
		logger.info("自定义菜单创建-->QQ应答JSON为{}", respData);
		QQErrorInfo errinfo = JSON.parseObject(respData, QQErrorInfo.class);
		if(errinfo.getErrcode() != 0){
			String errmsg = QQCodeEnum.getMessageByCode(errinfo.getErrcode());
			if(StringUtils.isNotBlank(errmsg)){
				errinfo.setErrmsg(errmsg);
			}
		}
		return errinfo;
	}


	/**
	 * 创建自定义菜单
	 * @see String menuJson = "{\"button\":[{\"type\":\"view\", \"name\":\"我的博客\", \"url\":\"https://jadyer.github.io/\"}, {\"type\":\"click\", \"name\":\"今日歌曲\", \"key\":\"V1001_TODAY_MUSIC\"}, {\"name\":\"个人中心\", \"sub_button\": [{\"type\":\"view\", \"name\":\"搜索\", \"url\":\"http://www.soso.com/\"}, {\"type\":\"view\", \"name\":\"视频\", \"url\":\"http://v.qq.com/\"}, {\"type\":\"click\", \"name\":\"赞一下我们\", \"key\":\"V1001_GOOD\"}]}]}";
	 * @see String menuJson = "{\"button\":[{\"type\":\"view\", \"name\":\"我的博客\", \"url\":\"https://jadyer.github.io/\"}, {\"name\":\"个人中心\", \"sub_button\": [{\"type\":\"view\", \"name\":\"搜索\", \"url\":\"http://www.soso.com/\"}, {\"type\":\"view\", \"name\":\"视频\", \"url\":\"http://v.qq.com/\"}, {\"type\":\"click\", \"name\":\"赞一下我们\", \"key\":\"V1001_GOOD\"}]}]}";
	 * @create Nov 28, 2015 8:44:47 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static QQErrorInfo createQQMenu(String accesstoken, String menuJson){
		String reqURL = QQConstants.URL_QQ_GET_CREATE_MENU.replace(QQConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
		logger.info("自定义菜单创建-->发送的JSON为{}", menuJson);
		String respData = HttpUtil.post(reqURL, menuJson, "application/json; charset="+HttpUtil.DEFAULT_CHARSET);
		logger.info("自定义菜单创建-->QQ应答JSON为{}", respData);
		QQErrorInfo errinfo = JSON.parseObject(respData, QQErrorInfo.class);
		if(errinfo.getErrcode() != 0){
			String errmsg = QQCodeEnum.getMessageByCode(errinfo.getErrcode());
			if(StringUtils.isNotBlank(errmsg)){
				errinfo.setErrmsg(errmsg);
			}
		}
		return errinfo;
	}


	/**
	 * 获取用户基本信息
	 * @see QQ服务器的应答报文是下面这样的,微信服务器应答的还有一个encoding,QQ服务器的竟然连encoding都没有
	 * @see HTTP/1.1 200 OK
	 * @see Server: nginx
	 * @see Date: Sat, 28 Nov 2015 14:25:31 GMT
	 * @see Content-Type: application/json
	 * @see Content-Length: 281
	 * @see Connection: close
	 * @see 
	 * @see {"subscribe":1,"openid":"E12D231CFC30438FB6970B0C7669C101","nickname":"玄玉","sex":0,"language":"zh_CN","city":"","province":"","country":"","headimgurl":"http://q3.qlogo.cn/g?b=qq&k=FMnTHnicr3kJvcZyG9UatiaQ&s=40&t=1371634951","subscribe_time":1448711806,"remark":"","groupid":1}
	 * @create Nov 28, 2015 8:45:20 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static QQFansInfo getQQFansInfo(String accesstoken, String openid){
		String reqURL = QQConstants.URL_QQ_GET_FANSINFO.replace(QQConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken).replace(QQConstants.URL_PLACEHOLDER_OPENID, openid);
		String respData = HttpUtil.post(reqURL, null, null);
		return JSON.parseObject(respData, QQFansInfo.class);
	}


	/**
	 * 单发主动推消息
	 * @create Nov 28, 2015 8:47:36 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static QQErrorInfo pushQQMsgToFans(String accesstoken, QQCustomMsg customMsg){
		String reqURL = QQConstants.URL_QQ_CUSTOM_PUSH_MESSAGE.replace(QQConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
		String reqData = JSON.toJSONString(customMsg);
		logger.info("单发主动推消息-->发送的JSON为{}", reqData);
		String respData = HttpUtil.post(reqURL, reqData, "application/json; charset="+HttpUtil.DEFAULT_CHARSET);
		logger.info("单发主动推消息-->QQ应答JSON为{}", respData);
		QQErrorInfo errinfo = JSON.parseObject(respData, QQErrorInfo.class);
		if(errinfo.getErrcode() != 0){
			String errmsg = QQCodeEnum.getMessageByCode(errinfo.getErrcode());
			if(StringUtils.isNotBlank(errmsg)){
				errinfo.setErrmsg(errmsg);
			}
		}
		return errinfo;
	}


	/**
	 * 单发主动推模板消息
	 * @create Dec 30, 2015 11:31:36 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static QQErrorInfo pushQQTemplateMsgToFans(String accesstoken, QQTemplateMsg templateMsg){
		String reqURL = QQConstants.URL_QQ_TEMPLATE_PUSH_MESSAGE.replace(QQConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken);
		String reqData = JSON.toJSONString(templateMsg);
		logger.info("单发主动推消息-->发送的JSON为{}", reqData);
		String respData = HttpUtil.post(reqURL, reqData, "application/json; charset="+HttpUtil.DEFAULT_CHARSET);
		logger.info("单发主动推消息-->QQ应答JSON为{}", respData);
		QQErrorInfo errinfo = JSON.parseObject(respData, QQErrorInfo.class);
		if(errinfo.getErrcode() != 0){
			String errmsg = QQCodeEnum.getMessageByCode(errinfo.getErrcode());
			if(StringUtils.isNotBlank(errmsg)){
				errinfo.setErrmsg(errmsg);
			}
		}
		return errinfo;
	}


	/**
	 * JS-SDK权限验证的签名
	 * @see 注意这里使用的是noncestr,不是nonceStr
	 * @param noncestr  随机字符串
	 * @param timestamp 时间戳
	 * @param url       当前网页的URL,不包含#及其后面部分
	 * @create Nov 28, 2015 8:48:52 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String signQQJSSDK(String appid, String noncestr, String timestamp, String url){
		StringBuilder sb = new StringBuilder();
		sb.append("jsapi_ticket=").append(QQTokenHolder.getQQJSApiTicket(appid)).append("&")
		  .append("noncestr=").append(noncestr).append("&")
		  .append("timestamp=").append(timestamp).append("&")
		  .append("url=").append(url);
		return DigestUtils.sha1Hex(sb.toString());
	}


	/**
	 * 获取临时素材
	 * @return 获取成功时返回文件保存在本地的路径,获取失败时将抛出RuntimeException
	 * @create Nov 28, 2015 8:49:15 PM
	 * @author 玄玉<https://jadyer.github.io/>
	 */
	public static String downloadQQTempMediaFile(String accesstoken, String mediaId){
		String reqURL = QQConstants.URL_QQ_GET_TEMP_MEDIA_FILE.replace(QQConstants.URL_PLACEHOLDER_ACCESSTOKEN, accesstoken).replace(QQConstants.URL_PLACEHOLDER_MEDIAID, mediaId);
		Map<String, String> resultMap = HttpUtil.postWithDownload(reqURL, null);
		if("no".equals(resultMap.get("isSuccess"))){
			Map<String, String> errmap = JSON.parseObject(resultMap.get("failReason"), new TypeReference<Map<String, String>>(){});
			String errmsg = QQCodeEnum.getMessageByCode(Integer.parseInt((errmap.get("errcode"))));
			if(StringUtils.isBlank(errmsg)){
				errmsg = errmap.get("errmsg");
			}
			throw new RuntimeException("下载QQ临时素材" + mediaId + "失败-->" + errmsg);
		}
		return resultMap.get("fullPath");
	}
}