package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AttemptGetResponseDto;
import com.quizzes.api.core.dtos.AttemptIdsResponseDto;
import com.quizzes.api.core.dtos.ContextAttemptsResponseDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileAttemptsResponseDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextProfileEventEntity;
import com.quizzes.api.core.services.content.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    @Autowired
    ContextProfileEventService contextProfileEventService;

    @Autowired
    ContextService contextService;

    @Autowired
    ContextProfileService contextProfileService;

    @Autowired
    CollectionService collectionService;

    @Autowired
    Gson gson;

    public ContextAttemptsResponseDto getCurrentAttemptByProfile(UUID contextId, UUID ownerId) {
        ContextEntity context = contextService.findCreatedContext(contextId, ownerId);
        Map<UUID, List<AssigneeEventEntity>> assigneeEvents = contextProfileEventService.findByContextId(contextId);

        List<ProfileAttemptsResponseDto> profileEvents = mapProfileAttempts(assigneeEvents);

        ContextAttemptsResponseDto response = new ContextAttemptsResponseDto();
        response.setContextId(contextId);
        response.setCollectionId(context.getCollectionId());
        response.setProfileAttempts(profileEvents);
        return response;
    }

    private List<ProfileAttemptsResponseDto> mapProfileAttempts(Map<UUID, List<AssigneeEventEntity>> assigneeEvents) {
        return assigneeEvents.entrySet().stream().map(entity -> {
            List<AssigneeEventEntity> assigneeEventEntityList = entity.getValue();
            ProfileAttemptsResponseDto profileEvent = new ProfileAttemptsResponseDto();
            profileEvent.setProfileId(entity.getKey());

            AssigneeEventEntity anyAssigneeEventEntity = assigneeEventEntityList.get(0);
            if (!assigneeEventEntityList.isEmpty()) {
                profileEvent.setCurrentResourceId(anyAssigneeEventEntity.getCurrentResourceId());
                profileEvent.setIsComplete(anyAssigneeEventEntity.getIsComplete());
            }

            profileEvent.setEvents(assigneeEventEntityList.stream()
                    .filter(studentEventEntity -> studentEventEntity.getEventData() != null)
                    .map(studentEventEntity -> gson.fromJson(studentEventEntity.getEventData(),
                            PostResponseResourceDto.class)).collect(Collectors.toList()));

            EventSummaryDataDto eventSummaryDataDto =
                    gson.fromJson(anyAssigneeEventEntity.getEventsSummary(), EventSummaryDataDto.class);
            profileEvent.setEventSummary(eventSummaryDataDto);

            return profileEvent;

        }).collect(Collectors.toList());
    }

    public AttemptGetResponseDto getAttempt(UUID attemptId, UUID profileId) {

        List<ContextProfileEventEntity> contextProfileEvents =
                contextProfileEventService.findByContextProfileIdAndProfileId(attemptId, profileId);

        if (contextProfileEvents.isEmpty()) {
            throw new ContentNotFoundException("Attempt: " + attemptId + " not found for profile: " + profileId);
        }

        ContextProfileEventEntity firstEvent = contextProfileEvents.get(0);
        AttemptGetResponseDto result = new AttemptGetResponseDto();
        result.setAttemptId(firstEvent.getContextProfileId());
        result.setContextId(firstEvent.getContextId());
        result.setCollectionId(firstEvent.getCollectionId());
        result.setProfileId(firstEvent.getProfileId());
        result.setCurrentResourceId(firstEvent.getCurrentResourceId());
        result.setEventSummary(gson.fromJson(firstEvent.getEventsSummary(), EventSummaryDataDto.class));
        List<PostResponseResourceDto> events = contextProfileEvents.stream().
                filter(contextProfileEvent -> contextProfileEvent.getEventData() != null).
                map(contextProfileEvent -> {
                    PostResponseResourceDto event =
                            gson.fromJson(contextProfileEvent.getEventData(), PostResponseResourceDto.class);
                    event.setResourceId(contextProfileEvent.getResourceId());
                    return event;
                }).collect(Collectors.toList());
        result.setEvents(events);

        return result;
    }
}
