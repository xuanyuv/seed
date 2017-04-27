package com.jadyer.seed.mpp.sdk.qq.msg.in;

/**
 * 接收图片消息
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 以下为20151128193859测试的收到QQ服务器的图片消息
 * @see POST /mpp/qq/e9293c3886c411e5bc85000c292d56c5?openId=E12D231CFC30438FB6970B0C7669C101&puin=2878591677 HTTP/1.0
 * @see host: weixinapi.msxf.com
 * @see x-forwarded-for: 14.17.43.107
 * @see connection: close
 * @see content-length: 373
 * @see user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36
 * @see accept: text/xml,application/xml,application/xhtml+xml,text/html,text/plain,image/png,image/jpeg,image/gif,/*
 * @see accept-language: zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2
 * @see referer: http://weixinapi.msxf.com/mpp/qq/e9293c3886c411e5bc85000c292d56c5
 * @see content-type: text/xml
 * @see 
 * @see <xml><ToUserName><![CDATA[2878591677]]></ToUserName><FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101]]></FromUserName><CreateTime>1448710809</CreateTime><MsgType><![CDATA[image]]></MsgType><PicUrl><![CDATA[http://c2cpicdw.qpic.cn/offpic_new/2878591677//20a4d5c4-674d-4341-b118-c22ee91b23ac/0?vuin=2878591677&term=255&srvver=]]></PicUrl><MsgId>909665634</MsgId></xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @see HTTP请求报文体格式化后是下面这样
 * @see <xml>
 * @see     <ToUserName><![CDATA[2878591677]]></ToUserName>
 * @see     <FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101]]></FromUserName>
 * @see     <CreateTime>1448710809</CreateTime>
 * @see     <MsgType><![CDATA[image]]></MsgType>
 * @see     <PicUrl><![CDATA[http://c2cpicdw.qpic.cn/offpic_new/2878591677//20a4d5c4-674d-4341-b118-c22ee91b23ac/0?vuin=2878591677&term=255&srvver=]]></PicUrl>
 * @see     <MsgId>909665634</MsgId>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @create Nov 28, 2015 7:40:59 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class QQInImageMsg extends QQInMsg {
    /**
     * 图片链接
     */
    private String picUrl;

//    /**
//     * 图片消息媒体id(可以调用多媒体文件下载接口拉取数据)
//     */
//    private String mediaId;

    /**
     * 64位整型的消息id
     */
    private String msgId;

    public QQInImageMsg(String toUserName, String fromUserName, long createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

//    public String getMediaId() {
//        return mediaId;
//    }
//
//    public void setMediaId(String mediaId) {
//        this.mediaId = mediaId;
//    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}