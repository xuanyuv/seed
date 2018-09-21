package com.jadyer.seed.mpp.web.controller;

import com.jadyer.seed.comm.constant.CodeEnum;
import com.jadyer.seed.comm.constant.CommResult;
import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.RequestUtil;
import com.jadyer.seed.mpp.sdk.qq.helper.QQHelper;
import com.jadyer.seed.mpp.sdk.qq.helper.QQTokenHolder;
import com.jadyer.seed.mpp.sdk.qq.model.QQErrorInfo;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinErrorInfo;
import com.jadyer.seed.mpp.web.model.MppReplyInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.MppMenuService;
import com.jadyer.seed.mpp.web.service.MppReplyService;
import com.jadyer.seed.mpp.web.service.MppUserService;
import com.jadyer.seed.mpp.web.service.async.AppidAsync;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/15 12:45.
 */
@Controller
@RequestMapping("/mpp")
public class MppController {
    @Resource
    private AppidAsync appidAsync;
    @Resource
    private MppUserService mppUserService;
    @Resource
    private MppMenuService mppMenuService;
    @Resource
    private MppReplyService mppReplyService;

    @PostConstruct
    public void scheduleReport(){
        appidAsync.signup();
        //Executors.newScheduledThreadPool(1).schedule(new Runnable(){
        //    @Override
        //    public void run() {
        //        try {
        //            List<MppUserInfo> userinfoList = mppUserService.findAll();
        //            if(userinfoList.isEmpty()){
        //                logger.info("未查询到需要登记的appid");
        //            }
        //            for(MppUserInfo obj : userinfoList){
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
     * 平台用户信息
     */
    @GetMapping("/user/info")
    public String info(HttpServletRequest request){
        MppUserInfo mppUserInfo = (MppUserInfo)request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER);
        //每次都取最新的
        mppUserInfo = mppUserService.findOne(mppUserInfo.getId());
        //再更新HttpSession
        request.getSession().setAttribute(SeedConstants.WEB_SESSION_USER, mppUserInfo);
        //拼造开发者服务器的URL和Token
        StringBuilder sb = new StringBuilder(RequestUtil.getFullContextPath(request));
        if(1 == mppUserInfo.getMptype()){
            sb.append("/weixin/").append(mppUserInfo.getUuid());
        }
        if(2 == mppUserInfo.getMptype()){
            sb.append("/qq/").append(mppUserInfo.getUuid());
        }
        request.setAttribute("token", DigestUtils.md5Hex(mppUserInfo.getUuid() + "https://jadyer.cn/"));
        request.setAttribute("mpurl", sb.toString());
        return "/admin/mpp/user.info";
    }


    /**
     * 绑定公众号
     */
    @ResponseBody
    @PostMapping("/user/bind")
    public CommResult bind(MppUserInfo user, HttpSession session){
        MppUserInfo mppUserInfo = (MppUserInfo)session.getAttribute(SeedConstants.WEB_SESSION_USER);
        mppUserInfo.setBindStatus(0);
        mppUserInfo.setAppid(user.getAppid());
        mppUserInfo.setAppsecret(user.getAppsecret());
        mppUserInfo.setMpid(user.getMpid());
        mppUserInfo.setMpno(user.getMpno());
        mppUserInfo.setMpname(user.getMpname());
        mppUserInfo.setMchid(user.getMchid());
        mppUserInfo.setMchkey(user.getMchkey());
        session.setAttribute(SeedConstants.WEB_SESSION_USER, mppUserService.upsert(mppUserInfo));
        //更换绑定的公众号，也要同步更新微信或QQ公众平台的appid、appsecret、mchid
        if(1 == mppUserInfo.getMptype()){
            WeixinTokenHolder.setWeixinAppidAppsecret(mppUserInfo.getAppid(), mppUserInfo.getAppsecret());
            if(StringUtils.isNotBlank(mppUserInfo.getMchid())){
                WeixinTokenHolder.setWeixinAppidMch(mppUserInfo.getAppid(), mppUserInfo.getMchid(), mppUserInfo.getMchkey());
            }
        }
        if(2 == mppUserInfo.getMptype()){
            QQTokenHolder.setQQAppidAppsecret(mppUserInfo.getAppid(), mppUserInfo.getAppsecret());
        }
        return CommResult.success();
    }


    /**
     * 修改密码
     */
    @ResponseBody
    @PostMapping("/user/password/update")
    public CommResult passwordUpdate(String oldPassword, String newPassword, HttpSession session){
        MppUserInfo mppUserInfo = (MppUserInfo)session.getAttribute(SeedConstants.WEB_SESSION_USER);
        MppUserInfo respMppUserInfo = mppUserService.passwordUpdate(mppUserInfo, oldPassword, newPassword);
        //修改成功后要刷新HttpSession中的用户信息
        session.setAttribute(SeedConstants.WEB_SESSION_USER, respMppUserInfo);
        return CommResult.success();
    }


