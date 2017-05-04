package com.jadyer.seed.controller.swagger;

/**
 * Created by 玄玉<http://jadyer.cn/> on 2016/9/26 14:41.
 */
class SwaggerDemoUser {
    private long id;
    private String username;
    private String position;

    public SwaggerDemoUser(){}

    public SwaggerDemoUser(long id, String username, String position) {
        this.id = id;
        this.username = username;
        this.position = position;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    String getPosition() {
        return position;
    }

    void setPosition(String position) {
        this.position = position;
    }
}