package com.jadyer.seed.mpp.sdk.weixin.model.redpack;

/**
 * 微信红包--发放普通红包--接口入参
 * https://pay.weixin.qq.com/wiki/doc/api/tools/cash_coupon.php?chapter=13_4&index=3
 * Created by 玄玉<https://jadyer.cn/> on 2017/7/28 10:26.
 */
public class WeixinRedpackSendReqData extends WeixinRedpackReqData {
    /** 微信分配的公众账号ID（企业号corpid即为此appId）。接口传入的所有appid应该为公众号的appid（在mp.weixin.qq.com申请的），不能为APP的appid（在open.weixin.qq.com申请的）。 */
    private String wxappid;

    /** 商户订单号（每个订单号必须唯一。取值范围：0~9，a~z，A~Z）。接口根据商户订单号支持重入，如出现超时可再调用。 */
    private String mch_billno;

    /** 商户名称，即红包发送者名称（最长32个字符） */
    private String send_name;

    /** 接受红包的用户在wxappid下的openid */
    private String re_openid;

    /** 付款金额，单位：分 */
    private int total_amount;

    /** 红包发放总人数 */
    private int total_num;

    /** 红包祝福语（最长128个字符） */
    private String wishing;

    /** 调用接口的机器Ip地址 */
    private String client_ip;

    /**
     * 活动名称（最长32个字符）
     * <p>
     *     注意：实际测试发现该值必传，否则会提示“参数错误:act_name字段必填,并且少于32个字符.”（即便是通过API发送红包）
     * </p>
     */
    private String act_name;

    /** 备注（最长256个字符） */
    private String remark;

    /**
     * 场景id（发放红包使用场景，红包金额大于200时必传）
     * PRODUCT_1:商品促销
     * PRODUCT_2:抽奖
     * PRODUCT_3:虚拟物品兑奖
     * PRODUCT_4:企业内部福利
     * PRODUCT_5:渠道分润
     * PRODUCT_6:保险回馈
     * PRODUCT_7:彩票派奖
     * PRODUCT_8:税务刮奖
     */
    private String scene_id;

    /**
     * 活动信息（把值为非空的信息用key=value进行拼接，再进行urlencode(posttime=xx&mobile=xx&deviceid=xx)）
     * posttime:用户操作的时间戳
     * mobile:业务系统账号的手机号，国家代码-手机号。不需要+号
     * deviceid :mac 地址或者设备唯一标识
     * clientversion :用户操作的客户端版本
     */
    private String risk_info;

    /** 资金授权商户号（服务商替特约商户发放时使用） */
    private String consume_mch_id;

    public String getWxappid() {
        return wxappid;
    }

    public void setWxappid(String wxappid) {
        this.wxappid = wxappid;
    }

    public String getMch_billno() {
        return mch_billno;
    }

    public void setMch_billno(String mch_billno) {
        this.mch_billno = mch_billno;
    }

    public String getSend_name() {
        return send_name;
    }

    public void setSend_name(String send_name) {
        this.send_name = send_name;
    }

    public String getRe_openid() {
        return re_openid;
    }

    public void setRe_openid(String re_openid) {
        this.re_openid = re_openid;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public int getTotal_num() {
        return total_num;
    }

    public void setTotal_num(int total_num) {
        this.total_num = total_num;
    }

    public String getWishing() {
        return wishing;
    }

    public void setWishing(String wishing) {
        this.wishing = wishing;
    }

    public String getClient_ip() {
        return client_ip;
    }

    public void setClient_ip(String client_ip) {
        this.client_ip = client_ip;
    }

    public String getAct_name() {
        return act_name;
    }

    public void setAct_name(String act_name) {
        this.act_name = act_name;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getScene_id() {
        return scene_id;
    }

    public void setScene_id(String scene_id) {
        this.scene_id = scene_id;
    }

    public String getRisk_info() {
        return risk_info;
    }

    public void setRisk_info(String risk_info) {
        this.risk_info = risk_info;
    }

    public String getConsume_mch_id() {
        return consume_mch_id;
    }

    public void setConsume_mch_id(String consume_mch_id) {
        this.consume_mch_id = consume_mch_id;
    }
}