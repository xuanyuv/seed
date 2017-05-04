package com.jadyer.seed.mpp.sdk.qq.msg.in.event;

/**
 * 接收关注/取消关注事件
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 20151128195353测试发现,关注和取消关注时,QQ服务器发送到开发者服务器的xml和微信服务器不一样
 * @see 微信服务器推送的会比官方文档多一个控制的EventKey元素,而QQ服务器不会多推送EventKey元素
 * @see <xml>
 * @see     <ToUserName><![CDATA[2878591677]]></ToUserName>
 * @see     <FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101-KBiFIs]]></FromUserName>
 * @see     <CreateTime>1448711806</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[subscribe]]></Event>
 * @see </xml>
 * @see <xml>
 * @see     <ToUserName><![CDATA[2878591677]]></ToUserName>
 * @see     <FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101-KBiFIs]]></FromUserName>
 * @see     <CreateTime>1448711701</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[unsubscribe]]></Event>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @create Nov 26, 2015 7:39:05 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class QQInFollowEventMsg extends QQInEventMsg {
    /**
     * 关注事件
     */
    public static final String EVENT_INFOLLOW_SUBSCRIBE = "subscribe";

    /**
     * 取消关注事件
     */
    public static final String EVENT_INFOLLOW_UNSUBSCRIBE = "unsubscribe";

    public QQInFollowEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }
}