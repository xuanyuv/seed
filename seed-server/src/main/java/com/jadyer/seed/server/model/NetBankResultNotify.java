package com.jadyer.seed.server.model;

/**
 * 对应支付处理系统的网银结果通知接口
 * Created by 玄玉<https://jadyer.github.io/> on 2012/12/25 22:55.
 */
public class NetBankResultNotify {
	private String orderNo;       //订单号：即网银网关流水号，对银行订单
	private String tradeResult;   //交易结果：即银行处理结果标志，1--成功，0--失败
	private String bankDate;      //收单机构处理日期：yyyyMMdd
	private String bankSerialNo;  //收单机构流水号
	private String bankRespCode;  //收单机构响应码
	private String bankRespDesc;  //收单机构响应描述
	private String bankAccountNo; //银行帐户帐号
	private String bankCertifi;   //收单机构证书（HEX描述）
	private String bankSignData;  //收单机构签名
	private String notifyType;    //通知类型，1--页面重定向应答，2--点对点应答，3--手工应答，4--系统补单，5--交易前置补单
	private String tradeAmount;   //交易金额，单位:分
	
	public String getOrderNo() {
		return orderNo==null ? "" : orderNo;
	}
	public String getTradeResult() {
		return tradeResult==null ? "" : tradeResult;
	}
	public String getBankDate() {
		return bankDate==null ? "" : bankDate;
	}
	public String getBankSerialNo() {
		return bankSerialNo==null ? "" : bankSerialNo;
	}
	public String getBankRespCode() {
		return bankRespCode==null ? "" : bankRespCode;
	}
	public String getBankRespDesc() {
		return bankRespDesc==null ? "" : bankRespDesc;
	}
	public String getBankAccountNo() {
		return bankAccountNo==null ? "" : bankAccountNo;
	}
	public String getBankCertifi() {
		return bankCertifi==null ? "" : bankCertifi;
	}
	public String getBankSignData() {
		return bankSignData==null ? "" : bankSignData;
	}
	public String getNotifyType() {
		return notifyType==null ? "" : notifyType;
	}
	public String getTradeAmount() {
		return tradeAmount==null ? "" : tradeAmount;
	}
	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}
	public void setTradeResult(String tradeResult) {
		this.tradeResult = tradeResult;
	}
	public void setBankDate(String bankDate) {
		this.bankDate = bankDate;
	}
	public void setBankSerialNo(String bankSerialNo) {
		this.bankSerialNo = bankSerialNo;
	}
	public void setBankRespCode(String bankRespCode) {
		this.bankRespCode = bankRespCode;
	}
	public void setBankRespDesc(String bankRespDesc) {
		this.bankRespDesc = bankRespDesc;
	}
	public void setBankAccountNo(String bankAccountNo) {
		this.bankAccountNo = bankAccountNo;
	}
	public void setBankCertifi(String bankCertifi) {
		this.bankCertifi = bankCertifi;
	}
	public void setBankSignData(String bankSignData) {
		this.bankSignData = bankSignData;
	}
	public void setNotifyType(String notifyType) {
		this.notifyType = notifyType;
	}
	public void setTradeAmount(String tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
}