package com.jadyer.seed.mpp.sdk.qq.model;

/**
 * 封装获取到的QQ网页授权的网页access_token
 * @create Nov 28, 2015 8:36:15 PM
 * @author 玄玉<http://jadyer.cn/>
 */
public class QQOAuthAccessToken {
    /**
     * 网页授权接口调用凭证(此access_token与基础支持的access_token不同)
     */
    private String access_token;

    /**
     * 网页access_token接口调用凭证超时时间(单位为秒)
     */
    private String expires_in;

    /**
     * 用户刷新access_token
     */
    private String refresh_token;

    /**
     * 用户唯一标识(在未关注公众号时,用户访问公众号的网页,也会产生一个用户和公众号唯一的OpenID)
     */
    private String openid;

    /**
     * 用户授权的作用域(使用逗号,分隔)
     */
    private String scope;

    /**
     * 只有在用户将公众号绑定到QQ开放平台帐号后,才会出现该字段
     */
    private String unionid;

    /**
     * 获取网页授权access_token错误时QQ返回的应答码
     */
    private int errcode;

    /**
     * 获取网页授权access_token错误时QQ返回的应答描述
     */
    private String errmsg;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public int getErrcode() {
        return errcode;
    }

    public void setErrcode(int errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }
}