package com.quizzes.realtime.model;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

import java.util.UUID;

@Entity(name = "event")
@TypeDefs( {@TypeDef( name= "StringJsonObject", typeClass = StringJsonUserType.class)})
public class Event {

    @Id
    @GenericGenerator(name = "uuid-gen", strategy = "uuid2")
    @GeneratedValue(generator = "uuid-gen")
    @org.hibernate.annotations.Type(type="pg-uuid")
    private UUID id;
//    private UUID eventIndexId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_index", referencedColumnName = "id")
    private EventIndex eventIndex;

    @Column(name="eventBody",columnDefinition="jsonb")
    @Type(type = "StringJsonObject")
    private String eventBody;

    public Event() {
    }

    public Event(UUID id, EventIndex eventIndex, String eventBody) {
        this.id = id;
        this.eventIndex = eventIndex;
        this.eventBody = eventBody;
    }

    public Event(EventIndex eventIndex, String eventBody) {
        this.eventIndex = eventIndex;
        this.eventBody = eventBody;
    }

    public UUID getId() {
        return id;
    }

    public EventIndex getEventIndex() {
        return eventIndex;
    }

    public UUID getEventIndexId() { return eventIndex.getId(); }

    public String getEventBody() {
        return eventBody;
    }

    @Override
    public String toString() {
        return String.format("Event[id='%s', eventIndexId='%s', eventBody='%s']",
                id, eventIndex.getId(), eventBody);
    }

}
