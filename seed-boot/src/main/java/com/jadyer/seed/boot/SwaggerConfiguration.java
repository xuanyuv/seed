package com.jadyer.seed.boot;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Springfox的前身是swagger-springmvc
 * -------------------------------------------------------------------
 * 接口地址为：http://127.0.0.1/boot/swagger-ui.html
 * -------------------------------------------------------------------
 * Created by 玄玉<https://jadyer.github.io/> on 2016/9/26 13:55.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {
    private ApiInfo getGpiInfo(){
        return new ApiInfoBuilder()
                .title("SpringBoot集成Swagger2的示例")
                .description("REST风格的APIs")
                .termsOfServiceUrl("https://jadyer.github.io/")
                .contact(new Contact("玄玉", "https://github.com/jadyer/seed", "jadyer@yeah.net"))
                .version("1.0")
                .build();
    }

    @Bean
    public Docket ceeateRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.getGpiInfo())
                //.forCodeGeneration(true)
                //.useDefaultResponseMessages(false)
                //指定api前缀：比如Controller定义了/user/list，则生成的文档中api路径为/myapi/user/list
                //.pathMapping("/myapi")
                //select()返回一个ApiSelectorBuilder实例用来控制哪些接口暴露给Swagger来展现
                .select()
                //Swagger会扫描该包下所有Controller定义的API，并产生文档内容（除了被@ApiIgnore指定的请求）
                .apis(RequestHandlerSelectors.basePackage("com.jadyer.seed.controller"))
                //.paths(PathSelectors.regex("/comm.*"))
                //.paths(PathSelectors.regex("/common/.*"))
                .paths(PathSelectors.any())
                .build();
    }
}