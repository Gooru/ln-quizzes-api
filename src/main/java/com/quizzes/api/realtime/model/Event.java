package com.quizzes.api.realtime.model;

import com.quizzes.api.common.model.StringJsonType;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.UUID;

@TypeDefs( {@TypeDef( name= "StringJsonType", typeClass = StringJsonType.class)})

@Entity(name = "event")
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
    @Type(type = "StringJsonType")
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
