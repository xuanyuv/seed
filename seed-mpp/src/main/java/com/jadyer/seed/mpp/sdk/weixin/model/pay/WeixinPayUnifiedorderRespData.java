package com.jadyer.seed.mpp.sdk.weixin.model.pay;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 微信支付--公众号支付--统一下单接口出参
 * <p>
 *     该出参为前台页面呼起微信支付所需的json数据实体
 * </p>
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/10 10:39.
 */
public class WeixinPayUnifiedorderRespData {
    private String appid;
    private String timestamp;
    private String noncestr;
    @JSONField(name="package")
    private String package_;
    private String signtype;
    private String paysign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNoncestr() {
        return noncestr;
    }

    public void setNoncestr(String noncestr) {
        this.noncestr = noncestr;
    }

    public String getPackage_() {
        return package_;
    }

    public void setPackage_(String package_) {
        this.package_ = package_;
    }

    public String getSigntype() {
        return signtype;
    }

    public void setSigntype(String signtype) {
        this.signtype = signtype;
    }

    public String getPaysign() {
        return paysign;
    }

    public void setPaysign(String paysign) {
        this.paysign = paysign;
    }
}