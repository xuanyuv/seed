package com.jadyer.seed.mpp.sdk.qq.model.menu;

/**
 * 封装父菜单项
 * @see 1.这类菜单项有两个固定属性name和sub_button
 * @see   而sub_button是一个可能为SubClickButton或SubViewButton子菜单项数组
 * @see 2.这里的父菜单指的是包含有二级菜单项的一级菜单
 * @create Nov 28, 2015 8:59:27 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class QQSuperButton extends QQButton {
	private QQButton[] sub_button;
	
	public QQSuperButton(String name, QQButton[] sub_button) {
		super(name);
		this.sub_button = sub_button;
	}

	public QQButton[] getSub_button() {
		return sub_button;
	}

	public void setSub_button(QQButton[] sub_button) {
		this.sub_button = sub_button;
	}
}