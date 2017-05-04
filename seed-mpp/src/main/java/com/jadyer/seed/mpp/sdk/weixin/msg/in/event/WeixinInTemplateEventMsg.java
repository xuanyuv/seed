package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

/**
 * 微信模板消息送达事件
 * -----------------------------------------------------------------------------------------------------------
 * 送达成功时，推送的XML如下
 * <xml>
 *     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 *     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 *     <CreateTime>1464077222</CreateTime>
 *     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>
 *     <MsgID>454401671</MsgID>
 *     <Status><![CDATA[success]]></Status>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * 送达由于用户拒收（用户设置拒绝接收公众号消息）而失败时，推送的XML如下
 * <xml>
 *     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 *     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 *     <CreateTime>1456320194</CreateTime>
 *     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>
 *     <MsgID>200163836</MsgID>
 *     <Status><![CDATA[failed:user block]]></Status>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * 送达由于其他原因失败时，推送的XML如下
 * <xml>
 *     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 *     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 *     <CreateTime>1456320194</CreateTime>
 *     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>
 *     <MsgID>200163836</MsgID>
 *     <Status><![CDATA[failed: system failed]]></Status>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * 阅读回执事件之阅读状态为成功时，推送的XML如下
 * <p>
 *     201605241613通過微信接口測試號mp.weixin.qq.com/debug/cgi-bin/sandbox?t=sandbox/login驗證<br/>
 *     發現微信服務器不會推送此事件，即我們無法清楚用戶是否閱讀了模板消息
 * </p>
 * <xml>
 *     <ToUserName><![CDATA[gh_7f083739789a]]></ToUserName>
 *     <FromUserName><![CDATA[oia2TjuEGTNoeX76QEjQNrcURxG8]]></FromUserName>
 *     <CreateTime>1395658920</CreateTime>
 *     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[TEMPLATEFANMSGREAD]]></Event>
 *     <MsgID>200163836</MsgID>
 *     <Status><![CDATA[success]]></Status>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<http://jadyer.cn/> on 2016/5/18 20:06.
 */
public class WeixinInTemplateEventMsg extends WeixinInEventMsg {
    /**
     * 模板消息送达事件
     */
    public static final String EVENT_INTEMPLATE_TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";

    /**
     * 模板消息阅读回执事件
     */
    public static final String EVENT_INTEMPLATE_TEMPLATEFANMSGREAD = "TEMPLATEFANMSGREAD";

    /**
     * 模板消息送达成功
     */
    public static final String EVENT_INTEMPLATE_STATUS_SUCCESS = "success";

    /**
     * 模板消息由于用户拒收而送达失败（用户设置拒绝接收公众号消息）
     */
    public static final String EVENT_INTEMPLATE_STATUS_BLOCK = "failed:user block";

    /**
     * 模板消息由于其他原因而送达失败
     */
    public static final String EVENT_INTEMPLATE_STATUS_FAILED = "failed: system failed";

    /**
     * 消息id
     */
    private String msgID;

    /**
     * 发送状态
     */
    private String status;

    public WeixinInTemplateEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