    /**
     * 公众号菜单读取
     */
    @ResponseBody
    @GetMapping("/menu/getjson")
    public CommResult<String> menuGetjson(HttpSession session){
        long uid = ((MppUserInfo)session.getAttribute(SeedConstants.WEB_SESSION_USER)).getId();
        return CommResult.success(mppMenuService.getMenuJson(uid));
    }


    /**
     * 通过json的方式发布公众号自定义菜单
     * @param menuJson 微信或QQ公众号自定义菜单数据的json字符串
     */
    @ResponseBody
    @PostMapping("/menu/create")
    public CommResult menuCreate(String menuJson, HttpSession session){
        MppUserInfo mppUserInfo = (MppUserInfo)session.getAttribute(SeedConstants.WEB_SESSION_USER);
        if(0 == mppUserInfo.getBindStatus()){
            return CommResult.fail(CodeEnum.SYSTEM_ERROR.getCode(), "当前用户未绑定微信或QQ公众平台");
        }
        if(1 == mppUserInfo.getMptype()){
            WeixinErrorInfo errorInfo = WeixinHelper.createWeixinMenu(WeixinTokenHolder.getWeixinAccessToken(mppUserInfo.getAppid()), menuJson);
            if(0==errorInfo.getErrcode() && mppMenuService.menuJsonUpsert(mppUserInfo.getId(), menuJson)){
                return CommResult.success();
            }
            return CommResult.fail(errorInfo.getErrcode(), errorInfo.getErrmsg());
        }
        if(2 == mppUserInfo.getMptype()){
            QQErrorInfo errorInfo = QQHelper.createQQMenu(QQTokenHolder.getQQAccessToken(mppUserInfo.getAppid()), menuJson);
            if(0==errorInfo.getErrcode() && mppMenuService.menuJsonUpsert(mppUserInfo.getId(), menuJson)){
                return CommResult.success();
            }
            return CommResult.fail(errorInfo.getErrcode(), errorInfo.getErrmsg());
        }
        return CommResult.fail(CodeEnum.SYSTEM_ERROR.getCode(), "当前用户未关联微信或QQ公众平台");
    }


    /**
     * 查询通用的回复内容
     */
    @RequestMapping("/reply/common/get")
    public String getCommon(HttpServletRequest request){
        long uid = ((MppUserInfo)request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER)).getId();
        request.setAttribute("replyInfo", mppReplyService.getByUidAndCategory(uid, 0));
        return "/admin/mpp/reply.common";
    }


    /**
     * 查询关注后回复的内容
     */
    @RequestMapping("/reply/follow/get")
    public String getFollow(HttpServletRequest request){
        long uid = ((MppUserInfo)request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER)).getId();
        request.setAttribute("replyInfo", mppReplyService.getByUidAndCategory(uid, 1));
        return "/admin/mpp/reply.follow";
    }


    /**
     * 更新关注后回复的内容
     */
    @ResponseBody
    @PostMapping("/reply/follow/upsert")
    public CommResult<MppReplyInfo> saveFollow(MppReplyInfo mppReplyInfo, HttpServletRequest request){
        mppReplyInfo.setUid(((MppUserInfo)request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER)).getId());
        return CommResult.success(mppReplyService.upsertFollow(mppReplyInfo));
    }


    /**
     * 分页查询关键字回复列表
     * @param page zero-based page index
     */
    @RequestMapping("/reply/keyword/list")
    public String listViaPage(String pageNo, HttpServletRequest request){
        final long uid = ((MppUserInfo)request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER)).getId();
        request.setAttribute("page", mppReplyService.listViaPage(uid, pageNo));
        return "/admin/mpp/reply.keyword.list";
    }


    /**
     * 查询关键字回复的内容
     */
    @ResponseBody
    @RequestMapping("/reply/keyword/get/{id}")
    public CommResult<MppReplyInfo> getKeyword(@PathVariable long id){
        return CommResult.success(mppReplyService.getKeyword(id));
    }


    /**
     * delete关键字
     */
    @ResponseBody
    @RequestMapping("/reply/keyword/delete/{id}")
    public CommResult deleteKeyword(@PathVariable long id){
        mppReplyService.deleteKeyword(id);
        return CommResult.success();
    }


    /**
     * saveOrUpdate关键字
     */
    @ResponseBody
    @RequestMapping("/reply/keyword/upsert")
    public CommResult<MppReplyInfo> upsertKeyword(MppReplyInfo mppReplyInfo, HttpServletRequest request){
        mppReplyInfo.setUid(((MppUserInfo)request.getSession().getAttribute(SeedConstants.WEB_SESSION_USER)).getId());
        return CommResult.success(mppReplyService.upsertKeyword(mppReplyInfo));
    }
}