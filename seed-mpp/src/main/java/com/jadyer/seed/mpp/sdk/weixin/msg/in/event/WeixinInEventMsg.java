package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

/**
 * 接收事件推送的公共类
 * @create Oct 18, 2015 5:25:34 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public abstract class WeixinInEventMsg extends WeixinInMsg {
    /**
     * 事件类型
     */
    protected String event;

    public WeixinInEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType);
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }
}