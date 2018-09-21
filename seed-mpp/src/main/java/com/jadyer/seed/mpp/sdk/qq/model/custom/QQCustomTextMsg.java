package com.jadyer.seed.mpp.sdk.qq.model.custom;

/**
 * 单发文本消息
 * @create Nov 28, 2015 9:47:11 PM
 * @author 玄玉<https://jadyer.cn/>
 */
public class QQCustomTextMsg extends QQCustomMsg {
    /**
     * 消息类型(文本为text,图片为image,语音为voice,视频消息为video,音乐消息为music,图文消息为mpnews)
     */
    private String msgtype;

    /**
     * 封装文本消息内容的对象
     */
    private Text text;

    public QQCustomTextMsg(String tousername, Text text) {
        super(tousername);
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