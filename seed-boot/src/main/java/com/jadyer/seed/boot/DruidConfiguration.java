package com.jadyer.seed.boot;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * 有时间研究研究这里面的属性https://github.com/alibaba/druid/issues/1344
 * 另经测试：当应用正常运行过程中，数据库突然宕掉后过一段时间又恢复，druid会自动重连
 * Created by 玄玉<https://jadyer.github.io/> on 2016/01/24 12:09.
 */
//@Configuration
public class DruidConfiguration {
	@Value("${spring.datasource.url}")
	private String url;

	@Value("${spring.datasource.username}")
	private String username;

	@Value("${spring.datasource.password}")
	private String password;

	@Value("${spring.datasource.minIdle:1}")
	private int minIdle;

	@Value("${spring.datasource.maxActive:20}")
	private int maxActive;

	@Value("${spring.datasource.initialSize:1}")
	private int initialSize;

	@Value("${spring.datasource.maxWait:60000}")
	private int maxWait;

	@Value("${spring.datasource.minEvictableIdleTimeMillis:300000}")
	private int minEvictableIdleTimeMillis;

	@Value("${spring.datasource.timeBetweenEvictionRunsMillis:60000}")
	private int timeBetweenEvictionRunsMillis;

	@Bean
	public DataSource druidDataSource(){
		DruidDataSource druidDataSource = new DruidDataSource();
		druidDataSource.setName("datasource_jadyer");
		druidDataSource.setUrl(this.url);
		druidDataSource.setUsername(this.username);
		druidDataSource.setPassword(this.password);
		//配置初始化大小、最小、最大
		druidDataSource.setMinIdle(this.minIdle);
		//配置初始化大小、最小、最大
		druidDataSource.setMaxActive(this.maxActive);
		//配置初始化大小、最小、最大
		druidDataSource.setInitialSize(this.initialSize);
		//配置获取连接等待超时的时间
		druidDataSource.setMaxWait(this.maxWait);
		//配置一个连接在池中最小生存的时间,单位是毫秒
		druidDataSource.setMinEvictableIdleTimeMillis(this.minEvictableIdleTimeMillis);
		//配置间隔多久才进行一次检测,检测需要关闭的空闲连接,单位是毫秒
		druidDataSource.setTimeBetweenEvictionRunsMillis(this.timeBetweenEvictionRunsMillis);
		//默认的testWhileIdle=true,testOnBorrow=false,testOnReturn=false
		druidDataSource.setValidationQuery("SELECT 1");
		/*
		 * 下面两行设置用于-->打开PSCache，并且指定每个连接上PSCache的大小
		 * PSCache(preparedStatement)对支持游标的数据库性能提升巨大，比如说Oracle/DB2/SQLServer，但MySQL下建议关闭
		 */
		druidDataSource.setPoolPreparedStatements(false);
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(-1);
		try{
			druidDataSource.setFilters("wall,mergeStat");
		}catch(SQLException e){
			e.printStackTrace();
		}
		return druidDataSource;
	}

	@Bean
	public FilterRegistrationBean druidFilterRegistrationBean(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new WebStatFilter());
		filterRegistrationBean.addUrlPatterns("/*");
		filterRegistrationBean.addInitParameter("profileEnable", "true");
		filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*");
		return filterRegistrationBean;
	}

	@Bean
	public ServletRegistrationBean druidServlet(){
		//http://host/druid/index.html
		//return new ServletRegistrationBean(new StatViewServlet(), "/druid/*")
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean();
		servletRegistrationBean.setServlet(new StatViewServlet());
		servletRegistrationBean.addUrlMappings("/druid/*");
		servletRegistrationBean.addInitParameter(ResourceServlet.PARAM_NAME_USERNAME, "druid");
		servletRegistrationBean.addInitParameter(ResourceServlet.PARAM_NAME_PASSWORD, "druid");
		return servletRegistrationBean;
	}
}