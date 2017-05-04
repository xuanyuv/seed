package com.jadyer.seed.mpp.sdk.weixin.model.custom;

/**
 * 客服接口发送文本消息
 * @create Oct 18, 2015 10:41:17 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class WeixinCustomTextMsg extends WeixinCustomMsg {
    /**
     * 消息类型(文本为text,图片为image,语音为voice,视频消息为video,音乐消息为music,图文消息为news,卡券为wxcard)
     */
    private String msgtype;

    /**
     * 封装文本消息内容的对象
     */
    private Text text;

    public WeixinCustomTextMsg(String touser, Text text) {
        super(touser);
        this.text = text;
        this.msgtype = "text";
    }

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public Text getText() {
        return text;
    }

    public void setText(Text text) {
        this.text = text;
    }

    public static class Text{
        private String content;
        public Text(String content) {
            this.content = content;
        }
        public String getContent() {
            return content;
        }
    }
}