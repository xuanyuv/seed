package com.jadyer.seed.test;

import com.jadyer.seed.boot.BootStrap;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * Created by 玄玉<https://jadyer.github.io/> on 2016/5/10 20:18.
 */
public class BootStartup {
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(BootStrap.class).profiles("local").run(args);
    }
}