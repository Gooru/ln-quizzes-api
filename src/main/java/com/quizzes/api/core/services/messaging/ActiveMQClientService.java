package com.quizzes.api.core.services.messaging;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.messaging.EventMessageWrapperDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.dtos.messaging.StartContextEventMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class ActiveMQClientService {

    private static final String QUIZZES_REAL_TIME_TOPIC = "QUIZZES.REAL_TIME.TOPIC";
    private static final String START_CONTEXT_EVENT = "startContextEvent";
    private static final String ON_RESOURCE_EVENT = "onResourceEvent";
    private static final String FINISH_CONTEXT_EVENT = "finishContextEvent";

    @Autowired
    private ConfigurableApplicationContext context;

    @Autowired
    private WebSocketBrokerService webSocketBrokerService;

    @Autowired
    private Gson gson;

    public void sendStartContextEventMessage(UUID contextId, UUID profileId,
                                             StartContextEventMessageDto startEventMessage) {
        sendEventMessage(contextId, profileId, START_CONTEXT_EVENT, startEventMessage);
    }

    public void sendOnResourceEventMessage(UUID contextId, UUID profileId,
                                           OnResourceEventMessageDto onResourceEventMessage) {
        sendEventMessage(contextId, profileId, ON_RESOURCE_EVENT, onResourceEventMessage);
    }

    public void sendFinishContextEventMessage(UUID contextId, UUID profileId,
                                              FinishContextEventMessageDto finishContextEventMessage) {
        sendEventMessage(contextId, profileId, FINISH_CONTEXT_EVENT, finishContextEventMessage);
    }

    @JmsListener(destination = QUIZZES_REAL_TIME_TOPIC, containerFactory = "jmsContainerFactory")
    public void receiveMessage(String message) {
        if (log.isDebugEnabled()) {
            log.debug("Receiving message from ActiveMQ Server: {}", message);
        }

        try {
            EventMessageWrapperDto eventMessageWrapper = gson.fromJson(message, EventMessageWrapperDto.class);
            String destination = eventMessageWrapper.getContextId().toString();
            webSocketBrokerService.sendMessage(destination, message);
        } catch (Exception e) {
            log.error("Failed receiving message from ActiveMQ Server: {}\n{}", message, e.getMessage());
        }
    }

    private void sendEventMessage(UUID contextId, UUID profileId, String eventName, Object eventMessage) {
        EventMessageWrapperDto eventMessageWrapper = new EventMessageWrapperDto();
        eventMessageWrapper.setContextId(contextId);
        eventMessageWrapper.setProfileId(profileId);
        eventMessageWrapper.setEventName(eventName);
        eventMessageWrapper.setEventBody(eventMessage);

        sendMessage(gson.toJson(eventMessageWrapper));
    }

    private void sendMessage(String message) {
        if (log.isDebugEnabled()) {
            log.debug("Sending message to ActiveMQ Server: {}", message);
        }

        try {
            JmsTemplate jmsTemplate = context.getBean(JmsTemplate.class);
            jmsTemplate.setPubSubDomain(true);
            jmsTemplate.convertAndSend(QUIZZES_REAL_TIME_TOPIC, message);
        } catch (Exception e) {
            log.error("Failed sending message to ActiveMQ Server: {}\n{}", message, e.getMessage());
        }
    }

}
