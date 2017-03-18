package com.jadyer.seed.mpp.mgr.user;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.mgr.user.model.UserInfo;
import com.jadyer.seed.mpp.sdk.qq.helper.QQHelper;
import com.jadyer.seed.mpp.sdk.qq.helper.QQTokenHolder;
import com.jadyer.seed.mpp.sdk.qq.model.QQErrorInfo;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinErrorInfo;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping(value="/user")
public class UserController{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Resource
	private UserService userService;

	@PostConstruct
	public void scheduleReport(){
		Executors.newScheduledThreadPool(1).schedule(new Runnable(){
			@Override
			public void run() {
				try {
					List<UserInfo> userinfoList = userService.findAll();
					if(userinfoList.isEmpty()){
						logger.info("未查询到需要登记的appid");
					}
					for(UserInfo obj : userinfoList){
						if("1".equals(obj.getBindStatus())){
							if("1".equals(obj.getMptype())){
								WeixinTokenHolder.setWeixinAppidAppsecret(obj.getAppid(), obj.getAppsecret());
								logger.info("登记微信appid=[{}]，appsecret=[{}]完毕", obj.getAppid(), obj.getAppsecret());
							}
							if("2".equals(obj.getMptype())){
								QQTokenHolder.setQQAppidAppsecret(obj.getAppid(), obj.getAppsecret());
								logger.info("登记QQappid=[{}]，appsecret=[{}]完毕", obj.getAppid(), obj.getAppsecret());
							}
						}
					}
				} catch (Exception e) {
					LogUtil.getLogger().info("登记appid时发生异常，堆栈轨迹如下", e);
				}
			}
		}, 6, TimeUnit.SECONDS);
	}


	/**
	 * 登录
	 */
	@ResponseBody
	@RequestMapping(value="/login", method= RequestMethod.POST)
	public CommonResult login(String username, String password, String captcha, HttpServletRequest request){
		if(StringUtils.isNotBlank(captcha)){
			if(!captcha.equals(request.getSession().getAttribute("rand"))){
				return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
			}
		}
		UserInfo userInfo = userService.findByUsernameAndPassword(username, password);
		if(null == userInfo){
			return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的用户名或密码");
		}
		request.getSession().setAttribute(Constants.WEB_SESSION_USER, userInfo);
		return new CommonResult();
	}


	/**
	 * 登出
	 */
	@RequestMapping("/logout")
	public String logout(HttpServletRequest request){
		request.getSession().removeAttribute(Constants.WEB_SESSION_USER);
		return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/login.jsp";
	}


	/**
	 * 平台用户信息
	 */
	@RequestMapping("/info")
	public String info(HttpServletRequest request){
		UserInfo userInfo = (UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
		//每次都取最新的
		userInfo = userService.findOne(userInfo.getId());
		//再更新HttpSession
		request.getSession().setAttribute(Constants.WEB_SESSION_USER, userInfo);
		//拼造开发者服务器的URL和Token
		StringBuilder sb = new StringBuilder();
		sb.append(request.getScheme()).append("://").append(request.getServerName());
		if(80!=request.getServerPort() && 443!=request.getServerPort()){
			sb.append(":").append(request.getServerPort());
		}
		sb.append(request.getContextPath());
		if("1".equals(userInfo.getMptype())){
			sb.append("/weixin/").append(userInfo.getUuid());
		}
		if("2".equals(userInfo.getMptype())){
			sb.append("/qq/").append(userInfo.getUuid());
		}
		request.setAttribute("token", DigestUtils.md5Hex(userInfo.getUuid() + "https://jadyer.github.io/" + userInfo.getUuid()));
		request.setAttribute("mpurl", sb.toString());
		return "user/userInfo";
	}


	/**
	 * 绑定公众号（即录库）
	 */
	@ResponseBody
	@RequestMapping("/bind")
	public CommonResult bind(UserInfo userInfo, HttpServletRequest request){
		UserInfo _userInfo = (UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
		userInfo.setPassword(_userInfo.getPassword());
		userInfo.setUsername(_userInfo.getUsername());
		userInfo.setPid(_userInfo.getPid());
		userInfo.setUuid(_userInfo.getUuid());
		userInfo.setMptype(_userInfo.getMptype());
		userInfo.setBindTime(new Date());
		request.getSession().setAttribute(Constants.WEB_SESSION_USER, userService.save(userInfo));
		//更换绑定的公众号,也要同步更新微信或QQ公众平台的appid和appsecret
		if("1".equals(userInfo.getMptype())){
			WeixinTokenHolder.setWeixinAppidAppsecret(userInfo.getAppid(), userInfo.getAppsecret());
		}
		if("2".equals(userInfo.getMptype())){
			QQTokenHolder.setQQAppidAppsecret(userInfo.getAppid(), userInfo.getAppsecret());
		}
		return new CommonResult();
	}


	/**
	 * 修改密码
	 */
	@ResponseBody
	@RequestMapping("/password/update")
	public CommonResult passwordUpdate(String oldPassword, String newPassword, HttpServletRequest request){
		UserInfo userInfo = (UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
		UserInfo respUserInfo = userService.passwordUpdate(userInfo, oldPassword, newPassword);
		if(null == respUserInfo){
			return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "原密码不正确");
		}
		//修改成功后要刷新HttpSession中的用户信息
		request.getSession().setAttribute(Constants.WEB_SESSION_USER, respUserInfo);
		return new CommonResult();
	}


	@ResponseBody
	@RequestMapping("/menu/getjson")
	public CommonResult menuGetjson(HttpServletRequest request){
		int uid = ((UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER)).getId();
		return new CommonResult(userService.getMenuJson(uid));
	}


	/**
	 * 通过JSON的方式发布微信或QQ公众号自定义菜单
	 * @param menuJson 微信或QQ公众号自定义菜单数据的JSON串
	 */
	@ResponseBody
	@RequestMapping("/menu/create")
	public CommonResult menuCreate(String menuJson, HttpServletRequest request){
		UserInfo userInfo = (UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
		if("0".equals(userInfo.getBindStatus())){
			return new CommonResult(CodeEnum.SYSTEM_ERROR.getCode(), "当前用户未绑定微信或QQ公众平台");
		}
		if("1".equals(userInfo.getMptype())){
			WeixinErrorInfo errorInfo = WeixinHelper.createWeixinMenu(WeixinTokenHolder.getWeixinAccessToken(userInfo.getAppid()), menuJson);
			if(0==errorInfo.getErrcode() && userService.menuJsonUpsert(userInfo.getId(), menuJson)){
				return new CommonResult();
			}
			return new CommonResult(errorInfo.getErrcode(), errorInfo.getErrmsg());
		}
		if("2".equals(userInfo.getMptype())){
			QQErrorInfo errorInfo = QQHelper.createQQMenu(QQTokenHolder.getQQAccessToken(userInfo.getAppid()), menuJson);
			if(0==errorInfo.getErrcode() && userService.menuJsonUpsert(userInfo.getId(), menuJson)){
				return new CommonResult();
			}
			return new CommonResult(errorInfo.getErrcode(), errorInfo.getErrmsg());
		}
		return new CommonResult(CodeEnum.SYSTEM_ERROR.getCode(), "当前用户未关联微信或QQ公众平台");
	}
}