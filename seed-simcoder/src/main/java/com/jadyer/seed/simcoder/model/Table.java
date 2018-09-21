package com.jadyer.seed.simcoder.model;

/**
 * 表信息
 * Created by 玄玉<https://jadyer.cn/> on 2017/9/7 17:22.
 */
public class Table {
    /** 表名 */
    private String name;
    /** 表描述 */
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}