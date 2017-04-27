package com.jadyer.seed.server.model;

/**
 * 对应沃前置系统的订单支付后台通知接口
 * Created by 玄玉<https://jadyer.github.io/> on 2012/12/25 22:10.
 */
public class OrderResultNotify {
    private String merNo;          //商户号
    private String goodsID;        //商品ID
    private String goodsName;      //商品名
    private String goodsDesc;      //商品描述
    private String merOrderNo;     //商户订单号
    private String orderAmount;    //订单金额，单位:分
    private String merUserID;      //商户用户ID，用户在商户系统的用户号
    private String phoneNo;        //手机号码
    private String orderDate;      //商户订单日期，商户产生yyyyMMdd
    private String orderExtend;    //订单扩展信息，商户自行填写
    private String charset;        //商户编码字符集
    private String merDate;        //商户时间，即商户发起请求的时间yyyyMMddHHmmss
    private String signType;       //签名方式，暂时固定为MD5
    private String payNo;          //支付号
    private String detailCount;    //明细数量
    private String payDetail;      //支付明细
    private String acceptTime;     //支付系统受理时间
    private String acountDate;     //会计日期，即账务日期yyyyMMdd
    private String payBankCode;    //合作银行号
    private String bankAcountName; //银行账户名
    private String bankAcountNo;   //银行账户账号

    public String getMerNo() {
        return merNo==null ? "" : merNo;
    }
    public String getGoodsID() {
        return goodsID==null ? "" : goodsID;
    }
    public String getGoodsName() {
        return goodsName==null ? "" : goodsName;
    }
    public String getGoodsDesc() {
        return goodsDesc==null ? "" : goodsDesc;
    }
    public String getMerOrderNo() {
        return merOrderNo==null ? "" : merOrderNo;
    }
    public String getOrderAmount() {
        return orderAmount==null ? "" : orderAmount;
    }
    public String getMerUserID() {
        return merUserID==null ? "" : merUserID;
    }
    public String getPhoneNo() {
        return phoneNo==null ? "" : phoneNo;
    }
    public String getOrderDate() {
        return orderDate==null ? "" : orderDate;
    }
    public String getOrderExtend() {
        return orderExtend==null ? "" : orderExtend;
    }
    public String getCharset() {
        return charset==null ? "" : charset;
    }
    public String getMerDate() {
        return merDate==null ? "" : merDate;
    }
    public String getSignType() {
        return signType==null ? "" : signType;
    }
    public String getPayNo() {
        return payNo==null ? "" : payNo;
    }
    public String getDetailCount() {
        return detailCount==null ? "" : detailCount;
    }
    public String getPayDetail() {
        return payDetail==null ? "" : payDetail;
    }
    public String getAcceptTime() {
        return acceptTime==null ? "" : acceptTime;
    }
    public String getAcountDate() {
        return acountDate==null ? "" : acountDate;
    }
    public String getPayBankCode() {
        return payBankCode==null ? "" : payBankCode;
    }
    public String getBankAcountName() {
        return bankAcountName==null ? "" : bankAcountName;
    }
    public String getBankAcountNo() {
        return bankAcountNo==null ? "" : bankAcountNo;
    }
    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }
    public void setGoodsID(String goodsID) {
        this.goodsID = goodsID;
    }
    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }
    public void setGoodsDesc(String goodsDesc) {
        this.goodsDesc = goodsDesc;
    }
    public void setMerOrderNo(String merOrderNo) {
        this.merOrderNo = merOrderNo;
    }
    public void setOrderAmount(String orderAmount) {
        this.orderAmount = orderAmount;
    }
    public void setMerUserID(String merUserID) {
        this.merUserID = merUserID;
    }
    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }
    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }
    public void setOrderExtend(String orderExtend) {
        this.orderExtend = orderExtend;
    }
    public void setCharset(String charset) {
        this.charset = charset;
    }
    public void setMerDate(String merDate) {
        this.merDate = merDate;
    }
    public void setSignType(String signType) {
        this.signType = signType;
    }
    public void setPayNo(String payNo) {
        this.payNo = payNo;
    }
    public void setDetailCount(String detailCount) {
        this.detailCount = detailCount;
    }
    public void setPayDetail(String payDetail) {
        this.payDetail = payDetail;
    }
    public void setAcceptTime(String acceptTime) {
        this.acceptTime = acceptTime;
    }
    public void setAcountDate(String acountDate) {
        this.acountDate = acountDate;
    }
    public void setPayBankCode(String payBankCode) {
        this.payBankCode = payBankCode;
    }
    public void setBankAcountName(String bankAcountName) {
        this.bankAcountName = bankAcountName;
    }
    public void setBankAcountNo(String bankAcountNo) {
        this.bankAcountNo = bankAcountNo;
    }
}