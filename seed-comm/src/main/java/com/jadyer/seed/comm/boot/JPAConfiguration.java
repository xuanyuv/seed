package com.jadyer.seed.comm.boot;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages="com.jadyer.seed")
@EnableJpaRepositories(basePackages="com.jadyer.seed")
public class JPAConfiguration {}