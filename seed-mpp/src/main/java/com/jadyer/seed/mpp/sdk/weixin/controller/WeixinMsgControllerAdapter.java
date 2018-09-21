package com.jadyer.seed.mpp.sdk.weixin.controller;

import com.jadyer.seed.comm.util.LogUtil;
import com.jadyer.seed.mpp.sdk.weixin.constant.WeixinConstants;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLinkMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInLocationMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInTextMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInCustomServiceEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInFollowEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInLocationEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInMenuEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInQrcodeEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.in.event.WeixinInTemplateEventMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutImageMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutMsg;
import com.jadyer.seed.mpp.sdk.weixin.msg.out.WeixinOutTextMsg;

/**
 * 通用的微信消息Adapter
 * @see 对WeixinMsgController部分方法提供默认实现,以便开发者可以只关注需要处理的抽象方法
 * @create Oct 19, 2015 11:33:07 AM
 * @author 玄玉<https://jadyer.cn/>
 */
abstract class WeixinMsgControllerAdapter extends WeixinMsgController {
    @Override
    protected abstract WeixinOutMsg processInMenuEventMsg(WeixinInMenuEventMsg inMenuEventMsg);


    /**
     * 处理收到的文本消息
     * @see 默认原样返回
     */
    @Override
    protected WeixinOutMsg processInTextMsg(WeixinInTextMsg inTextMsg) {
        return new WeixinOutTextMsg(inTextMsg).setContent(inTextMsg.getContent());
    }


    /**
     * 处理收到的图片消息
     * @see 默认原样返回
     */
    @Override
    protected WeixinOutMsg processInImageMsg(WeixinInImageMsg inImageMsg) {
        return new WeixinOutImageMsg(inImageMsg).setMediaId(inImageMsg.getMediaId());
    }


    /**
     * 处理收到的地址位置消息
     * @see 默认返回粉丝地理位置的明文地址
     */
    @Override
    protected WeixinOutMsg processInLocationMsg(WeixinInLocationMsg inLocationMsg) {
        return new WeixinOutTextMsg(inLocationMsg).setContent(inLocationMsg.getLabel());
    }


    /**
     * 处理收到的链接消息
     * @see 默认返回用户输入的链接
     */
    @Override
    protected WeixinOutMsg processInLinkMsg(WeixinInLinkMsg inLinkMsg) {
        return new WeixinOutTextMsg(inLinkMsg).setContent("您的链接为<a href=\""+inLinkMsg.getUrl()+"\">"+inLinkMsg.getTitle()+"</a>");
    }


    /**
     * 处理收到的关注/取消关注事件
     * @see 关注时默认返回欢迎语"感谢您的关注"
     * @see 取消专注时默认会打印取消关注的信息
     */
    @Override
    protected WeixinOutMsg processInFollowEventMsg(WeixinInFollowEventMsg inFollowEventMsg){
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_SUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            return new WeixinOutTextMsg(inFollowEventMsg).setContent("感谢您的关注");
        }
        if(WeixinInFollowEventMsg.EVENT_INFOLLOW_UNSUBSCRIBE.equals(inFollowEventMsg.getEvent())){
            LogUtil.getLogger().info("您的粉丝{}取消关注了您", inFollowEventMsg.getFromUserName());
        }
        return new WeixinOutTextMsg(inFollowEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }


    /**
     * 处理收到的扫描带参数二维码事件
     */
    @Override
    protected WeixinOutMsg processInQrcodeEventMsg(WeixinInQrcodeEventMsg inQrcodeEventMsg) {
        if(WeixinInQrcodeEventMsg.EVENT_INQRCODE_SUBSCRIBE.equals(inQrcodeEventMsg.getEvent())){
            return new WeixinOutTextMsg(inQrcodeEventMsg).setContent("感谢您的扫码并关注[" + inQrcodeEventMsg.getEventKey().substring(8) + "]");
        }
        if(WeixinInQrcodeEventMsg.EVENT_INQRCODE_SCAN.equals(inQrcodeEventMsg.getEvent())){
            return new WeixinOutTextMsg(inQrcodeEventMsg).setContent("感谢您的扫码[" + inQrcodeEventMsg.getEventKey() + "]");
        }
        return new WeixinOutTextMsg(inQrcodeEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }


    /**
     * 处理多客服接入会话/关闭会话/转接会话的事件
     * @see 默认返回事件详情描述,无法识别事件时返回欢迎语
     */
    @Override
    protected WeixinOutMsg processInCustomServiceEventMsg(WeixinInCustomServiceEventMsg inCustomServiceEventMsg){
        if(WeixinInCustomServiceEventMsg.EVENT_INCUSTOMSERVICE_KF_CREATE_SESSION.equals(inCustomServiceEventMsg.getEvent())){
            LogUtil.getLogger().info("客服{}接入了会话", inCustomServiceEventMsg.getKfAccount());
            return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent("客服" + inCustomServiceEventMsg.getKfAccount() + "接入了会话");
        }
        if(WeixinInCustomServiceEventMsg.EVENT_INCUSTOMSERVICE_KF_CLOSE_SESSION.equals(inCustomServiceEventMsg.getEvent())){
            LogUtil.getLogger().info("客服{}关闭了会话", inCustomServiceEventMsg.getKfAccount());
            return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent("客服" + inCustomServiceEventMsg.getKfAccount() + "关闭了会话");
        }
        if(WeixinInCustomServiceEventMsg.EVENT_INCUSTOMSERVICE_KF_SWITCH_SESSION.equals(inCustomServiceEventMsg.getEvent())){
            LogUtil.getLogger().info("客服{}将会话转接给了客服{}", inCustomServiceEventMsg.getKfAccount(), inCustomServiceEventMsg.getToKfAccount());
            return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent("客服" + inCustomServiceEventMsg.getKfAccount() + "将会话转接给了客服" + inCustomServiceEventMsg.getToKfAccount());
        }
        return new WeixinOutTextMsg(inCustomServiceEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }


    /**
     * 处理模板消息送达事件
     * <p>默认返回特定的消息使得微信服务器不会回复消息给用户手机上</p>
     */
    @Override
    protected WeixinOutMsg processInTemplateEventMsg(WeixinInTemplateEventMsg inTemplateEventMsg) {
        if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_TEMPLATEFANMSGREAD.equals(inTemplateEventMsg.getEvent())){
            LogUtil.getLogger().info("模板消息msgid={}阅读成功", inTemplateEventMsg.getMsgID());
        }
        if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_TEMPLATESENDJOBFINISH.equals(inTemplateEventMsg.getEvent())){
            if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_SUCCESS.equals(inTemplateEventMsg.getStatus())){
                LogUtil.getLogger().info("模板消息msgid={}送达成功", inTemplateEventMsg.getMsgID());
            }
            if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_BLOCK.equals(inTemplateEventMsg.getStatus())){
                LogUtil.getLogger().info("模板消息msgid={}由于{}而送达失败", inTemplateEventMsg.getMsgID(), "用户拒收（用户设置拒绝接收公众号消息）");
            }
            if(WeixinInTemplateEventMsg.EVENT_INTEMPLATE_STATUS_FAILED.equals(inTemplateEventMsg.getStatus())){
                LogUtil.getLogger().info("模板消息msgid={}由于{}而送达失败", inTemplateEventMsg.getMsgID(), "其它原因");
            }
        }
        return new WeixinOutTextMsg(inTemplateEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }


    /**
     * 上報地理位置事件
     */
    @Override
    protected WeixinOutMsg processInLocationEventMsg(WeixinInLocationEventMsg inLocationEventMsg) {
        LogUtil.getLogger().info("收到粉絲=[{}]上報的地理位置纬度=[{}]，经度=[{}]，精度=[{}]", inLocationEventMsg.getFromUserName(), inLocationEventMsg.getLatitude(), inLocationEventMsg.getLongitude(), inLocationEventMsg.getPrecision());
        return new WeixinOutTextMsg(inLocationEventMsg).setContent(WeixinConstants.NOT_NEED_REPLY_FLAG);
    }
}