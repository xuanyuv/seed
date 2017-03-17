package com.jadyer.seed.mpp.sdk.weixin.model;

/**
 * 封装微信服务器返回的操作信息
 * @see 自定义菜单创建成功时微信返回{"errcode":0,"errmsg":"ok"}
 * @see 自定义菜单创建失败时微信返回{"errcode":40018,"errmsg":"invalid button name size"}
 * @create Oct 18, 2015 8:26:44 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinErrorInfo {
	private int errcode;
	private String errmsg;

	public int getErrcode() {
		return errcode;
	}
	public void setErrcode(int errcode) {
		this.errcode = errcode;
	}
	public String getErrmsg() {
		return errmsg;
	}
	public void setErrmsg(String errmsg) {
		this.errmsg = errmsg;
	}
}