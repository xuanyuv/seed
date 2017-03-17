package com.jadyer.seed.mpp.sdk.weixin.msg.in;

/**
 * 接收消息的公共类
 * @see 1.禁止SDK接入方使用此类
 * @see 2.由于事件推送类消息没有msgId,所以没有在这里定义
 * @create Oct 18, 2015 11:18:47 AM
 * @author 玄玉<https://jadyer.github.io/>
 */
public abstract class WeixinInMsg {
	/**
	 * 开发者微信号
	 */
	protected String toUserName;

	/**
	 * 发送方微信号,若为普通用户,则是一个OpenID
	 */
	protected String fromUserName;
	
	/**
	 * 消息创建时间
	 * @see 1.它表示1970年1月1日0时0分0秒至消息创建时所间隔的秒数(注意是间隔的秒数,不是毫秒数)
	 * @see 2.System.currentTimeMillis()与new java.util.Date().getTime()是等价的
	 * @see   但它俩获取到的结果是表示当时时间距离1970年1月1日0时0分0秒0毫秒的毫秒数(注意这里是毫秒数)
	 * @see 3.可以通过下面的方式把long的时间转换为我们熟识的时间格式
	 * @see   org.apache.commons.lang3.time.DateFormatUtils.format(new Date(this.CreateTime*1000), "yyyy-MM-dd HH:mm:ss")
	 */
	protected long createTime;

	/**
	 * 消息类型
	 */
	protected String msgType;

	public WeixinInMsg(String toUserName, String fromUserName, long createTime, String msgType) {
		this.toUserName = toUserName;
		this.fromUserName = fromUserName;
		this.createTime = createTime;
		this.msgType = msgType;
	}

	public String getToUserName() {
		return toUserName;
	}

	public void setToUserName(String toUserName) {
		this.toUserName = toUserName;
	}

	public String getFromUserName() {
		return fromUserName;
	}

	public void setFromUserName(String fromUserName) {
		this.fromUserName = fromUserName;
	}

	public long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}

	public String getMsgType() {
		return msgType;
	}

	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
}