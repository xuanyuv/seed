package com.jadyer.seed.mpp.sdk.qq.msg.out;

import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInMsg;

/**
 * 被动回复消息的公共类
 * @see 禁止SDK接入方使用此类
 * @create Nov 26, 2015 7:36:13 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class QQOutMsg {
    /**
     * 接收方帐号(收到的OpenID)
     */
    protected String toUserName;

    /**
     * 开发者QQ号
     */
    protected String fromUserName;

    /**
     * 消息创建时间
     */
    protected long createTime;

    /**
     * 被动回复消息的消息类型
     */
    protected String msgType;

    public QQOutMsg() {}

    public QQOutMsg(QQInMsg inMsg) {
        this.toUserName = inMsg.getFromUserName();
        this.fromUserName = inMsg.getToUserName();
        this.createTime = (long)(System.currentTimeMillis()/1000);
    }

    public String getToUserName() {
        return toUserName;
    }

    public void setToUserName(String toUserName) {
        this.toUserName = toUserName;
    }

    public String getFromUserName() {
        return fromUserName;
    }

    public void setFromUserName(String fromUserName) {
        this.fromUserName = fromUserName;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getMsgType() {
        return msgType;
    }

    public void setMsgType(String msgType) {
        this.msgType = msgType;
    }
}