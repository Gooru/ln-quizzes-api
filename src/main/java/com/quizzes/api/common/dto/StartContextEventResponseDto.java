package com.quizzes.api.common.dto;

import com.google.gson.annotations.SerializedName;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.response.AttemptDto;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class StartContextEventResponseDto {

    private UUID id;

    private CollectionDto collection;

    private UUID currentResourceId;

    @ApiModelProperty(hidden = true)
    @SerializedName("events")
    private List<Map<String, Object>> eventsResponse;

    private transient List<AttemptDto> events;

    public StartContextEventResponseDto() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public CollectionDto getCollection() {
        return collection;
    }

    public void setCollection(CollectionDto collection) {
        this.collection = collection;
    }

    public UUID getCurrentResourceId() {
        return currentResourceId;
    }

    public void setCurrentResourceId(UUID currentResourceId) {
        this.currentResourceId = currentResourceId;
    }

    public List<Map<String, Object>> getEventsResponse() {
        return eventsResponse;
    }

    public void setEventsResponse(List<Map<String, Object>> eventsResponse) {
        this.eventsResponse = eventsResponse;
    }

    public List<AttemptDto> getEvents() {
        return events;
    }

    public void setEvents(List<AttemptDto> events) {
        this.events = events;
    }
}