package com.jadyer.seed.mpp.sdk.qq.msg.out;

import com.jadyer.seed.mpp.sdk.qq.msg.in.QQInMsg;

/**
 * 回复图片消息
 * @create Nov 28, 2015 7:39:27 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class QQOutImageMsg extends QQOutMsg {
    private String mediaId;

    public QQOutImageMsg(QQInMsg inMsg) {
        super(inMsg);
        this.msgType = "image";
    }

    public String getMediaId() {
        return mediaId;
    }

    public QQOutImageMsg setMediaId(String mediaId) {
        this.mediaId = mediaId;
        return this;
    }
}