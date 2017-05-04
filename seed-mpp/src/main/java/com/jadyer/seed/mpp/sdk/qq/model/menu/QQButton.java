package com.jadyer.seed.mpp.sdk.qq.model.menu;

/**
 * 按钮的基类
 * @see 所有一级菜单、二级菜单都有一个相同的name属性
 * @create Nov 28, 2015 8:58:18 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public abstract class QQButton {
    private String name;

    public QQButton(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}