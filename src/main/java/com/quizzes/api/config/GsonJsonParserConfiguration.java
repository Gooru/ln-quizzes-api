package com.quizzes.api.config;

import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonJsonParserConfiguration {

    @Bean
    public JsonParser jsonParser() {
        return new GsonJsonParser();
    }

}
