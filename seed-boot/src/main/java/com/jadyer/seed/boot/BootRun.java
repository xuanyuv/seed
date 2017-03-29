package com.jadyer.seed.boot;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.servlet.Filter;

@SpringBootApplication(scanBasePackages="${scan.base.packages}", exclude={DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
public class BootRun {
	public static void main(String[] args) {
		new SpringApplicationBuilder().sources(BootRun.class).profiles("local").run(args);
	}

	@Bean
	public Filter characterEncodingFilter(){
		return new CharacterEncodingFilter("UTF-8", true);
	}
}