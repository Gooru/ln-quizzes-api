package com.quizzes.api.core.services;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AttemptGetResponseDto;
import com.quizzes.api.core.dtos.ContextAttemptsResponseDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileAttemptsResponseDto;
import com.quizzes.api.core.dtos.TaxonomySummaryDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextProfileEventEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    @Autowired
    private ContextProfileEventService contextProfileEventService;

    @Autowired
    private ContextService contextService;

    @Autowired
    private Gson gson;

    public ContextAttemptsResponseDto getCurrentAttemptByProfile(UUID contextId, UUID ownerId, String token) {
        ContextEntity context = contextService.findCreatedContext(contextId, ownerId, token);
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
                    .map(studentEventEntity -> {
                        PostResponseResourceDto evenData = gson.fromJson(studentEventEntity.getEventData(),
                                PostResponseResourceDto.class);
                        evenData.setResourceId(studentEventEntity.getResourceId());
                        evenData.setIsResource(null);
                        return evenData;
                    }).collect(Collectors.toList()));

            profileEvent.setEventSummary(gson.fromJson(anyAssigneeEventEntity.getEventsSummary(),
                    EventSummaryDataDto.class));
            profileEvent.setTaxonomySummary(gson.fromJson(anyAssigneeEventEntity.getTaxonomySummary(),
                    new TypeToken<ArrayList<TaxonomySummaryDto>>(){}.getType()));

            return profileEvent;

        }).collect(Collectors.toList());
    }

    /**
     * Get the Attempt information
     * If the Collection in the {@link Context} has no Resources then Event Data will be empty ("null")
     *
     * @param attemptId the Attempt ID
     * @param profileId the Owner or the Assignee ID, it works for both
     * @return
     */
    public AttemptGetResponseDto getAttempt(UUID attemptId, UUID profileId) {
        List<ContextProfileEventEntity> contextProfileEvents =
                contextProfileEventService.findByContextProfileIdAndProfileId(attemptId, profileId);
        if (contextProfileEvents.isEmpty()) {
            throw new ContentNotFoundException("Attempt ID: " + attemptId + " not found for Profile ID: " + profileId);
        }
        return mapContextProfileEventListToAttempt(contextProfileEvents);
    }

    private AttemptGetResponseDto mapContextProfileEventListToAttempt(List<ContextProfileEventEntity> contextProfileEvents) {
        AttemptGetResponseDto result = new AttemptGetResponseDto();

        ContextProfileEventEntity firstEvent = contextProfileEvents.get(0);
        result.setAttemptId(firstEvent.getContextProfileId());
        result.setContextId(firstEvent.getContextId());
        result.setCollectionId(firstEvent.getCollectionId());
        result.setProfileId(firstEvent.getProfileId());
        result.setCurrentResourceId(firstEvent.getCurrentResourceId());
        result.setCreatedDate(firstEvent.getCreatedAt());
        result.setUpdatedDate(firstEvent.getUpdatedAt());
        result.setEventSummary(gson.fromJson(firstEvent.getEventsSummary(), EventSummaryDataDto.class));
        result.setTaxonomySummary(gson.fromJson(firstEvent.getTaxonomySummary(), List.class));
        List<PostResponseResourceDto> events = contextProfileEvents.stream().
                filter(contextProfileEvent -> contextProfileEvent.getEventData() != null).
                map(contextProfileEvent -> {
                    PostResponseResourceDto eventData =
                            gson.fromJson(contextProfileEvent.getEventData(), PostResponseResourceDto.class);
                    eventData.setResourceId(contextProfileEvent.getResourceId());
                    eventData.setIsResource(null);
                    return eventData;
                }).collect(Collectors.toList());
        result.setEvents(events);
        return result;
    }

}
