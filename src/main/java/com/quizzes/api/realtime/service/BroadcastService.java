package com.quizzes.api.realtime.service;

import com.quizzes.api.realtime.messaging.ActiveMQClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BroadcastService {

    @Autowired
    private ActiveMQClient activeMQClient;

    /**
     * Prepares an event message and sends it to the clients
     * @param collectionUniqueId collection unique id
     * @param userId user id
     * @param event event body
     */
    public void broadcastEvent(String collectionUniqueId, String userId, String event) {
        activeMQClient.sendEventMessage(collectionUniqueId, userId, event);
    }

    public void broadcastResetCollectionEvent(String collectionUniqueId, String userId) {
        activeMQClient.sendResetCollectionEventMessage(collectionUniqueId, userId);
    }

    public void broadcastCompleteCollectionEvent(String collectionUniqueId, String userId) {
        activeMQClient.sendCompleteCollectionEventMessage(collectionUniqueId, userId);
    }

}
