package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

/**
 * 回复文本消息
 * @create Oct 18, 2015 2:09:02 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class WeixinOutTextMsg extends WeixinOutMsg {
    private String content;

    public WeixinOutTextMsg(WeixinInMsg inMsg) {
        super(inMsg);
        this.msgType = "text";
    }

    public String getContent() {
        return content;
    }

    public WeixinOutTextMsg setContent(String content) {
        this.content = content;
        return this;
    }
}