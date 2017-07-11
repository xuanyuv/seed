package com.jadyer.seed.mpp.sdk.weixin.model.pay;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信支付--公众号支付--查询订单接口出参
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/10 11:26.
 */
public class WeixinPayOrderqueryRespData extends WeixinPayRespData {
    /** 微信支付分配的终端设备号 */
    private String device_info;

    /** 用户在商户appid下的唯一标识 */
    private String openid;

    /** 用户是否关注公众账号，Y-关注，N-未关注，仅在公众账号类型支付有效 */
    private String is_subscribe;

    /** 调用接口提交的交易类型，取值如下：JSAPI，NATIVE，APP，MICROPAY，详细说明见参数规定：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String trade_type;

    /**
     * 交易状态
     * SUCCESS-----支付成功
     * REFUND------转入退款
     * NOTPAY------未支付
     * CLOSED------已关闭
     * REVOKED-----已撤销（刷卡支付）
     * USERPAYING--用户支付中
     * PAYERROR----支付失败（其他原因，如银行返回失败）
     */
    private String trade_state;

    /** 付款银行，即银行类型，采用字符串类型的银行标识 */
    private String bank_type;

    /** 标价金额。即订单总金额，单位为分 */
    private String total_fee;

    /** 应结订单金额。当订单使用了免充值型优惠券后返回该参数，应结订单金额 = 订单金额 - 免充值优惠券金额 */
    private String settlement_total_fee;

    /** 标价币种。即货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String fee_type;

    /** 现金支付金额，即订单现金支付金额，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String cash_fee;

    /** 现金支付币种，即货币类型，符合ISO 4217标准的三位字母代码，默认人民币：CNY，其他值列表详见货币类型：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String cash_fee_type;

    /** 代金券金额。代金券金额 <= 订单金额，订单金额 - 代金券金额 = 现金支付金额，详见支付金额：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String coupon_fee;

    /** 代金券使用数量 */
    private String coupon_count;

    /** 微信支付订单号 */
    private String transaction_id;

    /** 商户系统内部订单号，要求32个字符内，只能是数字、大小写字母_-|*@ ，且在同一个商户号下唯一 */
    private String out_trade_no;

    /** 附加数据，原样返回 */
    private String attach;

    /** 支付完成时间，即订单支付时间，格式为yyyyMMddHHmmss，如2009年12月25日9点10分10秒表示为20091225091010。其他详见时间规则：https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_2 */
    private String time_end;

    /** 交易状态描述（对当前查询订单状态的描述和下一步操作的指引） */
    private String trade_state_desc;

    /** 代金券信息列表 */
    private List<CouponInfo> couponInfoList = new ArrayList<>();

    /**
     * 代金券信息
     */
    public static class CouponInfo{
        /**
         * 代金券类型
         * CASH-----充值代金券
         * NO_CASH--非充值优惠券
         * 开通免充值券功能，并且订单使用了优惠券后有返回（取值：CASH、NO_CASH）
         */
        private String coupon_type;

        /** 代金券ID */
        private String coupon_id;

        /** 单个代金券支付金额 */
        private String coupon_fee;

        public String getCoupon_type() {
            return coupon_type;
        }

        public void setCoupon_type(String coupon_type) {
            this.coupon_type = coupon_type;
        }

        public String getCoupon_id() {
            return coupon_id;
        }

        public void setCoupon_id(String coupon_id) {
            this.coupon_id = coupon_id;
        }

        public String getCoupon_fee() {
            return coupon_fee;
        }

        public void setCoupon_fee(String coupon_fee) {
            this.coupon_fee = coupon_fee;
        }
    }

    public String getDevice_info() {
        return device_info;
    }

    public void setDevice_info(String device_info) {
        this.device_info = device_info;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getIs_subscribe() {
        return is_subscribe;
    }

    public void setIs_subscribe(String is_subscribe) {
        this.is_subscribe = is_subscribe;
    }

    public String getTrade_type() {
        return trade_type;
    }

    public void setTrade_type(String trade_type) {
        this.trade_type = trade_type;
    }

    public String getTrade_state() {
        return trade_state;
    }

    public void setTrade_state(String trade_state) {
        this.trade_state = trade_state;
    }

    public String getBank_type() {
        return bank_type;
    }

    public void setBank_type(String bank_type) {
        this.bank_type = bank_type;
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

    public String getCoupon_fee() {
        return coupon_fee;
    }

    public void setCoupon_fee(String coupon_fee) {
        this.coupon_fee = coupon_fee;
    }

    public String getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(String coupon_count) {
        this.coupon_count = coupon_count;
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

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getTrade_state_desc() {
        return trade_state_desc;
    }

    public void setTrade_state_desc(String trade_state_desc) {
        this.trade_state_desc = trade_state_desc;
    }

    public List<CouponInfo> getCouponInfoList() {
        return couponInfoList;
    }

    public void setCouponInfoList(List<CouponInfo> couponInfoList) {
        this.couponInfoList = couponInfoList;
    }
}