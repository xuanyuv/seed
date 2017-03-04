package com.jadyer.seed.boot;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * 自定义Health服务
 * -----------------------------------------------------------------------------------------------------------
 * SpringBoot默认提供了对应用本身、关系数据库连接、MongoDB、Redis和RabbitMQ的健康状态的检测功能
 * 当应用中添加了DataSource类型的bean时,SpringBoot会自动在health服务中暴露数据库连接的信息
 * 应用也可提供自己的健康状态信息,实现org.springframework.boot.actuate.health.HealthIndicator即可
 * -----------------------------------------------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2015/11/29 15:48.
 */
@Component
public class BootHealthIndicator implements HealthIndicator {
	@Override
	public Health health() {
		return Health.up().withDetail("myblog", "https://jadyer.github.io/")
							.withDetail("mygithub", "https://github.com/jadyer")
							.build();
	}
}