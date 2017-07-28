package com.jadyer.seed.mpp.sdk.weixin.model.redpack;

/**
 * 微信红包--接口入参基类
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/28 10:23.
 */
public abstract class WeixinRedpackReqData {
    /** 微信支付分配的商户号 */
    private String mch_id;

    /** 随机字符串，不长于32位 */
    private String nonce_str;

    /**
     * 签名，详见签名生成算法：https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=4_3
     * <p>
     *     本参数不需要传
     * </p>
     */
    private String sign;
}