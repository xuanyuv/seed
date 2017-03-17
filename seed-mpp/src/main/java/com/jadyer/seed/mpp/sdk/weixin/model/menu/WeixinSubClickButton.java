package com.jadyer.seed.mpp.sdk.weixin.model.menu;

/**
 * 封装CLICK类型的子菜单项
 * @see 1.这一类菜单有三个固定值name/type/key
 * @see 2.这里的子菜单指的是没有子菜单的菜单项
 * @see   其可能是二级菜单项(微信菜单最多两级),也有能是不含二级菜单的一级菜单(即进入微信第一眼看到的)
 * @create Oct 18, 2015 8:55:19 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinSubClickButton extends WeixinButton {
	private String type;
	private String key;
	
	public WeixinSubClickButton(String name, String key) {
		super(name);
		this.key = key;
		this.type = "click";
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
}