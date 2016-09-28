package com.quizzes.realtime.model;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity(name = "event_index")
public class EventIndex implements Serializable {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @org.hibernate.annotations.Type(type="pg-uuid")
    private UUID id;
    private String collectionUniqueId;
    private String userId;
    private boolean isComplete;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "eventIndex")
    private Collection<Event> events;

    public EventIndex() {
    }

    public EventIndex(String collectionUniqueId, String userId) {
        this.collectionUniqueId = collectionUniqueId;
        this.userId = userId;
        this.isComplete = false;
        this.events = new ArrayList<>();
    }

    public EventIndex(String collectionUniqueId, String userId, Boolean isComplete, List<Event> events) {
        this.collectionUniqueId = collectionUniqueId;
        this.userId = userId;
        this.isComplete = isComplete;
        this.events = events;
    }

    public void addEventId(Event event) {
        this.events.add(event);
    }

    public UUID getId() {
        return id;
    }

    public String getCollectionUniqueId() {
        return collectionUniqueId;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isComplete() {
        return isComplete;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    public Collection<Event> getEvents() {
        return this.events;
    }

    public List<UUID> getEventIds() {
        List<UUID> ids = events.stream().map(Event::getId).collect(Collectors.toList());
        return ids;
    }

}
