package com.jadyer.seed.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 玄玉服务器启动类
 * Created by 玄玉<https://jadyer.github.io/> on 2013/9/3 20:14.
 */
public class MainApp {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("applicationContext.xml");
    }
}