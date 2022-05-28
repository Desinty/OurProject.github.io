package com.reggie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.VendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2// 开启Swagger2的自动配置
public class SwaggerConfig {

    @Bean
    public Docket docket(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("一组")
                .apiInfo(apiInfo())
                .enable(true)
                .select()// 通过.select()方法，去配置扫描接口,RequestHandlerSelectors配置如何扫描接口
                .apis(RequestHandlerSelectors.basePackage("com.reggie.controller"))
                .build();
    }

    public ApiInfo apiInfo(){
        Contact DEFAULT_CONTACT = new Contact("xLoj", "https://ai.taobao.com/?pid=mm_130402922_1111150093_109790500145&union_lens=lensId%3APUB%401627031575%400bb0b71b_0996_17ad2a39a80_063d%4001", "1140198670@qq.com");
        return new ApiInfo(
                "瑞吉外卖",
                "这里有吃不完的美食",
                "1.0",
                "https://ai.taobao.com/?pid=mm_130402922_1111150093_109790500145&union_lens=lensId%3APUB%401627031575%400bb0b71b_0996_17ad2a39a80_063d%4001",
                DEFAULT_CONTACT,
                "Apache 2.0",
                "http://www.apache.org/licenses/LICENSE-2.0",
                new ArrayList<VendorExtension>());
    }
}