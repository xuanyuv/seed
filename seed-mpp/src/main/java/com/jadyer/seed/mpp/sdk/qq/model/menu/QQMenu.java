package com.jadyer.seed.mpp.sdk.qq.model.menu;

/**
 * 封装整个菜单对象
 * <p>
 *     手机QQ公众号页面右上角菜单，是可以屏蔽的，方法为：在页面URL尾部增加参数“_wv=4099”即可
 *     比如页面URL为：http://qq.msxf.com/view/apply/index?openid=openid&oauth=base
 *     则屏蔽右上角菜单的URL即为：http://qq.msxf.com/view/apply/index?openid=openid&oauth=base&_wv=4099
 *     关于4099这个数字，它是专门用来屏蔽右上角菜单的（比如禁止粉丝分享功能），当然也有其它数字，其各自对应不同的功能
 * </p>
 * Created by 玄玉<https://jadyer.github.io/> on 2015/11/28 20:58.
 */
public class QQMenu {
    private QQButton[] button;

    public QQMenu(QQButton[] button) {
        this.button = button;
    }

    public QQButton[] getButton() {
        return button;
    }

    public void setButton(QQButton[] button) {
        this.button = button;
    }
}