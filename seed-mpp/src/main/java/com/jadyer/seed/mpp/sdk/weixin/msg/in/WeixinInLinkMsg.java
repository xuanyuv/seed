package com.jadyer.seed.mpp.sdk.weixin.msg.in;

/**
 * 接收链接消息
 * @create Oct 18, 2015 5:16:31 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinInLinkMsg extends WeixinInMsg {
	/**
	 * 消息标题
	 */
	private String title;
	
	/**
	 * 消息描述
	 */
	private String description;
	
	/**
	 * 消息链接
	 */
	private String url;
	
	/**
	 * 64位整型的消息id
	 */
	private String msgId;
	
	public WeixinInLinkMsg(String toUserName, String fromUserName, long createTime, String msgType) {
		super(toUserName, fromUserName, createTime, msgType);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMsgId() {
		return msgId;
	}

	public void setMsgId(String msgId) {
		this.msgId = msgId;
	}
}