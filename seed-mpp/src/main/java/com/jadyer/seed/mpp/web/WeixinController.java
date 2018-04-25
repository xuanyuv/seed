package com.jadyer.seed.mpp.web;

import com.jadyer.seed.comm.constant.SeedConstants;
import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.controller.WeixinMsgControllerCustomServiceAdapter;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinHelper;
import com.jadyer.seed.mpp.sdk.weixin.helper.WeixinTokenHolder;
import com.jadyer.seed.mpp.sdk.weixin.model.WeixinErrorInfo;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplate;
import com.jadyer.seed.mpp.sdk.weixin.model.template.WeixinTemplateMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutCustomServiceMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutTextMsg;
import com.jadyer.seed.mpp.web.model.MppReplyInfo;
import com.jadyer.seed.mpp.web.model.MppUserInfo;
import com.jadyer.seed.mpp.web.service.async.FansSaveAsync;
import com.jadyer.seed.mpp.web.service.FansService;
import com.jadyer.seed.mpp.web.service.MppReplyService;
import com.jadyer.seed.mpp.web.service.MppUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value="/weixin")
public class WeixinController extends WeixinMsgControllerCustomServiceAdapter {
    @Resource
    private FansService fansService;
    @Resource
    private FansSaveAsync fansSaveAsync;
    @Resource
    private MppUserService mppUserService;
    @Resource
    private MppReplyService mppReplyService;

