package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * mpplus平台用户
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/09/10 21:44.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_mpp_user_info")
public class MppUserInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 4261762103844791566L;
    /** 平台用户所属上一级ID */
    private long pid;
    /** 用户名 */
    private String username;
    /** 登录密码 */
    private String password;
    /** 用户唯一标识，用来生成微信或QQ公众平台Token */
    private String uuid;
    /** 公众平台类型：0--未知，1--微信，2--QQ */
    private int mptype;
    /** 微信或QQ公众平台原始ID */
    private String mpid;
    /** 微信或QQ公众平台号 */
    private String mpno;
    /** 微信或QQ公众平台名称 */
    private String mpname;
    /** 微信或QQ公众平台appid */
    private String appid;
    /** 微信或QQ公众平台appsecret */
    private String appsecret;
    /** 微信或QQ公众平台商户号 */
    private String mchid;
    /** 微信或QQ公众平台商户Key */
    private String mchkey;
    /** 微信或QQ公众平台绑定状态：0--未绑定，1--已绑定 */
    @Column(name="bind_status")
    private int bindStatus;
    /** 微信或QQ公众平台绑定解绑时间 */
    @Column(name="bind_time")
    private Date bindTime;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getMptype() {
        return mptype;
    }

    public void setMptype(int mptype) {
        this.mptype = mptype;
    }

    public String getMpid() {
        return mpid;
    }

    public void setMpid(String mpid) {
        this.mpid = mpid;
    }

    public String getMpno() {
        return mpno;
    }

    public void setMpno(String mpno) {
        this.mpno = mpno;
    }

    public String getMpname() {
        return mpname;
    }

    public void setMpname(String mpname) {
        this.mpname = mpname;
    }

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getAppsecret() {
        return appsecret;
    }

    public void setAppsecret(String appsecret) {
        this.appsecret = appsecret;
    }

    public String getMchid() {
        return mchid;
    }

    public void setMchid(String mchid) {
        this.mchid = mchid;
    }

    public String getMchkey() {
        return mchkey;
    }

    public void setMchkey(String mchkey) {
        this.mchkey = mchkey;
    }

    public int getBindStatus() {
        return bindStatus;
    }

    public void setBindStatus(int bindStatus) {
        this.bindStatus = bindStatus;
    }

    public Date getBindTime() {
        return bindTime;
    }

    public void setBindTime(Date bindTime) {
        this.bindTime = bindTime;
    }
}