package com.jadyer.seed.mpp.mgr.user;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommonResult;
import com.jadyer.seed.comm.constant.Constants;
import com.jadyer.seed.comm.util.JadyerUtil;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value="/user")
public class UserController{
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private UserService userService;
    @Resource
    private AppidSignupAsync appidSignupAsync;

    @PostConstruct
    public void scheduleReport(){
        appidSignupAsync.signup();
        //Executors.newScheduledThreadPool(1).schedule(new Runnable(){
        //    @Override
        //    public void run() {
        //        try {
        //            List<UserInfo> userinfoList = userService.findAll();
        //            if(userinfoList.isEmpty()){
        //                logger.info("未查询到需要登记的appid");
        //            }
        //            for(UserInfo obj : userinfoList){
        //                if(1 == obj.getBindStatus()){
        //                    if(1 == obj.getMptype()){
        //                        WeixinTokenHolder.setWeixinAppidAppsecret(obj.getAppid(), obj.getAppsecret());
        //                        logger.info("登记微信appid=[{}]，appsecret=[{}]完毕", obj.getAppid(), obj.getAppsecret());
        //                    }
        //                    if(2 == obj.getMptype()){
        //                        QQTokenHolder.setQQAppidAppsecret(obj.getAppid(), obj.getAppsecret());
        //                        logger.info("登记QQappid=[{}]，appsecret=[{}]完毕", obj.getAppid(), obj.getAppsecret());
        //                    }
        //                }
        //            }
        //        } catch (Exception e) {
        //            LogUtil.getLogger().info("登记appid时发生异常，堆栈轨迹如下", e);
        //        }
        //    }
        //}, 6, TimeUnit.SECONDS);
    }


    /**
     * 登录
     */
    @ResponseBody
    @PostMapping(value="/login")
    public CommonResult login(String username, String password, String captcha, HttpSession session){
        if(StringUtils.isNotBlank(captcha)){
            if(!captcha.equals(session.getAttribute("rand"))){
                return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的验证码");
            }
        }
        UserInfo userInfo = userService.findByUsernameAndPassword(username, password);
        if(null == userInfo){
            return new CommonResult(CodeEnum.SYSTEM_BUSY.getCode(), "无效的用户名或密码");
        }
        session.setAttribute(Constants.WEB_SESSION_USER, userInfo);
        return new CommonResult();
    }


    /**
     * 登出
     */
    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute(Constants.WEB_SESSION_USER);
        return InternalResourceViewResolver.REDIRECT_URL_PREFIX + "/login.jsp";
    }


    /**
     * 平台用户信息
     */
    @GetMapping("/info")
    public String info(HttpServletRequest request){
        UserInfo userInfo = (UserInfo)request.getSession().getAttribute(Constants.WEB_SESSION_USER);
        //每次都取最新的
        userInfo = userService.findOne(userInfo.getId());
        //再更新HttpSession
        request.getSession().setAttribute(Constants.WEB_SESSION_USER, userInfo);
        //拼造开发者服务器的URL和Token
        StringBuilder sb = new StringBuilder(JadyerUtil.getFullContextPath(request));
        if(1 == userInfo.getMptype()){
            sb.append("/weixin/").append(userInfo.getUuid());
        }
        if(2 == userInfo.getMptype()){
            sb.append("/qq/").append(userInfo.getUuid());
        }
        request.setAttribute("token", DigestUtils.md5Hex(userInfo.getUuid() + "http://jadyer.cn/"));
        request.setAttribute("mpurl", sb.toString());
        return "user/info";
    }


    /**
     * 绑定公众号
     */
    @ResponseBody
    @PostMapping("/bind")
    public CommonResult bind(UserInfo _userInfo, HttpSession session){
        UserInfo userInfo = (UserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        userInfo.setBindStatus(0);
        userInfo.setAppid(_userInfo.getAppid());
        userInfo.setAppsecret(_userInfo.getAppsecret());
        userInfo.setMpid(_userInfo.getMpid());
        userInfo.setMpno(_userInfo.getMpno());
        userInfo.setMpname(_userInfo.getMpname());
        userInfo.setMchid(_userInfo.getMchid());
        userInfo.setMchkey(_userInfo.getMchkey());
        session.setAttribute(Constants.WEB_SESSION_USER, userService.save(userInfo));
        //更换绑定的公众号，也要同步更新微信或QQ公众平台的appid、appsecret、mchid
        if(1 == userInfo.getMptype()){
            WeixinTokenHolder.setWeixinAppidAppsecret(userInfo.getAppid(), userInfo.getAppsecret());
            if(StringUtils.isNotBlank(userInfo.getMchid())){
                WeixinTokenHolder.setWeixinAppidMch(userInfo.getAppid(), userInfo.getMchid(), userInfo.getMchkey());
            }
        }
        if(2 ==userInfo.getMptype()){
            QQTokenHolder.setQQAppidAppsecret(userInfo.getAppid(), userInfo.getAppsecret());
        }
        return new CommonResult();
    }


    /**
     * 修改密码
     */
    @ResponseBody
    @PostMapping("/password/update")
    public CommonResult passwordUpdate(String oldPassword, String newPassword, HttpSession session){
        UserInfo userInfo = (UserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        UserInfo respUserInfo = userService.passwordUpdate(userInfo, oldPassword, newPassword);
        //修改成功后要刷新HttpSession中的用户信息
        session.setAttribute(Constants.WEB_SESSION_USER, respUserInfo);
        return new CommonResult();
    }


    @ResponseBody
    @GetMapping("/menu/getjson")
    public CommonResult menuGetjson(HttpSession session){
        long uid = ((UserInfo)session.getAttribute(Constants.WEB_SESSION_USER)).getId();
        return new CommonResult(userService.getMenuJson(uid));
    }


    /**
     * 通过JSON的方式发布微信或QQ公众号自定义菜单
     * @param menuJson 微信或QQ公众号自定义菜单数据的JSON串
     */
    @ResponseBody
    @PostMapping("/menu/create")
    public CommonResult menuCreate(String menuJson, HttpSession session){
        UserInfo userInfo = (UserInfo)session.getAttribute(Constants.WEB_SESSION_USER);
        if(0 == userInfo.getBindStatus()){
            return new CommonResult(CodeEnum.SYSTEM_ERROR.getCode(), "当前用户未绑定微信或QQ公众平台");
        }
        if(1 == userInfo.getMptype()){
            WeixinErrorInfo errorInfo = WeixinHelper.createWeixinMenu(WeixinTokenHolder.getWeixinAccessToken(userInfo.getAppid()), menuJson);
            if(0==errorInfo.getErrcode() && userService.menuJsonUpsert(userInfo.getId(), menuJson)){
                return new CommonResult();
            }
            return new CommonResult(errorInfo.getErrcode(), errorInfo.getErrmsg());
        }
        if(2 == userInfo.getMptype()){
            QQErrorInfo errorInfo = QQHelper.createQQMenu(QQTokenHolder.getQQAccessToken(userInfo.getAppid()), menuJson);
            if(0==errorInfo.getErrcode() && userService.menuJsonUpsert(userInfo.getId(), menuJson)){
                return new CommonResult();
            }
            return new CommonResult(errorInfo.getErrcode(), errorInfo.getErrmsg());
        }
        return new CommonResult(CodeEnum.SYSTEM_ERROR.getCode(), "当前用户未关联微信或QQ公众平台");
    }
}