package com.quizzes.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GsonConfiguration {

    @Bean
    public Gson gson() {
        return new GsonBuilder().serializeNulls().create();
    }

    @Bean
    public Gson gsonPretty() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

}