    @Override
    protected WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg) {
        /*
        //回复带链接和表情的文本消息
        if("blog".equals(inTextMsg.getContent())){
            return new WeixinOutTextMsg(inTextMsg).setContent("[右哼哼]欢迎访问<a href=\"http://jadyer.cn/\">我的博客</a>[左哼哼]");
        }
        //回复多图文
        WeixinOutNewsMsg outMsg = new WeixinOutNewsMsg(inTextMsg);
        outMsg.addNews("第一个大图文标题", "第一个大图文描述", "http://avatar.csdn.net/6/0/B/1_jadyer.jpg", "http://jadyer.cn/");
        outMsg.addNews("第二个图文的标题", "第二个图文的描述", "http://img.my.csdn.net/uploads/201507/26/1437881866_3678.png", "https://github.com/jadyer");
        outMsg.addNews("第三个图文的标题", "第三个图文的描述", "http://img.my.csdn.net/uploads/201009/14/7892753_1284475095fyR0.jpg", "http://jadyer.cn/2015/10/20/wechat-dev-ngrok-pagekite/");
        return outMsg;
        */
        //防伪
        MppUserInfo mppUserInfo = mppUserService.findByWxid(inTextMsg.getToUserName());
        if(null == mppUserInfo){
            return new WeixinOutTextMsg(inTextMsg).setContent("该公众号未绑定");
        }
        //没绑定就提示绑定
        if(0== mppUserInfo.getBindStatus() && !SeedConstants.MPP_BIND_TEXT.equals(inTextMsg.getContent())){
            return new WeixinOutTextMsg(inTextMsg).setContent("账户未绑定\r请发送\"" + SeedConstants.MPP_BIND_TEXT + "\"绑定");
        }
        //绑定
        if(0== mppUserInfo.getBindStatus() && SeedConstants.MPP_BIND_TEXT.equals(inTextMsg.getContent())){
            mppUserInfo.setBindStatus(1);
            mppUserInfo.setBindTime(new Date());
            mppUserService.upsert(mppUserInfo);
            return new WeixinOutTextMsg(inTextMsg).setContent("绑定完毕，升级成功！");
        }
        //关键字查找（暂时只支持回复文本或转发到多客服）
        MppReplyInfo mppReplyInfo = mppReplyService.getByUidAndKeyword(mppUserInfo.getId(), inTextMsg.getContent());
        if(0 == mppReplyInfo.getType()){
            return new WeixinOutTextMsg(inTextMsg).setContent(mppReplyInfo.getContent());
        }
        //查找通用的回复（暂时设定为转发到多客服）
        mppReplyInfo = mppReplyService.getByUidAndCategory(mppUserInfo.getId(), 0);
        if(4 == mppReplyInfo.getType()){
            return new WeixinOutCustomServiceMsg(inTextMsg);
        }
        //否则原样返回
        return new WeixinOutTextMsg(inTextMsg).setContent(inTextMsg.getContent());
    }


    @Override
    protected WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg) {
        //防伪
        MppUserInfo mppUserInfo = mppUserService.findByWxid(inMenuEventMsg.getToUserName());
        if(null == mppUserInfo){
            return new WeixinOutTextMsg(inMenuEventMsg).setContent("该公众号未绑定");
        }
        //VIEW类的直接跳转过去了，CLICK类的暂定根据关键字回复（找不到关键字就转发到多客服）
        if(WeixinInMenuEventMsg.EVENT_INMENU_CLICK.equals(inMenuEventMsg.getEvent())){
            MppReplyInfo mppReplyInfo = mppReplyService.getByUidAndKeyword(mppUserInfo.getId(), inMenuEventMsg.getEventKey());
            if(StringUtils.isBlank(mppReplyInfo.getKeyword())){
                return new WeixinOutCustomServiceMsg(inMenuEventMsg);
            }else{
                return new WeixinOutTextMsg(inMenuEventMsg).setContent(mppReplyInfo.getContent());
            }
        }
        //跳到URL时，返回特定的消息使得微信服务器不会回复消息给用户手机上
        return new WeixinOutTextMsg(inMenuEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }


    @Override
    protected WeixinOutMsg processInFollowEventMsg(WeixinInFollowEventMsg inFollowEventMsg) {
        //防伪
        MppUserInfo mppUserInfo = mppUserService.findByWxid(inFollowEventMsg.getToUserName());
        if(null == mppUserInfo){
            return new WeixinOutTextMsg(inFollowEventMsg).setContent("该公众号未绑定");
        }
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            //异步记录粉丝关注情况
            fansSaveAsync.save(mppUserInfo, inFollowEventMsg.getFromUserName());
            //目前设定关注后回复文本
            MppReplyInfo mppReplyInfo = mppReplyService.getByUidAndCategory(mppUserInfo.getId(), 1);
            if(StringUtils.isBlank(mppReplyInfo.getContent())){
                return new WeixinOutTextMsg(inFollowEventMsg).setContent("感谢您的关注");
            }else{
                return new WeixinOutTextMsg(inFollowEventMsg).setContent(mppReplyInfo.getContent());
            }
        }
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            fansService.unSubscribe(mppUserInfo.getId(), inFollowEventMsg.getFromUserName());
            LogUtil.getLogger().info("您的粉丝" + inFollowEventMsg.getFromUserName() + "取消关注了您");
        }
        return new WeixinOutTextMsg(inFollowEventMsg).setContent("您的粉丝" + inFollowEventMsg.getFromUserName() + "取消关注了您");
    }


    @ResponseBody
    @RequestMapping(value="/getopenid")
    public String getopenid(String openid){
        return "your openid is [" + openid + "]";
    }


    /**
     * 获取模板列表
     * http://jadyer.ngrok.cc/weixin/pushWeixinTemplateMsgToFans?appid=wx63ae5326e400cca2
     */
    @ResponseBody
    @RequestMapping(value="/getWeixinTemplateList")
    public List<WeixinTemplate> getWeixinTemplateList(String appid){
        return WeixinHelper.getWeixinTemplateList(WeixinTokenHolder.getWeixinAccessToken(appid));
    }


    /**
     * 单发主动推模板消息
     * http://jadyer.ngrok.cc/weixin/pushWeixinTemplateMsgToFans?appid=wx63ae5326e400cca2&openid=o3SHot22_IqkUI7DpahNv-KBiFIs&templateid=hIKe4NumXXpPpgks_cqwnCuAFRZH2Z8sb0BC0L3Hooc
     */
    @ResponseBody
    @RequestMapping(value="/pushWeixinTemplateMsgToFans")
    public WeixinErrorInfo pushWeixinTemplateMsgToFans(String appid, String openid, String templateid){
        WeixinTemplateMsg.DataItem data = new WeixinTemplateMsg.DataItem();
        data.put("first", new WeixinTemplateMsg.DItem("恭喜你购买成功！", "#173177"));
        data.put("remark", new WeixinTemplateMsg.DItem("欢迎再次购买！", "#FFD700"));
        data.put("keynote1", new WeixinTemplateMsg.DItem("巧克力", "#C0F000"));
        data.put("keynote2", new WeixinTemplateMsg.DItem("39.8元", "#006400"));
        data.put("keynote3", new WeixinTemplateMsg.DItem("2014年9月22日"));
        WeixinTemplateMsg templateMsg = new WeixinTemplateMsg();
        templateMsg.setTouser(openid);
        templateMsg.setTemplate_id(templateid);
        templateMsg.setUrl("http://jadyer.cn/");
        templateMsg.setData(data);
        return WeixinHelper.pushWeixinTemplateMsgToFans(WeixinTokenHolder.getWeixinAccessToken(appid), templateMsg);
    }


    /*
    @ResponseBody
    @RequestMapping(value="/getWeixinFansInfo")
    public WeixinFansInfo getWeixinFansInfo(){
        String accesstoken = "axJDAkVIyi5SEYXTnHbWQZypbO0O8p5cDM5CdU0gs4loKMtpB3QCSuK4S6rfPebIArXx33Lmg_U5QoSLflUIHwKQnlGSsTlqhgFk-5RT2y0";
        String openid = "o3SHot22_IqkUI7DpahNv-KBiFIs";
        return WeixinHelper.getWeixinFansInfo(accesstoken, openid);
    }


    @ResponseBody
    @RequestMapping(value="/createWeixinMenu")
    public WeixinErrorInfo createWeixinMenu(){
        String accesstoken = "nHVQXjVPWlyvdglrU6EgGnH_MzvdltddS4HOzUJocjX-wb_NVOi-6rJjumZJayRqwHT7xx80ziBaDCXc6dqddVHheP7g6aJAxv71Lwj3Cxg";
        WeixinSubViewButton btn11 = new WeixinSubViewButton("我的博客", "http://jadyer.cn/");
        WeixinSubViewButton btn22 = new WeixinSubViewButton("我的GitHub", "http://jadyer.tunnel.mobi/weixin/getOpenid?oauth=base&openid=openid");
        WeixinSubClickButton btn33 = new WeixinSubClickButton("历史上的今天", "123abc");
        WeixinSubClickButton btn44 = new WeixinSubClickButton("天气预报", "456");
        WeixinSubClickButton btn55 = new WeixinSubClickButton("幽默笑话", "joke");
        WeixinSuperButton sbtn11 = new WeixinSuperButton("个人中心", new WeixinButton[]{btn11, btn22});
        WeixinSuperButton sbtn22 = new WeixinSuperButton("休闲驿站", new WeixinButton[]{btn33, btn44});
        WeixinMenu menu = new WeixinMenu(new WeixinButton[]{sbtn11, btn55, sbtn22});
        return WeixinHelper.createWeixinMenu(accesstoken, menu);
    }


    @ResponseBody
    @RequestMapping(value="/pushWeixinMsgToFans")
    public WeixinErrorInfo pushWeixinMsgToFans(){
        String accesstoken = "axJDAkVIyi5SEYXTnHbWQZypbO0O8p5cDM5CdU0gs4loKMtpB3QCSuK4S6rfPebIArXx33Lmg_U5QoSLflUIHwKQnlGSsTlqhgFk-5RT2y0";
        String openid = "o3SHot22_IqkUI7DpahNv-KBiFIs";
        //推文本消息
        WeixinCustomTextMsg customTextMsg = new WeixinCustomTextMsg(openid, new WeixinCustomTextMsg.Text("[呲牙]SDK已发布，详见<a href=\"https://github.com/jadyer/seed\">我的Github</a>[呲牙]"));
        WeixinHelper.pushWeixinMsgToFans(accesstoken, customTextMsg);
        //推图文消息
        WeixinCustomNewsMsg.News.Article article11 = new WeixinCustomNewsMsg.News.Article("欢迎访问玄玉博客", "玄玉博客是一个开放态度的Java生态圈", "http://avatar.csdn.net/6/0/B/1_jadyer.jpg", "http://jadyer.cn/");
        WeixinCustomNewsMsg.News.Article article22 = new WeixinCustomNewsMsg.News.Article("玄玉微信SDK", "玄玉微信SDK是一个正在研发中的SDK", "http://img.my.csdn.net/uploads/201507/26/1437881866_3678.png", "https://github.com/jadyer");
        WeixinCustomNewsMsg customNewsMsg = new WeixinCustomNewsMsg(openid, new WeixinCustomNewsMsg.News(new WeixinCustomNewsMsg.News.Article[]{article11, article22}));
        return WeixinHelper.pushWeixinMsgToFans(accesstoken, customNewsMsg);
    }
    */
}