package com.jadyer.seed.mpp.sdk.weixin.model.redpack;

/**
 * 微信红包--查询红包记录--接口入参
 * https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_6&index=5
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/28 10:51.
 */
public class WeixinRedpackGethbinfoReqData extends WeixinRedpackReqData {
    /** 微信分配的公众账号ID（企业号corpid即为此appId），接口传入的所有appid应该为公众号的appid（在mp.weixin.qq.com申请的），不能为APP的appid（在open.weixin.qq.com申请的）。 */
    private String appid;

    /** 商户发放红包的商户订单号 */
    private String mch_billno;

    /**
     * 订单类型
     * MCHT:通过商户订单号获取红包信息
     */
    private String bill_type;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getBill_type() {
        return bill_type;
    }

    public void setBill_type(String bill_type) {
        this.bill_type = bill_type;
    }
}