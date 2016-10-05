package com.quizzes.api;

import com.quizzes.api.realtime.repository.CollectionOnAirRepository;
import com.quizzes.api.realtime.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jms.annotation.EnableJms;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.UUID;

import static springfox.documentation.builders.PathSelectors.regex;

@Configuration
@EnableJms
@EnableSwagger2
@SpringBootApplication
@EntityScan(basePackages = { "com.quizzes.api.common.model", "com.quizzes.api.realtime.model" })
@EnableJpaRepositories
public class AppRunner implements CommandLineRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CollectionOnAirRepository collectionOnAirRepository;

    public static void main(String[] args) {
        // Launch the application
        SpringApplication.run(AppRunner.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        checkRepositoryConnection();
    }

    private void checkRepositoryConnection() {
        try {
            logger.info("Connecting to repository...");
            collectionOnAirRepository.findOne(UUID.fromString("a68e3677-adab-46bc-9048-0ab7bdbcc16d"));
            logger.info("Connected!!!");
        } catch(Exception e) {
            logger.error("Error trying to connect to repository: {}", e.getMessage());
            System.exit(1);
        }
    }

    @Bean
    public EventService eventService() {
        EventService eventService = new EventService();
        // set properties, etc.
        return eventService;
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

