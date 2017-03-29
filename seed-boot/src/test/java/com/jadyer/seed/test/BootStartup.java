package com.jadyer.seed.test;

import com.jadyer.seed.boot.BootStrap;
import com.jadyer.seed.boot.event.ApplicationEnvironmentPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationFailedEventListener;
import com.jadyer.seed.boot.event.ApplicationPreparedEventListener;
import com.jadyer.seed.boot.event.ApplicationStartedEventListener;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class BootStartup {
	public static void main(String[] args) {
		//SpringApplication.run(BootStarp.class, args);
		//new SpringApplicationBuilder().sources(BootStrap.class).profiles("local").run(args);
		new SpringApplicationBuilder().sources(BootStrap.class)
				.listeners(new ApplicationStartedEventListener())
				.listeners(new ApplicationEnvironmentPreparedEventListener())
				.listeners(new ApplicationPreparedEventListener())
				.listeners(new ApplicationFailedEventListener())
				.profiles("local")
				.run(args);
	}
}