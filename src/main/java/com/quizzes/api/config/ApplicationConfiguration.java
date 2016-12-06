package com.quizzes.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJms
@Configuration
@EnableTransactionManagement
public class ApplicationConfiguration {

}
