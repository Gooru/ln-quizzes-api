package com.quizzes.api.core.services.messaging;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.messaging.EventMessageWrapperDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.dtos.messaging.StartContextEventMessageDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ActiveMQClientService {

    private static final String QUIZZES_REAL_TIME_TOPIC = "QUIZZES.REAL_TIME.TOPIC";
    private static final String START_CONTEXT_EVENT = "startContextEvent";
    private static final String ON_RESOURCE_EVENT = "onResourceEvent";
    private static final String FINISH_CONTEXT_EVENT = "finishContextEvent";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private WebSocketBrokerService webSocketBrokerService;

    @Autowired
    Gson gson;

    public void sendStartContextEventMessage(UUID contextId, UUID profileId,
                                             StartContextEventMessageDto startEventMessage) {
        EventMessageWrapperDto eventMessageWrapper = new EventMessageWrapperDto();
        eventMessageWrapper.setContextId(contextId);
        eventMessageWrapper.setProfileId(profileId);
        eventMessageWrapper.setEventName(START_CONTEXT_EVENT);
        eventMessageWrapper.setEventBody(startEventMessage);

        sendMessage(gson.toJson(eventMessageWrapper));
    }

    public void sendOnResourceEventMessage(UUID contextId, UUID profileId,
                                           OnResourceEventMessageDto onResourceEventMessage) {
        EventMessageWrapperDto eventMessageWrapper = new EventMessageWrapperDto();
        eventMessageWrapper.setContextId(contextId);
        eventMessageWrapper.setProfileId(profileId);
        eventMessageWrapper.setEventName(ON_RESOURCE_EVENT);
        eventMessageWrapper.setEventBody(onResourceEventMessage);

        sendMessage(gson.toJson(eventMessageWrapper));
    }

    public void sendFinishContextEventMessage(UUID contextId, UUID profileId,
                                              FinishContextEventMessageDto finishContextEventMessage) {
        EventMessageWrapperDto eventMessageWrapper = new EventMessageWrapperDto();
        eventMessageWrapper.setContextId(contextId);
        eventMessageWrapper.setProfileId(profileId);
        eventMessageWrapper.setEventName(FINISH_CONTEXT_EVENT);
        eventMessageWrapper.setEventBody(finishContextEventMessage);

        sendMessage(gson.toJson(eventMessageWrapper));
    }

    @JmsListener(destination = QUIZZES_REAL_TIME_TOPIC, containerFactory = "jmsContainerFactory")
    public void receiveMessage(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug("Receiving message from ActiveMQ Server: {}", message);
        }

        try {
            EventMessageWrapperDto eventMessageWrapper = gson.fromJson(message, EventMessageWrapperDto.class);
            String destination = eventMessageWrapper.getContextId().toString();
            webSocketBrokerService.sendMessage(destination, message);
        } catch (Exception e) {
            logger.error("Failed receiving message from ActiveMQ Server: {}\n{}", message, e.getMessage());
        }
    }

    private void sendMessage(String message) {
        if (logger.isDebugEnabled()) {
            logger.debug("Sending message to ActiveMQ Server: {}", message);
        }

        try {
            JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
            jmsTemplate.setPubSubDomain(true);
            jmsTemplate.convertAndSend(QUIZZES_REAL_TIME_TOPIC, message);
        } catch (Exception e) {
            logger.error("Failed sending message to ActiveMQ Server: {}\n{}", message, e.getMessage());
        }
    }

}
