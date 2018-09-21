package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * mpplus平台粉丝
 * Generated from seed-simcoder by 玄玉<https://jadyer.cn/> on 2017/09/10 21:44.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_mpp_fans_info")
public class MppFansInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 4354048541875327438L;
    /** 平台用户ID，对应t_mpp_user_info#id */
    private long uid;
    /** 微信原始ID */
    private String wxid;
    /** 粉丝的openid */
    private String openid;
    /** 粉丝的真实姓名 */
    private String name;
    /** 粉丝的身份证号 */
    @Column(name="id_card")
    private String idCard;
    /** 粉丝的手机号 */
    @Column(name="phone_no")
    private String phoneNo;
    /** 关注状态：0--未关注，其它为已关注 */
    private String subscribe;
    /** 粉丝的昵称 */
    private String nickname;
    /** 粉丝的性别：0--未知，1--男，2--女 */
    private int sex;
    /** 粉丝所在城市 */
    private String city;
    /** 粉丝所在国家 */
    private String country;
    /** 粉丝所在省份 */
    private String province;
    /** 粉丝的语言，简体中文为zh_CN */
    private String language;
    /** 粉丝的头像，值为腾讯服务器的图片URL */
    private String headimgurl;
    /** 粉丝最后一次关注的时间，格式为yyyy-MM-dd HH:mm:ss */
    @Column(name="subscribe_time")
    private String subscribeTime;
    /** 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段 */
    private String unionid;
    /** 公众号运营者对粉丝的备注 */
    private String remark;
    /** 粉丝用户所在的分组ID */
    private String groupid;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getWxid() {
        return wxid;
    }

    public void setWxid(String wxid) {
        this.wxid = wxid;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(String subscribe) {
        this.subscribe = subscribe;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getSubscribeTime() {
        return subscribeTime;
    }

    public void setSubscribeTime(String subscribeTime) {
        this.subscribeTime = subscribeTime;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }
}