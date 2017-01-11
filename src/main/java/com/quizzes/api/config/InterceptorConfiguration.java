package com.quizzes.api.config;

import com.quizzes.api.common.interceptor.SessionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableWebMvc
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

    @Bean
    SessionInterceptor quizzesInterceptor() {
        return new SessionInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(quizzesInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/**/session/authorization");
    }

}