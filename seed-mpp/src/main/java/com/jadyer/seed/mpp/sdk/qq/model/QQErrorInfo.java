package com.jadyer.seed.mpp.sdk.qq.model;

/**
 * 封装QQ服务器返回的操作信息
 * @see 自定义菜单创建成功时QQ返回{"errcode":0,"errmsg":"ok"}
 * @see 自定义菜单创建失败时QQ返回{"errcode":40018,"errmsg":"invalid button name size"}
 * @create Nov 28, 2015 8:35:57 PM
 * @author 玄玉<https://jadyer.github.io/>
 */
public class QQErrorInfo {
    private int errcode;
    private String errmsg;
    private String msgid;

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
    public String getMsgid() {
        return msgid;
    }
    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }
}