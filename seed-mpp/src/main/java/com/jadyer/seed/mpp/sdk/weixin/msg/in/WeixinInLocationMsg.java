package com.jadyer.seed.mpp.sdk.weixin.msg.in;

/**
 * 接收地理位置消息
 * @create Oct 18, 2015 11:42:05 AM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinInLocationMsg extends WeixinInMsg {
    /**
     * 地理位置纬度
     */
    private String location_X;

    /**
     * 地理位置经度
     */
    private String location_Y;

    /**
     * 地图缩放大小
     */
    private String scale;

    /**
     * 地理位置信息
     */
    private String label;

    /**
     * 64位整型的消息id
     */
    private String msgId;

    public WeixinInLocationMsg(String toUserName, String fromUserName, long createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getLocation_X() {
        return location_X;
    }

    public void setLocation_X(String location_X) {
        this.location_X = location_X;
    }

    public String getLocation_Y() {
        return location_Y;
    }

    public void setLocation_Y(String location_Y) {
        this.location_Y = location_Y;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}