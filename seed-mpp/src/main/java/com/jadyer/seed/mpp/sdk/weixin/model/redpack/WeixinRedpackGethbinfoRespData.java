package com.jadyer.seed.mpp.sdk.weixin.model.redpack;

import java.util.ArrayList;
import java.util.List;

/**
 * 微信红包--查询红包记录--接口出参
 * Created by 玄玉<http://jadyer.cn/> on 2017/7/28 10:54.
 */
public class WeixinRedpackGethbinfoRespData extends WeixinRedpackRespData {
    /** 商户使用查询API填写的商户单号的原路返回 */
    private String mch_billno;

    /** 使用API发放现金红包时返回的红包单号 */
    private String detail_id;

    /**
     * 红包状态
     * SENDING:发放中
     * SENT:已发放待领取
     * FAILED：发放失败
     * RECEIVED:已领取
     * RFUND_ING:退款中
     * REFUND:已退款
     */
    private String status;

    /**
     * 发放类型
     * API:通过API接口发放
     * UPLOAD:通过上传文件方式发放
     * ACTIVITY:通过活动方式发放
     */
    private String send_type;

    /**
     * 红包类型
     * GROUP:裂变红包
     * NORMAL:普通红包
     */
    private String hb_type;

    /** 红包个数 */
    private int total_num;

    /** 红包总金额（单位分） */
    private int total_amount;

    /** 发送失败原因 */
    private String reason;

    /** 红包发送时间，格式：2015-04-21 20:00:00 */
    private String send_time;

    /** 红包的退款时间（如果其未领取的退款），格式：2015-04-21 23:03:00） */
    private String refund_time;

    /** 红包退款金额 */
    private int refund_amount;

    /** 祝福语 */
    private String wishing;

    /** 活动描述，低版本微信可见 */
    private String remark;

    /** 发红包的活动名称 */
    private String act_name;

    /** 裂变红包的领取列表 */
    private List<Hbinfo> hbinfolist = new ArrayList<>();

    /**
     * 裂变红包信息
     */
    public static class Hbinfo{
        /** 领取红包的openid */
        private String openid;

        /** 领取金额 */
        private int amount;

        /** 领取红包的时间，格式：2015-04-21 20:00:00 */
        private String rcv_time;

        public String getOpenid() {
            return openid;
        }

        public void setOpenid(String openid) {
            this.openid = openid;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public String getRcv_time() {
            return rcv_time;
        }

        public void setRcv_time(String rcv_time) {
            this.rcv_time = rcv_time;
        }
    }

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(String detail_id) {
        this.detail_id = detail_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSend_type() {
        return send_type;
    }

    public void setSend_type(String send_type) {
        this.send_type = send_type;
    }

    public String getHb_type() {
        return hb_type;
    }

    public void setHb_type(String hb_type) {
        this.hb_type = hb_type;
    }

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getSend_time() {
        return send_time;
    }

    public void setSend_time(String send_time) {
        this.send_time = send_time;
    }

    public String getRefund_time() {
        return refund_time;
    }

    public void setRefund_time(String refund_time) {
        this.refund_time = refund_time;
    }

    public int getRefund_amount() {
        return refund_amount;
    }

    public void setRefund_amount(int refund_amount) {
        this.refund_amount = refund_amount;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public List<Hbinfo> getHbinfolist() {
        return hbinfolist;
    }

    public void setHbinfolist(List<Hbinfo> hbinfolist) {
        this.hbinfolist = hbinfolist;
    }
}