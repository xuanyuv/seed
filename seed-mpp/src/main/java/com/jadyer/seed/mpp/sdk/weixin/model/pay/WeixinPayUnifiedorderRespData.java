package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--统一下单接口出参
 * <p>
 *     该出参为前台页面呼起微信支付所需的json数据实体
 * </p>
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/10 10:39.
 */
public class WeixinPayUnifiedorderRespData {
    private String appId;
    private String timeStamp;
    private String nonceStr;
    private String package_;
    private String signType;
    private String paySign;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getNonceStr() {
        return nonceStr;
    }

    public void setNonceStr(String nonceStr) {
        this.nonceStr = nonceStr;
    }

    public String getPackage_() {
        return package_;
    }

    public void setPackage_(String package_) {
        this.package_ = package_;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public String getPaySign() {
        return paySign;
    }

    public void setPaySign(String paySign) {
        this.paySign = paySign;
    }
}