package com.quizzes.api.config;

import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;

import java.util.ArrayList;
import java.util.Collection;

/**
 * This class is a custom configuration of a HttpMessageConverter to use GSON (instead of Jackson) as
 * the default library for JSON conversion.
 *
 * Look more information in: http://www.leveluplunch.com/java/tutorials/023-configure-integrate-gson-spring-boot/
 */
@Configuration
public class HttpMessageConverterConfiguration {

    @Bean
    public HttpMessageConverters customConverters() {

        Collection<HttpMessageConverter<?>> messageConverters = new ArrayList<>();

        GsonHttpMessageConverter gsonHttpMessageConverter = new GsonHttpMessageConverter();
        messageConverters.add(gsonHttpMessageConverter);

        return new HttpMessageConverters(true, messageConverters);
    }
}