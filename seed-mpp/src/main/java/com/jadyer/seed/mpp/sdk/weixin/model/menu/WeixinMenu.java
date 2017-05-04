package com.jadyer.seed.mpp.sdk.weixin.model.menu;

/**
 * 封装整个菜单对象
 * @create Oct 18, 2015 8:57:46 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class WeixinMenu {
    private WeixinButton[] button;

    public WeixinMenu(WeixinButton[] button) {
        this.button = button;
    }

    public WeixinButton[] getButton() {
        return button;
    }

    public void setButton(WeixinButton[] button) {
        this.button = button;
    }
}