package com.jadyer.seed.test;

import com.jadyer.seed.server.boot.ServerBootStrap;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class ServerStartup {
    public static void main(String[] args) {
        new SpringApplicationBuilder().sources(ServerBootStrap.class).profiles("local").run(args);
    }
}