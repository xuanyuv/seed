package com.jadyer.seed.mpp.sdk.qq.msg.in.event;

/**
 * QQ模板消息送达事件
 * -----------------------------------------------------------------------------------------------------------
 * 送达成功时，推送的XML如下
 * <xml>
 *     <ToUserName><![CDATA[gh_7f083739789a]]></ToUserName>
 *     <FromUserName><![CDATA[oia2TjuEGTNoeX76QEjQNrcURxG8]]></FromUserName>
 *     <CreateTime>1395658920</CreateTime>
 //*     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>
 *     <MsgID>200163836</MsgID>
 *     <Status><![CDATA[success]]></Status>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * 送达由于用户拒收（用户设置拒绝接收公众号消息）而失败时，推送的XML如下
 * <xml>
 *     <ToUserName><![CDATA[gh_7f083739789a]]></ToUserName>
 *     <FromUserName><![CDATA[oia2TjuEGTNoeX76QEjQNrcURxG8]]></FromUserName>
 *     <CreateTime>1395658984</CreateTime>
 //*     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>
 *     <MsgID>200163840</MsgID>
 *     <Status><![CDATA[failed:user block]]></Status>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * 送达由于其他原因失败时，推送的XML如下
 * <xml>
 *     <ToUserName><![CDATA[gh_7f083739789a]]></ToUserName>
 *     <FromUserName><![CDATA[oia2TjuEGTNoeX76QEjQNrcURxG8]]></FromUserName>
 *     <CreateTime>1395658984</CreateTime>
 //*     <MsgType><![CDATA[event]]></MsgType>
 *     <Event><![CDATA[TEMPLATESENDJOBFINISH]]></Event>
 *     <MsgID>200163840</MsgID>
 *     <Status><![CDATA[failed: system failed]]></Status>
 * </xml>
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.cn/> on 2016/5/19 18:44.
 */
public class QQInTemplateEventMsg extends QQInEventMsg {
    /**
     * 模板消息送达事件
     */
    public static final String EVENT_INTEMPLATE_TEMPLATESENDJOBFINISH = "TEMPLATESENDJOBFINISH";

    /**
     * 模板消息阅读回执事件
     * <p>待验证</p>
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

    public QQInTemplateEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
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