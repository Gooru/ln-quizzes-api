package com.quizzes.api;

import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.repository.ContextRepositoryImpl;
import com.quizzes.api.common.repository.ProfileRepository;
import com.quizzes.api.common.repository.ProfileRepositoryImpl;
import com.quizzes.api.common.service.ProfileService;
import com.quizzes.api.common.service.ProfileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    public ProfileRepository profileRepository() {
        return new ProfileRepositoryImpl();
    }

    @Bean
    public ProfileService profileService() {
        return new ProfileServiceImpl();
    }

    @Bean
    public ContextRepository contextRepository() {
        return new ContextRepositoryImpl();
    }

    @Bean
    public Logger logger(){
        return LoggerFactory.getLogger(this.getClass());
    }

    @Bean
    public Docket newsApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("real-time")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/quizzes.*"))
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
