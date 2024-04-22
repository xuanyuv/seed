package com.jadyer.seed.admin.boot;

import org.springframework.context.annotation.Configuration;

/**
 * Created by 玄玉<https://jadyer.cn/> on 2022/1/14 11:14.
 */
@Configuration
public class SecurityConfiguration {
}

/*
【下面是springboot-2.1.6.RELEASE的写法，springboot-3.2.5的写法待补充...】
【下面是springboot-2.1.6.RELEASE的写法，springboot-3.2.5的写法待补充...】
【下面是springboot-2.1.6.RELEASE的写法，springboot-3.2.5的写法待补充...】

import de.codecentric.boot.admin.server.config.AdminServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    private final String adminServerContextPath;

    public SecurityConfiguration(AdminServerProperties adminServerProperties) {
        this.adminServerContextPath = adminServerProperties.getContextPath();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // super.configure(http);
        SavedRequestAwareAuthenticationSuccessHandler successHandler = new SavedRequestAwareAuthenticationSuccessHandler();
        successHandler.setTargetUrlParameter("redirectTo");
        http.authorizeRequests()
                .antMatchers(adminServerContextPath + "/assets/**").permitAll()
                .antMatchers(adminServerContextPath + "/login").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin().loginPage(adminServerContextPath + "/login").successHandler(successHandler).and()
                .logout().logoutUrl(adminServerContextPath + "/logout").and()
                .httpBasic().and()
                .csrf().disable();
    }
}
*/