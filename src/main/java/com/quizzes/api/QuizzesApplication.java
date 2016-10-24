package com.quizzes.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableJms
@EnableSwagger2
@SpringBootApplication
@EntityScan(basePackages = { "com.quizzes.api.common.model", "com.quizzes.api.realtime.model" })
@EnableJpaRepositories
public class QuizzesApplication {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(QuizzesApplication.class, args);
    }

}

