package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

/**
 * 接收关注/取消关注事件
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 测试发现,关注和取消关注时,微信服务器主动推给开发者服务器的xml都比官方文档多一个空值的EventKey元素
 * @see 事实上多的EventKey元素,当用户通过扫码关注后,它就有值了,详见WeixinInQrcodeEventMsg.java
 * @see <xml>
 * @see     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 * @see     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 * @see     <CreateTime>1445160792</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[subscribe]]></Event>
 * @see     <EventKey><![CDATA[]]></EventKey>
 * @see </xml>
 * @see <xml>
 * @see     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 * @see     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 * @see     <CreateTime>1445160733</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[unsubscribe]]></Event>
 * @see     <EventKey><![CDATA[]]></EventKey>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @create Oct 18, 2015 5:26:59 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinInFollowEventMsg extends WeixinInEventMsg {
    /**
     * 关注事件
     */
    public static final String EVENT_INFOLLOW_SUBSCRIBE = "subscribe";

    /**
     * 取消关注事件
     */
    public static final String EVENT_INFOLLOW_UNSUBSCRIBE = "unsubscribe";

    public WeixinInFollowEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }
}