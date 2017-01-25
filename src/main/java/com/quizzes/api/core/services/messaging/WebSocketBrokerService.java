package com.quizzes.api.core.services.messaging;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class WebSocketBrokerService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    /**
     * Sends the message to all the clients connected to the destination channel
     * @param destination the destination channel
     * @param message the message body
     */
    public void sendMessage(String destination, String message) throws Exception {
        messagingTemplate.send("/topic/" + destination, MessageBuilder.withPayload(message.getBytes()).build());
    }

}
