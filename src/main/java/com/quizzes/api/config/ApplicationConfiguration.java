package com.quizzes.api.config;

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
import org.springframework.jms.annotation.EnableJms;

@EnableJms
@Configuration
public class ApplicationConfiguration {

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
}
