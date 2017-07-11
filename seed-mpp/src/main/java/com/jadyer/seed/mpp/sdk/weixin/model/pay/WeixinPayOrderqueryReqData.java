package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--查询订单接口入参
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_2
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/10 11:22.
 */
public class WeixinPayOrderqueryReqData extends WeixinPayReqData {
    /** 微信的订单号，建议优先使用 */
    private String transaction_id;

    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一。 详见商户订单号：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String out_trade_no;

    public String getTransaction_id() {
        return transaction_id;
    }

    public void setTransaction_id(String transaction_id) {
        this.transaction_id = transaction_id;
    }

    public String getOut_trade_no() {
        return out_trade_no;
    }

    public void setOut_trade_no(String out_trade_no) {
        this.out_trade_no = out_trade_no;
    }
}