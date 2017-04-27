package com.jadyer.seed.mpp.sdk.weixin.msg.in;

/**
 * 接收图片消息
 * @create Oct 18, 2015 11:37:50 AM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinInImageMsg extends WeixinInMsg {
    /**
     * 图片链接
     */
    private String picUrl;

    /**
     * 图片消息媒体id(可以调用多媒体文件下载接口拉取数据)
     */
    private String mediaId;

    /**
     * 64位整型的消息id
     */
    private String msgId;

    public WeixinInImageMsg(String toUserName, String fromUserName, long createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}