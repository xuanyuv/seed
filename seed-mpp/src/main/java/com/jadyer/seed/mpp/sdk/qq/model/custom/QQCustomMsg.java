package com.jadyer.seed.mpp.sdk.qq.model.custom;

/**
 * 单发请求参数的基类
 * @create Nov 28, 2015 9:39:07 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public abstract class QQCustomMsg {
	/**
	 * 消息接收者的openid
	 */
	private String tousername;

	public QQCustomMsg(String tousername) {
		this.tousername = tousername;
	}

	public String getTousername() {
		return tousername;
	}

	public void setTousername(String tousername) {
		this.tousername = tousername;
	}
}