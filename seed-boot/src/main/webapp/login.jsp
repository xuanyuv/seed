<%@ page pageEncoding="UTF-8"%>

这是springboot环境下的jsp页面首页，点此访问<a href="${pageContext.request.contextPath}/view?url=time">jsp自定义标签测试页</a>

<%--
JSP测试时依靠
1、注释com.jadyer.seed.boot.AuthConfiguration.@Configuration
2、com.jadyer.seed.boot.MVCConfiguration.@Configuration
3、/seed/seed-boot/pom.xml中的spring-boot-maven-plugin
4、com.jadyer.seed.boot.BootStrap.main()

Thymeleaf测试时需要
1、注释com.jadyer.seed.boot.MVCConfiguration.@Configuration
2、启用com.jadyer.seed.boot.AuthConfiguration.@Configuration
3、启用com.jadyer.seed.controller.DemoController.index()
4、引入/seed/pom.xml中的spring-boot-starter-thymeleaf
5、启动com.jadyer.seed.test.BootStartup.main()
6、访问http://127.0.0.1/boot/
--%>