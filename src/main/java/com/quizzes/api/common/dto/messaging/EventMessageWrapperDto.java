package com.quizzes.api.common.dto.messaging;

import java.util.UUID;

public class EventMessageWrapperDto {

    private UUID contextId;
    private UUID profileId;
    private String eventName;
    private Object eventBody;

    public UUID getContextId() {
        return contextId;
    }

    public void setContextId(UUID contextId) {
        this.contextId = contextId;
    }

    public UUID getProfileId() {
        return profileId;
    }

    public void setProfileId(UUID profileId) {
        this.profileId = profileId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getEventBody() {
        return eventBody;
    }

    public void setEventBody(Object eventBody) {
        this.eventBody = eventBody;
    }

}
