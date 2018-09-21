package com.jadyer.seed.mpp.sdk.qq.msg.in;

/**
 * 接收地理位置消息
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 以下为20151128193859测试的收到QQ服务器的图片消息
 * @see POST /mpp/qq/e9293c3886c411e5bc85000c292d56c5?openId=E12D231CFC30438FB6970B0C7669C101&puin=2878591677 HTTP/1.0
 * @see host: weixinapi.msxf.com
 * @see x-forwarded-for: 14.17.43.102
 * @see connection: close
 * @see content-length: 383
 * @see user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36
 * @see accept: text/xml,application/xml,application/xhtml+xml,text/html,text/plain,image/png,image/jpeg,image/gif,/*
 * @see accept-language: zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2
 * @see referer: http://weixinapi.msxf.com/mpp/qq/e9293c3886c411e5bc85000c292d56c5
 * @see content-type: text/xml
 * @see 
 * @see <xml><ToUserName><![CDATA[2878591677]]></ToUserName><FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101]]></FromUserName><CreateTime>1448711382</CreateTime><MsgType><![CDATA[location]]></MsgType><Location_X>29.616652</Location_X><Location_Y>106.501167</Location_Y><Scale>20</Scale><Label><![CDATA[重庆市渝北区黄山大道中段6号]]></Label><MsgId>1714971960</MsgId></xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @see HTTP请求报文体格式化后是下面这样
 * @see <xml>
 * @see     <ToUserName><![CDATA[2878591677]]></ToUserName>
 * @see     <FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101]]></FromUserName>
 * @see     <CreateTime>1448711382</CreateTime>
 * @see     <MsgType><![CDATA[location]]></MsgType>
 * @see     <Location_X>29.616652</Location_X>
 * @see     <Location_Y>106.501167</Location_Y>
 * @see     <Scale>20</Scale>
 * @see     <Label><![CDATA[重庆市渝北区黄山大道中段6号]]></Label>
 * @see     <MsgId>1714971960</MsgId>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @create Nov 28, 2015 6:59:55 PM
 * @author 玄玉<https://jadyer.cn/>
 */
public class QQInLocationMsg extends QQInMsg {
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

    public QQInLocationMsg(String toUserName, String fromUserName, long createTime, String msgType) {
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