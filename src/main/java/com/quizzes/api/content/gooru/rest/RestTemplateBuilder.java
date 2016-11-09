package com.quizzes.api.content.gooru.rest;

import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class RestTemplateBuilder {

    public static RestTemplate buildRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        messageConverters.add(new GsonHttpMessageConverter());
        restTemplate.setMessageConverters(messageConverters);

        return restTemplate;
    }
}
