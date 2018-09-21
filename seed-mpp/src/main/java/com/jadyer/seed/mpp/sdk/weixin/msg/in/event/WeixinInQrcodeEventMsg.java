package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

/**
 * 扫描带参数二维码事件
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 用户未关注时,进行关注后的事件推送
 * @see 若用户此时没有点击按钮进行关注,则微信不会推送此事件给开发者服务器
 * @see 若用户点击了关注,那么也只会推送该事件给开发者服务器,不会额外再推送WeixinInFollowEventMsg.java描述的subscribe事件
 * @see <xml>
 * @see     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 * @see     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 * @see     <CreateTime>1456320194</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[subscribe]]></Event>
 * @see     <EventKey><![CDATA[qrscene_xuanyuabc]]></EventKey>
 * @see     <Ticket><![CDATA[gQHy8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL1pVeHIyaFhsOTJfT29HTWdZV1FaAAIE_6jNVgMEAAAAAA==]]></Ticket>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 用户已关注时的事件推送
 * @see <xml>
 * @see     <ToUserName><![CDATA[gh_4769d11d72e0]]></ToUserName>
 * @see     <FromUserName><![CDATA[o3SHot22_IqkUI7DpahNv-KBiFIs]]></FromUserName>
 * @see     <CreateTime>1456320091</CreateTime>
 * @see     <MsgType><![CDATA[event]]></MsgType>
 * @see     <Event><![CDATA[SCAN]]></Event>
 * @see     <EventKey><![CDATA[xuanyuabc]]></EventKey>
 * @see     <Ticket><![CDATA[gQHy8DoAAAAAAAAAASxodHRwOi8vd2VpeGluLnFxLmNvbS9xL1pVeHIyaFhsOTJfT29HTWdZV1FaAAIE_6jNVgMEAAAAAA==]]></Ticket>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @create Oct 18, 2015 6:42:58 PM
 * @author 玄玉<https://jadyer.cn/>
 */
public class WeixinInQrcodeEventMsg extends WeixinInEventMsg {
    /**
     * 用户未关注时,进行关注后的事件推送
     */
    public static final String EVENT_INQRCODE_SUBSCRIBE = "subscribe";

    /**
     * 用户已关注时的事件推送
     */
    public static final String EVENT_INQRCODE_SCAN = "SCAN";

    /**
     * 用户未关注时,进行关注后的事件推送,eventKey=qrscene_场景值
     * 用户已关注时的事件推送,eventKey=场景值
     */
    private String eventKey;

    /**
     * 二维码ticket
     */
    private String ticket;

    public WeixinInQrcodeEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getEventKey() {
        return eventKey;
    }

    public void setEventKey(String eventKey) {
        this.eventKey = eventKey;
    }

    public String getTicket() {
        return ticket;
    }

    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
}