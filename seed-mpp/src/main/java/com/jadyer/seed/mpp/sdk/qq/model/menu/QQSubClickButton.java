package com.jadyer.seed.mpp.sdk.qq.model.menu;

/**
 * 封装CLICK类型的子菜单项
 * @see 1.这一类菜单有三个固定值name/type/key
 * @see 2.这里的子菜单指的是没有子菜单的菜单项
 * @see   其可能是二级菜单项(QQ公众号菜单最多两级),也有能是不含二级菜单的一级菜单(即进入QQ公众号第一眼看到的)
 * @create Nov 28, 2015 8:58:36 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class QQSubClickButton extends QQButton {
    private String type;
    private String key;

    public QQSubClickButton(String name, String key) {
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