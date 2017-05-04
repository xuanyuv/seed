package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

/**
 * 转发多客服消息
 * @create Oct 19, 2015 10:02:30 AM
 * @author 玄玉<http://jadyer.cn/>
 */
public class WeixinOutCustomServiceMsg extends WeixinOutMsg {
    public WeixinOutCustomServiceMsg(WeixinInMsg inMsg) {
        super(inMsg);
        this.msgType = "transfer_customer_service";
    }
}