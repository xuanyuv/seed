package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

/**
 * 自定义菜单事件
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 点击菜单拉取消息时的事件推送
 * @see <xml>
 * @see     <ToUserName><![CDATA[toUser]]></ToUserName>
 * @see     <FromUserName><![CDATA[FromUser]]></FromUserName>
 * @see     <CreateTime>123456789</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[CLICK]]></Event>
 * @see     <EventKey><![CDATA[EVENTKEY]]></EventKey>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 点击菜单跳转链接时的事件推送
 * @see <xml>
 * @see     <ToUserName><![CDATA[toUser]]></ToUserName>
 * @see     <FromUserName><![CDATA[FromUser]]></FromUserName>
 * @see     <CreateTime>123456789</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[VIEW]]></Event>
 * @see     <EventKey><![CDATA[www.qq.com]]></EventKey>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @create Oct 18, 2015 6:42:58 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinInMenuEventMsg extends WeixinInEventMsg {
    /**
     * 点击菜单拉取消息时的事件
     */
    public static final String EVENT_INMENU_CLICK = "CLICK";

    /**
     * 点击菜单跳转链接时的事件
     */
    public static final String EVENT_INMENU_VIEW = "VIEW";

    private String eventKey;

    public WeixinInMenuEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }
}