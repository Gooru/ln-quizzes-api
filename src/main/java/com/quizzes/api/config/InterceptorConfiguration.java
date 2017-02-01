package com.quizzes.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.quizzes.api.common.interceptor.AuthorizationTokenInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    AuthorizationTokenInterceptor quizzesInterceptor() {
        return new AuthorizationTokenInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(quizzesInterceptor())
                .addPathPatterns("/quizzes/api/**");
    }
}