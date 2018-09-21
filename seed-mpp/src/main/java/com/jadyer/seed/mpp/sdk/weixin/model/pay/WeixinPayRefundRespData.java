package com.jadyer.seed.mpp.sdk.weixin.model.pay;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信支付--公众号支付--申请退款接口出参
 * <p>
 *     注意：退款申请接收成功时，本实体类有值，但退款结果需要调用退款查询接口查询
 * </p>
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/10 15:52.
 */
public class WeixinPayRefundRespData extends WeixinPayRespData {
    /** 微信订单号 */
    private String transaction_id;

    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一 */
    private String out_trade_no;

    /** 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔 */
    private String out_refund_no;

    /** 微信退款单号 */
    private String refund_id;

    /** 退款总金额，单位为分，可以做部分退款 */
    private String refund_fee;

    /** 应结退款金额。即去掉非充值代金券退款金额后的退款金额，退款金额 = 申请退款金额 - 非充值代金券退款金额，退款金额 <= 申请退款金额 */
    private String settlement_refund_fee;

    /** 标价金额。即订单总金额，单位为分，只能为整数，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String total_fee;

    /** 应结订单金额。即去掉非充值代金券金额后的订单总金额，应结订单金额 = 订单金额 - 非充值代金券金额，应结订单金额 <= 订单金额 */
    private String settlement_total_fee;

    /** 标价币种。即订单金额货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String fee_type;

    /** 现金支付金额，单位为分，只能为整数，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String cash_fee;

    /** 现金支付币种。货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String cash_fee_type;

    /** 现金退款金额，单位为分，只能为整数，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String cash_refund_fee;

    /** 代金券退款总金额。代金券退款金额 <= 退款金额，退款金额 - 代金券或立减优惠退款金额为现金，说明详见代金券或立减优惠：https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_1 */
    private String coupon_refund_fee;

    /** 退款代金券使用数量 */
    private String coupon_refund_count;

    /** 退款代金券信息列表 */
    private List<CouponInfo> couponInfoList = new ArrayList<>();

    /**
     * 退款代金券信息
     */
    public static class CouponInfo{
        /**
         * 代金券类型
         * CASH-----充值代金券
         * NO_CASH--非充值优惠券
         * 订单使用代金券时有返回（取值：CASH、NO_CASH）
         */
        private String coupon_type;

        /** 退款代金券ID */
        private String coupon_refund_id;

        /** 单个代金券退款金额。代金券退款金额 <= 退款金额，退款金额 - 代金券或立减优惠退款金额为现金，说明详见代金券或立减优惠：https://pay.weixin.qq.com/wiki/doc/api/tools/sp_coupon.php?chapter=12_1 */
        private String coupon_refund_fee;

        public String getCoupon_type() {
            return coupon_type;
        }

        public void setCoupon_type(String coupon_type) {
            this.coupon_type = coupon_type;
        }

        public String getCoupon_refund_id() {
            return coupon_refund_id;
        }

        public void setCoupon_refund_id(String coupon_refund_id) {
            this.coupon_refund_id = coupon_refund_id;
        }

        public String getCoupon_refund_fee() {
            return coupon_refund_fee;
        }

        public void setCoupon_refund_fee(String coupon_refund_fee) {
            this.coupon_refund_fee = coupon_refund_fee;
        }
    }

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

    public String getRefund_fee() {
        return refund_fee;
    }

    public void setRefund_fee(String refund_fee) {
        this.refund_fee = refund_fee;
    }

    public String getSettlement_refund_fee() {
        return settlement_refund_fee;
    }

    public void setSettlement_refund_fee(String settlement_refund_fee) {
        this.settlement_refund_fee = settlement_refund_fee;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getSettlement_total_fee() {
        return settlement_total_fee;
    }

    public void setSettlement_total_fee(String settlement_total_fee) {
        this.settlement_total_fee = settlement_total_fee;
    }

    public String getFee_type() {
        return fee_type;
    }

    public void setFee_type(String fee_type) {
        this.fee_type = fee_type;
    }

    public String getCash_fee() {
        return cash_fee;
    }

    public void setCash_fee(String cash_fee) {
        this.cash_fee = cash_fee;
    }

    public String getCash_fee_type() {
        return cash_fee_type;
    }

    public void setCash_fee_type(String cash_fee_type) {
        this.cash_fee_type = cash_fee_type;
    }

    public String getCash_refund_fee() {
        return cash_refund_fee;
    }

    public void setCash_refund_fee(String cash_refund_fee) {
        this.cash_refund_fee = cash_refund_fee;
    }

    public String getCoupon_refund_fee() {
        return coupon_refund_fee;
    }

    public void setCoupon_refund_fee(String coupon_refund_fee) {
        this.coupon_refund_fee = coupon_refund_fee;
    }

    public String getCoupon_refund_count() {
        return coupon_refund_count;
    }

    public void setCoupon_refund_count(String coupon_refund_count) {
        this.coupon_refund_count = coupon_refund_count;
    }

    public List<CouponInfo> getCouponInfoList() {
        return couponInfoList;
    }

    public void setCouponInfoList(List<CouponInfo> couponInfoList) {
        this.couponInfoList = couponInfoList;
    }
}