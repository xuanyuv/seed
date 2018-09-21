package com.jadyer.seed.mpp.sdk.qq.msg.in;

/**
 * 接收文本消息
 * @see -----------------------------------------------------------------------------------------------------------
 * @see 以下为20151128173823测试的收到QQ服务器的文本消息
 * @see POST /mpp/qq/e9293c3886c411e5bc85000c292d56c5?openId=E12D231CFC30438FB6970B0C7669C101&puin=2878591677 HTTP/1.0
 * @see host: weixinapi.msxf.com
 * @see x-forwarded-for: 14.17.43.104
 * @see connection: close
 * @see content-length: 262
 * @see user-agent: Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36
 * @see accept: text/xml,application/xml,application/xhtml+xml,text/html,text/plain,image/png,image/jpeg,image/gif,/*
 * @see accept-language: zh-CN,zh;q=0.8,en;q=0.6,zh-TW;q=0.4,ja;q=0.2
 * @see referer: http://weixinapi.msxf.com/mpp/qq/e9293c3886c411e5bc85000c292d56c5
 * @see content-type: text/xml
 * @see 
 * @see <xml><ToUserName><![CDATA[2878591677]]></ToUserName><FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101]]></FromUserName><CreateTime>1448703573</CreateTime><MsgType><![CDATA[text]]></MsgType><Content><![CDATA[你好]]></Content><MsgId>875639142</MsgId></xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @see HTTP请求报文体格式化后是下面这样
 * @see <xml>
 * @see     <ToUserName><![CDATA[2878591677]]></ToUserName>
 * @see     <FromUserName><![CDATA[E12D231CFC30438FB6970B0C7669C101]]></FromUserName>
 * @see     <CreateTime>1448703573</CreateTime>
 * @see     <MsgType><![CDATA[text]]></MsgType>
 * @see     <Content><![CDATA[你好]]></Content>
 * @see     <MsgId>875639142</MsgId>
 * @see </xml>
 * @see -----------------------------------------------------------------------------------------------------------
 * @create Nov 26, 2015 7:38:16 PM
 * @author 玄玉<https://jadyer.cn/>
 */
public class QQInTextMsg extends QQInMsg {
    /**
     * 文本消息内容
     */
    private String content;

    /**
     * 64位整型的消息id
     */
    private String msgId;

    public QQInTextMsg(String toUserName, String fromUserName, long createTime, String msgType) {
        super(toUserName, fromUserName, createTime, msgType);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }
}