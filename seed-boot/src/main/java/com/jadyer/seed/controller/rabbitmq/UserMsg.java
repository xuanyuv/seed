package com.jadyer.seed.controller.rabbitmq;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2018/3/23 12:09.
 */
public class UserMsg {
    private int id;
    private String name;
    private String website;

    public UserMsg() {}

    UserMsg(int id, String name, String website) {
        this.id = id;
        this.name = name;
        this.website = website;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}