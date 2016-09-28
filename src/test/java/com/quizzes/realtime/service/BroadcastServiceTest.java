package com.quizzes.realtime.service;

import com.quizzes.realtime.messaging.ActiveMQClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by fperez on 9/23/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class BroadcastServiceTest {
    @InjectMocks
    private BroadcastService broadcastService = new BroadcastService();

    @Mock
    private ActiveMQClient activeMQClient;

    @Test
    public void broadcastEvent() throws Exception {
        broadcastService.broadcastEvent("collectionUniqueId", "userId", "event");
        verify(activeMQClient, times(1)).sendEventMessage(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"), Mockito.eq("event"));
    }

    @Test
    public void broadcastResetCollectionEvent() throws Exception {
        broadcastService.broadcastResetCollectionEvent("collectionUniqueId", "userId");
        verify(activeMQClient, times(1)).sendResetCollectionEventMessage(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
    }

    @Test
    public void broadcastCompleteCollectionEvent() throws Exception {
        broadcastService.broadcastCompleteCollectionEvent("collectionUniqueId", "userId");
        verify(activeMQClient, times(1)).sendCompleteCollectionEventMessage(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
    }

}