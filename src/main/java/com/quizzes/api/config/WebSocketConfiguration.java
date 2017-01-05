package com.quizzes.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration extends AbstractWebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.initialize();
        config.enableSimpleBroker()
                .setTaskScheduler(taskScheduler)
                .setHeartbeatValue(new long[] {5000, 5000});
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registers STOMP endpoints
        registry.addEndpoint("/ws/quizzes-realtime").setAllowedOrigins("*").withSockJS();
    }

}