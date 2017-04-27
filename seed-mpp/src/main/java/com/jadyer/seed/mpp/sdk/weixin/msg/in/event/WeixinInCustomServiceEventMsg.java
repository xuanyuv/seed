package com.jadyer.seed.mpp.sdk.weixin.msg.in.event;

/**
 * 多客服会话状态通知事件
 * @create Oct 19, 2015 10:12:02 AM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class WeixinInCustomServiceEventMsg extends WeixinInEventMsg {
    /**
     * 接入会话
     */
    public static final String EVENT_INCUSTOMSERVICE_KF_CREATE_SESSION = "kf_create_session";

    /**
     * 关闭会话
     */
    public static final String EVENT_INCUSTOMSERVICE_KF_CLOSE_SESSION = "kf_close_session";

    /**
     * 转接会话
     */
    public static final String EVENT_INCUSTOMSERVICE_KF_SWITCH_SESSION = "kf_switch_session";

    private String kfAccount;
    private String toKfAccount;

    public WeixinInCustomServiceEventMsg(String toUserName, String fromUserName, long createTime, String msgType, String event) {
        super(toUserName, fromUserName, createTime, msgType, event);
    }

    public String getKfAccount() {
        return kfAccount;
    }

    public void setKfAccount(String kfAccount) {
        this.kfAccount = kfAccount;
    }

    public String getToKfAccount() {
        return toKfAccount;
    }

    public void setToKfAccount(String toKfAccount) {
        this.toKfAccount = toKfAccount;
    }
}