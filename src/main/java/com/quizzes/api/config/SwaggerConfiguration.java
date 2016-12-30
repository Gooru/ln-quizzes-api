package com.quizzes.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import static springfox.documentation.builders.PathSelectors.regex;

@EnableSwagger2
@Configuration
public class SwaggerConfiguration {

    @Bean
    public Docket quizzesApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("quizzes-api")
                .apiInfo(apiInfo())
                .select()
                .paths(regex("/quizzes/api/.*"))
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Quizzes API")
                .description("Quizzes API is a set of RESTful endpoints to handle Assessments and Collections. " +
                        "Those Assessments and Collection can be assigned to a group of people. Each Assessment or " +
                        "Collection can be taken by each person in the group and will have a score indicating the " +
                        "correct and wrong responses.")
                .build();
    }

}
