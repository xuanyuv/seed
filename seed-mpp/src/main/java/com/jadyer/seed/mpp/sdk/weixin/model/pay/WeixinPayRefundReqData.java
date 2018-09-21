package com.jadyer.seed.mpp.sdk.weixin.model.pay;

/**
 * 微信支付--公众号支付--申请退款接口入参
 * https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=9_4
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/10 15:36.
 */
public class WeixinPayRefundReqData extends WeixinPayReqData {
    /** 微信生成的订单号，在支付通知中有返回 */
    private String transaction_id;

    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一 */
    private String out_trade_no;

    /** 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔 */
    private String out_refund_no;

    /** 订单总金额，单位为分，只能为整数，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String total_fee;

    /** 退款总金额，单位为分，只能为整数，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String refund_fee;

    /** 货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String refund_fee_type;

    /** 退款原因。若商户传入，会在下发给用户的退款消息中体现退款原因 */
    private String refund_desc;

    /**
     * 退款资金来源（仅针对老资金流商户使用）
     * REFUND_SOURCE_UNSETTLED_FUNDS--未结算资金退款（默认使用未结算资金退款）
     * REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款
     */
    private String refund_account;

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

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getRefund_fee_type() {
        return refund_fee_type;
    }

    public void setRefund_fee_type(String refund_fee_type) {
        this.refund_fee_type = refund_fee_type;
    }

    public String getRefund_desc() {
        return refund_desc;
    }

    public void setRefund_desc(String refund_desc) {
        this.refund_desc = refund_desc;
    }

    public String getRefund_account() {
        return refund_account;
    }

    public void setRefund_account(String refund_account) {
        this.refund_account = refund_account;
    }
}