package com.quizzes.api;

import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.ContextServiceImpl;
import com.quizzes.api.gooru.service.GooruContextServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Objects;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

    @Autowired
    Environment env;

    @Bean
    public JsonParser jsonParser() {
        return new GsonJsonParser();
    }

    @Bean
    public ContextService contextService() {
        if (Objects.equals(env.getProperty("quizzes.lms.configuration"), "gooru")) {
            return new GooruContextServiceImpl();
        }
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
