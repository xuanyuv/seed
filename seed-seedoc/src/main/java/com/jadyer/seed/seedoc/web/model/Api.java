package com.jadyer.seed.seedoc.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * API
 * Generated from seed-simcoder by 玄玉<https://jadyer.cn/> on 2017/11/15 17:42.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_api")
public class Api extends BaseEntity<Long> {
    private static final long serialVersionUID = 1773208952866545630L;
    /** 平台ID，对应t_platform#id */
    @Column(name="platform_id")
    private long platformId;
    /** API名称 */
    private String name;
    /** API的Markdown文本 */
    @Column(name="md_text")
    private String mdText;
    /** API的HTML文本 */
    @Column(name="html_text")
    private String htmlText;

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

    public String getMdText() {
        return mdText;
    }

    public void setMdText(String mdText) {
        this.mdText = mdText;
    }

    public String getHtmlText() {
        return htmlText;
    }

    public void setHtmlText(String htmlText) {
        this.htmlText = htmlText;
    }
}