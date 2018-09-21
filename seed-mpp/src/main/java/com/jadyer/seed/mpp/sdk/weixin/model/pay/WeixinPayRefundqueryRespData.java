package com.jadyer.seed.mpp.sdk.weixin.model.pay;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信支付--公众号支付--查询退款接口出参
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/10 16:44.
 */
public class WeixinPayRefundqueryRespData extends WeixinPayRespData {
    /** 微信订单号 */
    private String transaction_id;

    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一 */
    private String out_trade_no;

    /** 订单总金额，单位为分，只能为整数，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String total_fee;

    /** 应结订单金额。当订单使用了免充值型优惠券后返回该参数，应结订单金额 = 订单金额 - 免充值优惠券金额 */
    private String settlement_total_fee;

    /** 订单金额货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String fee_type;

    /** 现金支付金额，单位为分，只能为整数，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String cash_fee;

    /** 退款笔数 */
    private String refund_count;

    /** 退款信息列表 */
    private List<RefundInfo> refundInfoList = new ArrayList<>();

    /**
     * 退款信息
     */
    public static class RefundInfo{
        /** 商户系统内部的退款单号，商户系统内部唯一，只能是数字、大小写字母_-|*@ ，同一退款单号多次请求只退一笔 */
        private String out_refund_no;

        /** 微信退款单号 */
        private String refund_id;

        /**
         * 退款渠道
         * ORIGINAL--------原路退款
         * BALANCE---------退回到余额
         * OTHER_BALANCE---原账户异常退到其他余额账户
         * OTHER_BANKCARD--原银行卡异常退到其他银行卡
         */
        private String refund_channel;

        /** 申请退款金额。也即退款总金额，单位为分，可以做部分退款 */
        private String refund_fee;

        /** 退款金额。退款金额 = 申请退款金额 - 非充值代金券退款金额，退款金额 <= 申请退款金额 */
        private String settlement_refund_fee;

        /**
         * 代金券类型
         * CASH-----充值代金券
         * NO_CASH--非充值优惠券
         * 开通免充值券功能，并且订单使用了优惠券后有返回（取值：CASH、NO_CASH）
         */
        private String coupon_type;

        /** 总代金券退款金额。代金券退款金额 <= 退款金额，退款金额 - 代金券或立减优惠退款金额为现金，说明详见代金券或立减优惠：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=12_1 */
        private String coupon_refund_fee;

        /**
         * 退款状态
         * SUCCESS------退款成功
         * REFUNDCLOSE--退款关闭
         * PROCESSING---退款处理中
         * CHANGE-------退款异常，退款到银行发现用户的卡作废或者冻结了，导致原路退款银行卡失败，可前往商户平台（pay.weixin.qq.com）-交易中心，手动处理此笔退款
         */
        private String refund_status;

        /**
         * 退款资金来源
         * REFUND_SOURCE_RECHARGE_FUNDS---可用余额退款/基本账户
         * REFUND_SOURCE_UNSETTLED_FUNDS--未结算资金退款
         */
        private String refund_account;

        /**
         * 退款入账账户
         * <ul>
         *     取当前退款单的退款入账方
         *     <li>1）退回银行卡：{银行名称}{卡类型}{卡尾号}</li>
         *     <li>2）退回支付用户零钱：支付用户零钱</li>
         *     <li>3）退还商户：商户基本账户、商户结算银行账户</li>
         * </ul>
         */
        private String refund_recv_accout;

        /** 退款成功时间，当退款状态为退款成功时有返回 */
        private String refund_success_time;

        /** 退款代金券使用数量 */
        private String coupon_refund_count;

        /** 退款代金券信息列表 */
        private List<CouponRefundInfo> couponRefundInfoList = new ArrayList<>();

        /**
         * 退款代金券信息
         */
        public static class CouponRefundInfo{
            /** 退款代金券ID */
            private String coupon_refund_id;

            /** 单个代金券退款金额	，即单个退款代金券支付金额 */
            private String coupon_refund_fee;

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

        public String getRefund_channel() {
            return refund_channel;
        }

        public void setRefund_channel(String refund_channel) {
            this.refund_channel = refund_channel;
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

        public String getCoupon_type() {
            return coupon_type;
        }

        public void setCoupon_type(String coupon_type) {
            this.coupon_type = coupon_type;
        }

        public String getCoupon_refund_fee() {
            return coupon_refund_fee;
        }

        public void setCoupon_refund_fee(String coupon_refund_fee) {
            this.coupon_refund_fee = coupon_refund_fee;
        }

        public String getRefund_status() {
            return refund_status;
        }

        public void setRefund_status(String refund_status) {
            this.refund_status = refund_status;
        }

        public String getRefund_account() {
            return refund_account;
        }

        public void setRefund_account(String refund_account) {
            this.refund_account = refund_account;
        }

        public String getRefund_recv_accout() {
            return refund_recv_accout;
        }

        public void setRefund_recv_accout(String refund_recv_accout) {
            this.refund_recv_accout = refund_recv_accout;
        }

        public String getRefund_success_time() {
            return refund_success_time;
        }

        public void setRefund_success_time(String refund_success_time) {
            this.refund_success_time = refund_success_time;
        }

        public String getCoupon_refund_count() {
            return coupon_refund_count;
        }

        public void setCoupon_refund_count(String coupon_refund_count) {
            this.coupon_refund_count = coupon_refund_count;
        }

        public List<CouponRefundInfo> getCouponRefundInfoList() {
            return couponRefundInfoList;
        }

        public void setCouponRefundInfoList(List<CouponRefundInfo> couponRefundInfoList) {
            this.couponRefundInfoList = couponRefundInfoList;
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

    public String getRefund_count() {
        return refund_count;
    }

    public void setRefund_count(String refund_count) {
        this.refund_count = refund_count;
    }

    public List<RefundInfo> getRefundInfoList() {
        return refundInfoList;
    }

    public void setRefundInfoList(List<RefundInfo> refundInfoList) {
        this.refundInfoList = refundInfoList;
    }
}