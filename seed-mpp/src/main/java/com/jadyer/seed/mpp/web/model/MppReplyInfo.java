package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * mpplus平台回复设置
 * Generated from seed-simcoder by 玄玉<https://jadyer.cn/> on 2017/09/10 21:44.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_mpp_reply_info")
public class MppReplyInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 6572341374195211101L;
    /** 平台用户ID，对应t_mpp_user_info#id */
    private long uid;
    /** 回复的类别：0--通用的回复，1--关注后回复，2--关键字回复 */
    private int category;
    /** 回复的类型：0--文本，1--图文，2--图片，3--活动，4--转发到多客服 */
    private int type;
    /** 关键字 */
    private String keyword;
    /** 回复的内容 */
    private String content;
    /** 活动插件ID，对应t_plugin#id */
    @Column(name="plugin_id")
    private long pluginId;

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getPluginId() {
        return pluginId;
    }

    public void setPluginId(long pluginId) {
        this.pluginId = pluginId;
    }
}