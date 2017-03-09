package com.jadyer.seed.test;

import com.jadyer.seed.boot.BootStrap;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class BootStartup {
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(BootStrap.class).profiles("local").run(args);
    }
}