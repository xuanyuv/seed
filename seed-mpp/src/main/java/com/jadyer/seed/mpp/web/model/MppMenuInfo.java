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
@Table(name="t_mpp_menu_info")
public class MppMenuInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = -9022840044287534961L;
    private String pid;
    private long uid;
    private int level;
    private int type;
    private String name;
    @Column(name="view_url")
    private String viewURL;
    @Column(name="reply_id")
    private String replyId;
    @Column(name="menu_json")
    private String menuJson;

    public String getPid() {
        return pid;
    }
    public void setPid(String pid) {
        this.pid = pid;
    }
    public long getUid() {
        return uid;
    }
    public void setUid(long uid) {
        this.uid = uid;
    }
    public int getLevel() {
        return level;
    }
    public void setLevel(int level) {
        this.level = level;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getViewURL() {
        return viewURL;
    }
    public void setViewURL(String viewURL) {
        this.viewURL = viewURL;
    }
    public String getReplyId() {
        return replyId;
    }
    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }
    public String getMenuJson() {
        return menuJson;
    }
    public void setMenuJson(String menuJson) {
        this.menuJson = menuJson;
    }
}