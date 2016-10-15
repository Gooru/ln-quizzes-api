package com.quizzes.api.realtime.service;


import com.quizzes.api.realtime.model.EventIndex;
import com.quizzes.api.realtime.model.Event;
import com.quizzes.api.realtime.repository.EventIndexRepository;
import com.quizzes.api.realtime.repository.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EventIndexRepository eventIndexRepository;

    @Autowired
    private BroadcastService broadcastService;


    public Iterable<Event> findEvents(Iterable<Event> events) {
//        return eventRepository.findAll(events);
        return eventRepository.findAll();
    }

    public Iterable<EventIndex> findEventIndexesOrderByUser(String collectionUniqueId) {
        return eventIndexRepository.findByCollectionUniqueIdOrderByUserIdAsc(collectionUniqueId);
    }

    public Event saveEvent(String collectionUniqueId, String userId, String body) {
        // Broadcast the event message
        broadcastService.broadcastEvent(collectionUniqueId, userId, body);

        // Save the Event in the repository
        EventIndex eventIndex = eventIndexRepository.findFirstByCollectionUniqueIdAndUserId(collectionUniqueId, userId);
        if (Objects.isNull(eventIndex)) {
            eventIndex = eventIndexRepository.save(new EventIndex(collectionUniqueId, userId));
        }
        Event event = eventRepository.save(new Event(eventIndex, body));
        eventIndex.addEventId(event);
        eventIndexRepository.save(eventIndex);

        return event;
    }

    public void completeEventIndexByUser(String collectionUniqueId, String userId) {
        // Broadcasts the event message
        broadcastService.broadcastCompleteCollectionEvent(collectionUniqueId, userId);

        // Finds the Event Index in the repository
        EventIndex eventIndex = eventIndexRepository.findFirstByCollectionUniqueIdAndUserId(collectionUniqueId, userId);
        if (eventIndex != null) {
            eventIndex.setIsComplete(true);
            eventIndexRepository.save(eventIndex);
        }
    }

    //TODO: if the event list is equal to 0 the system produces an error
    public void deleteCollectionEventsByUser(String collectionUniqueId, String userId) {
//        // Broadcast the event message
        broadcastService.broadcastResetCollectionEvent(collectionUniqueId, userId);

        // Deletes the Event Index in the repository
        EventIndex eventIndex = eventIndexRepository.findFirstByCollectionUniqueIdAndUserId(collectionUniqueId, userId);
        if (eventIndex != null) {
            // Remove event index
            eventIndexRepository.delete(eventIndex);
            // Remove all events registered in the event index
            eventRepository.delete(eventRepository.findAll(eventIndex.getEventIds()));
        }
    }

}
