package com.jadyer.seed.mpp.sdk.weixin.model.redpack;

/**
 * 微信红包--发放普通红包--接口出参
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/28 10:46.
 */
public class WeixinRedpackSendRespData extends WeixinRedpackRespData {
    /** 商户appid，接口传入的所有appid应该为公众号的appid（在mp.weixin.qq.com申请的），不能为APP的appid（在open.weixin.qq.com申请的）。 */
    private String wxappid;

    /** 商户订单号（每个订单号必须唯一）。组成：mch_id+yyyymmdd+10位一天内不能重复的数字 */
    private String mch_billno;

    /** 接受红包的用户在wxappid下的openid */
    private String re_openid;

    /** 付款金额，单位：分 */
    private int total_amount;

    /** 红包订单的微信单号 */
    private String send_listid;

    public String getWxappid() {
        return wxappid;
    }

    public void setWxappid(String wxappid) {
        this.wxappid = wxappid;
    }

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getRe_openid() {
        return re_openid;
    }

    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getSend_listid() {
        return send_listid;
    }

    public void setSend_listid(String send_listid) {
        this.send_listid = send_listid;
    }
}