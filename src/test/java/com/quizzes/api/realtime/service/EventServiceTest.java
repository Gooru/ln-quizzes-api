package com.quizzes.api.realtime.service;

import com.quizzes.api.realtime.model.EventIndex;
import com.quizzes.api.realtime.repository.EventIndexRepository;
import com.quizzes.api.realtime.model.Event;
import com.quizzes.api.realtime.repository.EventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EventServiceTest {
    @InjectMocks
    private EventService eventService = new EventService();

    @Mock
    private BroadcastService broadcastService;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventIndex eventIndexMock;

    @Mock
    private EventIndexRepository eventIndexRepository;

    @Test
    public void findEvents() throws Exception {

    }

    @Test
    public void findEventIndexesOrderByUser() throws Exception {
        eventService.findEventIndexesOrderByUser("uniqueId");
        verify(eventIndexRepository, times(1)).findByCollectionUniqueIdOrderByUserIdAsc("uniqueId");
    }

    @Test
    public void saveEvent() throws Exception {
        EventIndex mockIndex = Mockito.spy(new EventIndex("collectionUniqueId", "userId"));
        UUID id = UUID.randomUUID();
        Event eventMock = new Event(id, null, "{\"description\": \"description test\"}");
        when(eventIndexRepository.findFirstByCollectionUniqueIdAndUserId("collectionUniqueId", "userId")).thenReturn(mockIndex);
        when(eventRepository.save(any(Event.class))).thenReturn(eventMock);

        Event result = eventService.saveEvent("collectionUniqueId", "userId", "{body}");

        verify(broadcastService, times(1)).broadcastEvent(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"), Mockito.eq("{body}"));
        verify(eventIndexRepository, times(1)).findFirstByCollectionUniqueIdAndUserId(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));

        verify(eventRepository, times(1)).save(any(Event.class));
        verify(mockIndex, times(1)).addEventId(any(Event.class));
        verify(eventIndexRepository, times(1)).save(any(EventIndex.class));
        assertNotNull(result);
    }

    @Test
    public void completeEventIndexByUser() throws Exception {
        EventIndex mockIndex = Mockito.spy(new EventIndex("collectionUniqueId", "userId"));
        when(eventIndexRepository.findFirstByCollectionUniqueIdAndUserId("collectionUniqueId", "userId")).thenReturn(mockIndex);

        eventService.completeEventIndexByUser("collectionUniqueId", "userId");

        verify(broadcastService, times(1)).broadcastCompleteCollectionEvent(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));

        verify(eventIndexRepository, times(1)).findFirstByCollectionUniqueIdAndUserId(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
        verify(mockIndex, times(1)).setIsComplete(Mockito.eq(true));
        verify(eventIndexRepository, times(1)).save(any(EventIndex.class));
    }

    @Test
    public void completeEventIndexByUserWhenIndexIsNull() throws Exception {
        EventIndex mockIndex = Mockito.spy(new EventIndex("collectionUniqueId", "userId"));

        eventService.completeEventIndexByUser("collectionUniqueId", "userId");

        verify(broadcastService, times(1)).broadcastCompleteCollectionEvent(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
        when(eventIndexRepository.findFirstByCollectionUniqueIdAndUserId("collectionUniqueId", "userId")).thenReturn(null);
        verify(mockIndex, times(0)).setIsComplete(Mockito.eq(true));
        verify(eventIndexRepository, times(0)).save(any(EventIndex.class));
    }

    @Test
    public void deleteCollectionEventsByUser() throws Exception {
        List<UUID> uuidList = Mockito.mock(List.class);
        uuidList.add(UUID.randomUUID());

        EventIndex mockIndex = Mockito.spy(new EventIndex("collectionUniqueId", "userId"));

        when(eventIndexRepository.findFirstByCollectionUniqueIdAndUserId("collectionUniqueId", "userId")).thenReturn(mockIndex);
        when(eventRepository.findAll(any(List.class))).thenReturn(uuidList);

        eventService.deleteCollectionEventsByUser("collectionUniqueId", "userId");

        verify(broadcastService, times(1)).broadcastResetCollectionEvent(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
        verify(eventIndexRepository, times(1)).findFirstByCollectionUniqueIdAndUserId(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
        verify(eventIndexRepository, times(1)).delete(Mockito.eq(mockIndex));
        verify(eventRepository, times(1)).delete(any(Iterable.class));
    }

    @Test
    public void deleteCollectionEventsByUserIndexNull() throws Exception {
        EventIndex mockIndex = Mockito.spy(new EventIndex("collectionUniqueId", "userId"));
        List<Event> mockList = Mockito.mock(List.class);
        when(eventIndexRepository.findFirstByCollectionUniqueIdAndUserId("collectionUniqueId", "userId")).thenReturn(null);

        eventService.deleteCollectionEventsByUser("collectionUniqueId", "userId");

        verify(broadcastService, times(1)).broadcastResetCollectionEvent(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
        verify(eventIndexRepository, times(1)).findFirstByCollectionUniqueIdAndUserId(Mockito.eq("collectionUniqueId"), Mockito.eq("userId"));
        verify(eventIndexRepository, times(0)).delete(Mockito.eq(mockIndex));
        verify(eventRepository, times(0)).delete(Mockito.eq(mockList));
    }

}