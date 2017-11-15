package com.jadyer.seed.seedoc.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * API
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/11/15 15:45.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_api")
public class Api extends BaseEntity<Long> {
    private static final long serialVersionUID = 7592670602653385762L;
    /** 平台ID，对应t_platform#id */
    @Column(name="platform_id")
    private long platformId;
    /** API名称 */
    private String name;
    /** API内容 */
    private String content;

    public long getPlatformId() {
        return platformId;
    }

    public void setPlatformId(long platformId) {
        this.platformId = platformId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}