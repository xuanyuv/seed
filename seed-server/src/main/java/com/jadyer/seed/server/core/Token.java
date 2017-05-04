package com.jadyer.seed.server.core;

/**
 * 封装客户端请求报文
 * Created by 玄玉<http://jadyer.cn/> on 2012/12/23 20:35.
 */
class Token {
    static final String BUSI_TYPE_TCP = "TCP";
    static final String BUSI_TYPE_HTTP = "HTTP";
    private String busiCode;    //业务码
    private String busiType;    //业务类型：TCP or HTTP
    private String busiMessage; //业务报文：TCP请求时为TCP完整报文，HTTP_POST请求时为报文体部分，HTTP_GET时为报文头第一行参数部分
    private String busiCharset; //报文字符集
    private String fullMessage; //完整报文（用于在日志中打印完整报文）

    String getBusiCode() {
        return busiCode;
    }
    String getBusiType() {
        return busiType;
    }
    String getBusiMessage() {
        return busiMessage;
    }
    String getBusiCharset() {
        return busiCharset;
    }
    String getFullMessage() {
        return fullMessage;
    }
    void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }
    void setBusiType(String busiType) {
        this.busiType = busiType;
    }
    void setBusiMessage(String busiMessage) {
        this.busiMessage = busiMessage;
    }
    void setBusiCharset(String busiCharset) {
        this.busiCharset = busiCharset;
    }
    void setFullMessage(String fullMessage) {
        this.fullMessage = fullMessage;
    }
}