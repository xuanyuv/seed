package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--关闭订单接口入参
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_3
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/10 19:47.
 */
public class WeixinPayCloseorderReqData extends WeixinPayReqData {
    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一 */
    private String out_trade_no;

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
}