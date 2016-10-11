package com.quizzes.api;

import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.ContextServiceImpl;
import com.quizzes.api.gooru.service.GooruContextServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
public class AppConfig {

    @Bean
    public JsonParser jsonParser() {
        return new GsonJsonParser();
    }

    @Bean
    public ContextService contextService() {
        return new GooruContextServiceImpl();
    }

    @Bean
    @ConditionalOnMissingBean(ContextService.class)
    public ContextService defaultContextService() {
        return new ContextServiceImpl();
    }

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("real-time")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/nucleus.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Quizzes API")
                .description("API Documentation")
                .termsOfServiceUrl("http://about.gooru.org/terms-and-conditions")
                .contact("gooru.org")
                .license("Quizzes API 0.1.0")
                .licenseUrl("http://gooru.org")
                .version("0.1.0")
                .build();
    }

}
