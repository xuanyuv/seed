package com.jadyer.seed.boot;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackages="${scan.base.packages}")
@EnableJpaRepositories(basePackages="${scan.base.packages}")
public class JPAConfiguration {}