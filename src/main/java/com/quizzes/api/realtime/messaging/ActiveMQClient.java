package com.quizzes.api.realtime.messaging;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import javax.jms.ConnectionFactory;

@Component
public class ActiveMQClient {

    private static final String REAL_TIME_TOPIC = "REAL_TIME.TOPIC";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private ActiveMQProperties activeMQProperties;

    @Autowired
    private MessageBuilder messageBuilder;

    @Autowired
    private WebSocketBroker webSocketBroker;


    @Bean
    public ConnectionFactory connectionFactory() {
        ConnectionFactory connectionFactory =
                new CachingConnectionFactory(new ActiveMQConnectionFactory(activeMQProperties.getBrokerURL()));
        return connectionFactory;
    }

    @Bean
    JmsListenerContainerFactory<?> jmsContainerFactory(ConnectionFactory connectionFactory) {
        SimpleJmsListenerContainerFactory factory = new SimpleJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setPubSubDomain(true);
        return factory;
    }

    public void sendMessage(String message) {
        logger.debug("Sending message: {}", message);
        try {
            JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
            jmsTemplate.setPubSubDomain(true);
            jmsTemplate.convertAndSend(REAL_TIME_TOPIC, message);
        } catch(Exception e) {
            logger.error("Failed sending message: {}\n{}", message, e.getMessage());
        }
    }

    public void sendEventMessage(String collectionUniqueId, String userId, String event) {
        String eventMessage = messageBuilder.buildEventMessage(collectionUniqueId, userId, event);
        this.sendMessage(eventMessage);
    }

    public void sendResetCollectionEventMessage(String collectionUniqueId, String userId) {
        String eventMessage = messageBuilder.buildResetCollectionEventMessage(collectionUniqueId, userId);
        this.sendMessage(eventMessage);
    }

    public void sendCompleteCollectionEventMessage(String collectionUniqueId, String userId) {
        String eventMessage = messageBuilder.buildCompleteCollectionEventMessage(collectionUniqueId, userId);
        this.sendMessage(eventMessage);
    }

    @JmsListener(destination = REAL_TIME_TOPIC, containerFactory = "jmsContainerFactory")
    public void receiveMessage(String message) {
        logger.debug("Receiving message: {}", message);
        try {
            String destination = messageBuilder.extractCollectionUniqueIdFromMessage(message);
            webSocketBroker.sendMessage(destination, message);
        } catch(Exception e) {
            logger.error("Failed receiving message: {}\n{}", message, e.getMessage());
        }
    }

}
