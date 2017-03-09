package com.jadyer.seed.test;

import com.jadyer.seed.open.boot.OpenBootStrap;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class OpenStartup {
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(OpenBootStrap.class).profiles("local").run(args);
    }
}