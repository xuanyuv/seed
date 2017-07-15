package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_mpp_reply_info")
public class MppReplyInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = -9162851555238487580L;
    private long uid;
    private int category;
    private int type;
    private String keyword;
    private String content;
    @Column(name="plugin_id")
    private String pluginId;

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
    public String getPluginId() {
        return pluginId;
    }
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }
}