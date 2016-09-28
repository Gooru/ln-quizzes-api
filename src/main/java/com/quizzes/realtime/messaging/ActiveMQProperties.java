package com.quizzes.realtime.messaging;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(value = "spring.activemq")
public class ActiveMQProperties {

    private String brokerURL = "tcp://localhost:61616";

    public String getBrokerURL() {
        return this.brokerURL;
    }

    public void setBrokerURL(String brokerURL) {
        this.brokerURL = brokerURL;
    }

}
