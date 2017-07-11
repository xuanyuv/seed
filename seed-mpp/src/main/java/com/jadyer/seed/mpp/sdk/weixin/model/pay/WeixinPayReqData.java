package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--接口入参基类
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/10 16:11.
 */
public abstract class WeixinPayReqData {
    /** 微信支付分配的公众账号ID（企业号corpid即为此appId） */
    private String appid;

    /** 微信支付分配的商户号 */
    private String mch_id;

    /** 随机字符串，长度要求在32位以内。推荐随机数生成算法：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3 */
    private String nonce_str;

    /** 签名类型，默认为MD5，支持HMAC-SHA256和MD5 */
    private String sign_type;

    /**
     * 签名，详见签名生成算法：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_3
     * <p>
     *     本参数不需要传
     * </p>
     */
    private String sign;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getMch_id() {
        return mch_id;
    }

    public void setMch_id(String mch_id) {
        this.mch_id = mch_id;
    }

    public String getNonce_str() {
        return nonce_str;
    }

    public void setNonce_str(String nonce_str) {
        this.nonce_str = nonce_str;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}