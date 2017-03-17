package com.jadyer.seed.mpp.sdk.weixin.msg.out;

import com.jadyer.seed.mpp.sdk.weixin.msg.in.WeixinInMsg;

/**
 * 回复图片消息
 * @create Oct 18, 2015 2:21:40 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinOutImageMsg extends WeixinOutMsg {
	private String mediaId;

	public WeixinOutImageMsg(WeixinInMsg inMsg) {
		super(inMsg);
		this.msgType = "image";
	}

	public String getMediaId() {
		return mediaId;
	}

	public WeixinOutImageMsg setMediaId(String mediaId) {
		this.mediaId = mediaId;
		return this;
	}
}