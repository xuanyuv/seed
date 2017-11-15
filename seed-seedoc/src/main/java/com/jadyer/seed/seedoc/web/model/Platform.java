package com.jadyer.seed.seedoc.web.model;

import com.jadyer.seed.comm.jpa.BaseEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * 平台
 * Generated from seed-simcoder by 玄玉<http://jadyer.cn/> on 2017/11/15 15:45.
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name="t_platform")
public class Platform extends BaseEntity<Long> {
    private static final long serialVersionUID = 7392341218733531063L;
    /** 平台名称 */
    private String name;
    /** 平台联系人 */
    private String linkman;
    /** 平台图片 */
    private String img;
    /** 平台描述 */
    private String intro;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLinkman() {
        return linkman;
    }

    public void setLinkman(String linkman) {
        this.linkman = linkman;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }
}