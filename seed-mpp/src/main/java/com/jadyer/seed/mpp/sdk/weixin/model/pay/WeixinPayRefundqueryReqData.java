package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--查询退款接口入参
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_5
 * <p>
 *     查询的优先级是： refund_id > out_refund_no > transaction_id > out_trade_no
 * </p>
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/10 16:43.
 */
public class WeixinPayRefundqueryReqData extends WeixinPayReqData {
    /** 微信订单号 */
    private String transaction_id;

    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一 */
    private String out_trade_no;

    /** 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔 */
    private String out_refund_no;

    /** 微信生成的退款单号，在申请退款接口有返回 */
    private String refund_id;

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

    public String getOut_refund_no() {
        return out_refund_no;
    }

    public void setOut_refund_no(String out_refund_no) {
        this.out_refund_no = out_refund_no;
    }

    public String getRefund_id() {
        return refund_id;
    }

    public void setRefund_id(String refund_id) {
        this.refund_id = refund_id;
    }
}