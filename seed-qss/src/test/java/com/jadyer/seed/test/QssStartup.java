package com.jadyer.seed.test;

import com.jadyer.seed.qss.boot.QssBootStrap;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2017/3/4 20:05.
 */
public class QssStartup extends QssBootStrap {
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(QssBootStrap.class).profiles("local").run(args);
    }
}