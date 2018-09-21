package com.jadyer.seed.mpp.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * mpplus平台自定义菜单
 * Generated from seed-simcoder by 玄玉<https://jadyer.cn/> on 2017/09/10 21:44.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_mpp_menu_info")
public class MppMenuInfo extends BaseEntity<Long> {
    private static final long serialVersionUID = 4356172549774220331L;
    /** 上一级菜单的ID，一级菜单情况下为0 */
    private long pid;
    /** 平台用户ID，对应t_mpp_user_info#id */
    private long uid;
    /** 菜单类型：1--CLICK，2--VIEW，3--JSON */
    private int type;
    /** 菜单级别：1--一级菜单，2--二级菜单 */
    private int level;
    /** 菜单名称 */
    private String name;
    /** type=2时用到 */
    @Column(name="view_url")
    private String viewUrl;
    /** type=1时用到，对应t_reply_info#id */
    @Column(name="reply_id")
    private long replyId;
    /** 微信或QQ公众号菜单JSON */
    @Column(name="menu_json")
    private String menuJson;

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getViewUrl() {
        return viewUrl;
    }

    public void setViewUrl(String viewUrl) {
        this.viewUrl = viewUrl;
    }

    public long getReplyId() {
        return replyId;
    }

    public void setReplyId(long replyId) {
        this.replyId = replyId;
    }

    public String getMenuJson() {
        return menuJson;
    }

    public void setMenuJson(String menuJson) {
        this.menuJson = menuJson;
    }
}