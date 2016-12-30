package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.AnswerDto;
import com.quizzes.api.common.dto.ContextEventsResponseDto;
import com.quizzes.api.common.dto.EventSummaryDataDto;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostRequestResourceDto;
import com.quizzes.api.common.dto.PostResponseResourceDto;
import com.quizzes.api.common.dto.ProfileEventResponseDto;
import com.quizzes.api.common.dto.QuestionDataDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.messaging.FinishContextEventMessageDto;
import com.quizzes.api.common.dto.messaging.OnResourceEventMessageDto;
import com.quizzes.api.common.dto.messaging.StartContextEventMessageDto;
import com.quizzes.api.common.enums.QuestionTypeEnum;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.common.model.entities.AssigneeEventEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.messaging.ActiveMQClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ContextEventService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContextProfileService contextProfileService;

    @Autowired
    ContextProfileEventService contextProfileEventService;

    @Autowired
    ContextService contextService;

    @Autowired
    ContextRepository contextRepository;

    @Autowired
    ResourceService resourceService;

    @Autowired
    ProfileService profileService;

    @Autowired
    ActiveMQClientService activeMQClientService;

    @Autowired
    Gson gson;

    public StartContextEventResponseDto processStartContextEvent(UUID contextId, UUID profileId) {
        Context context = contextService.findById(contextId);
        if (context == null) {
            throw new ContentNotFoundException("Not Found Context Id: " + contextId);
        }

        ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);
        boolean isNewAttempt = contextProfile == null || contextProfile.getIsComplete();

        if (contextProfile == null) {
            contextProfile = createNewContextProfile(contextId, profileId);
            doStartContextEventTransaction(contextProfile);
        } else if (contextProfile.getIsComplete()) {
            contextProfile = createNewContextProfile(contextId, profileId);
            contextProfile.setIsComplete(true);
            doStartContextEventTransaction(contextProfile);
        }

        broadcastStartEventMessage(contextProfile, isNewAttempt);

        return prepareStartContextEventResponse(context, contextProfile, isNewAttempt);
    }

    public void processOnResourceEvent(UUID contextId, UUID profileId, UUID resourceId,
                                       OnResourceEventPostRequestDto body) {
        PostRequestResourceDto resourceDto = body.getPreviousResource();

        ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);
        if (contextProfile == null) {
            throw new ContentNotFoundException("Not Found ContextProfile for Context Id: " + contextId
                    + " and Profile Id: " + profileId);
        }

        Resource resource = resourceService.findById(resourceId);
        if (resource == null) {
            throw new ContentNotFoundException("Not Found Resource Id: " + resourceId);
        }

        Resource previousResource = resourceService.findById(resourceDto.getResourceId());
        if (previousResource == null) {
            throw new ContentNotFoundException("Not Found Previous Resource Id: " + resourceDto.getResourceId());
        }

        QuestionDataDto previousResourceData =
                gson.fromJson(previousResource.getResourceData(), QuestionDataDto.class);

        // Calculates provided answer score
        if (!resourceDto.getAnswer().isEmpty()) {
            resourceDto.setIsSkipped(false);
            resourceDto.setScore(calculateScoreByQuestionType(previousResourceData.getType(), resourceDto.getAnswer(),
                    previousResourceData.getCorrectAnswer()));
        }

        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getId());
        ContextProfileEvent contextProfileEvent = contextProfileEvents.stream()
                .filter(event -> event.getResourceId().equals(previousResource.getId()))
                .findFirst()
                .orElse(null);
        if (contextProfileEvent == null) {
            contextProfileEvent = createNewContextProfileEvent(contextProfile.getId(), previousResource.getId());
            contextProfileEvents.add(contextProfileEvent);
        }
        contextProfileEvent.setEventData(gson.toJson(resourceDto));

        EventSummaryDataDto eventSummary =  calculateEventSummary(contextProfileEvents, false);
        contextProfile.setCurrentResourceId(resource.getId());
        contextProfile.setEventSummaryData(gson.toJson(eventSummary));
        doOnResourceEventTransaction(contextProfile, contextProfileEvent);

        broadcastOnResourceEventMessage(contextProfile, resourceDto, eventSummary);
    }

    public void processFinishContextEvent(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);
        if (contextProfile == null) {
            throw new ContentNotFoundException("Not Found ContextProfile for Context Id: " + contextId
                    + " and Profile Id: " + profileId);
        }

        if (!contextProfile.getIsComplete()) {
            contextProfile.setIsComplete(true);
            doFinishContextEventTransaction(contextProfile);
        }

        broadcastFinishContextEventMessage(contextProfile);
    }

    public ContextEventsResponseDto getContextEvents(UUID contextId) {
        try {
            Map<UUID, List<AssigneeEventEntity>> assigneeEvents =
                    contextProfileEventService.findByContextId(contextId);
            ContextEventsResponseDto response = new ContextEventsResponseDto();
            response.setContextId(contextId);

            Context context = contextService.findById(contextId);
            CollectionDto collection = new CollectionDto();
            collection.setId(context.getCollectionId().toString());
            response.setCollection(collection);

            List<ProfileEventResponseDto> profileEvents = assigneeEvents.entrySet().stream().map(entity -> {
                List<AssigneeEventEntity> assigneeEventEntityList = entity.getValue();
                ProfileEventResponseDto profileEvent = new ProfileEventResponseDto();
                profileEvent.setProfileId(entity.getKey());
                if (!entity.getValue().isEmpty()) {
                    profileEvent.setCurrentResourceId(entity.getValue().get(0).getCurrentResourceId());
                }

                profileEvent.setEvents(assigneeEventEntityList.stream()
                        .filter(studentEventEntity -> studentEventEntity.getEventData() != null)
                        .map(studentEventEntity -> gson.fromJson(studentEventEntity.getEventData(),
                                PostResponseResourceDto.class)).collect(Collectors.toList()));
                return profileEvent;

            }).collect(Collectors.toList());
            response.setProfileEvents(profileEvents);
            return response;
        } catch (Exception e) {
            logger.error("We could not get the events for context " + contextId + ".", e);
            throw new InternalServerException("We could not get the events for context " + contextId + ".", e);
        }
    }

    @Transactional
    public ContextProfile doStartContextEventTransaction(ContextProfile contextProfile) {
        if (contextProfile.getIsComplete()) {
            contextProfileEventService.deleteByContextProfileId(contextProfile.getId());
            contextProfile.setIsComplete(false);
        }
        return contextProfileService.save(contextProfile);
    }

    @Transactional
    public void doOnResourceEventTransaction(ContextProfile contextProfile, ContextProfileEvent contextProfileEvent) {
        contextProfileService.save(contextProfile);
        contextProfileEventService.save(contextProfileEvent);
    }

    @Transactional
    public void doFinishContextEventTransaction(ContextProfile contextProfile) {
        contextProfileService.save(contextProfile);
    }

    private StartContextEventResponseDto prepareStartContextEventResponse(Context context,
                                                                          ContextProfile contextProfile,
                                                                          boolean isNewAttempt) {
        StartContextEventResponseDto response = new StartContextEventResponseDto();
        response.setId(context.getId());
        response.setCurrentResourceId(contextProfile.getCurrentResourceId());

        CollectionDto collection = new CollectionDto();
        collection.setId(context.getCollectionId().toString());
        response.setCollection(collection);

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        if (!isNewAttempt) {
            contextProfileEvents = contextProfileEventService.findByContextProfileId(contextProfile.getId());
        }
        response.setEvents(contextProfileEvents.stream()
                .map(event -> gson.fromJson(event.getEventData(), PostResponseResourceDto.class))
                .collect(Collectors.toList()));

        return response;
    }

    private void broadcastStartEventMessage(ContextProfile contextProfile, boolean isNewAttempt) {
        StartContextEventMessageDto startEventMessage = new StartContextEventMessageDto();
        startEventMessage.setIsNewAttempt(isNewAttempt);
        startEventMessage.setCurrentResourceId(contextProfile.getCurrentResourceId());
        activeMQClientService.sendStartContextEventMessage(contextProfile.getContextId(),
                contextProfile.getProfileId(), startEventMessage);
    }

    private void broadcastOnResourceEventMessage(ContextProfile contextProfile,
                                                 PostRequestResourceDto previousResource,
                                                 EventSummaryDataDto eventSummary) {
        OnResourceEventMessageDto onResourceEventMessage = new OnResourceEventMessageDto();
        onResourceEventMessage.setCurrentResourceId(contextProfile.getCurrentResourceId());
        onResourceEventMessage.setPreviousResource(previousResource);
        onResourceEventMessage.setEventSummary(eventSummary);
        activeMQClientService.sendOnResourceEventMessage(contextProfile.getContextId(), contextProfile.getProfileId(),
                onResourceEventMessage);
    }

    private void broadcastFinishContextEventMessage(ContextProfile contextProfile) {
        FinishContextEventMessageDto finishContextEventMessage = new FinishContextEventMessageDto();
        finishContextEventMessage.setEventSummary(null);
        activeMQClientService.sendFinishContextEventMessage(contextProfile.getContextId(),
                contextProfile.getProfileId(), finishContextEventMessage);
    }

    private ContextProfile createNewContextProfile(UUID contextId, UUID profileId) {
        Resource firstResource = findFirstResourceByContextId(contextId);
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setContextId(contextId);
        contextProfile.setProfileId(profileId);
        contextProfile.setCurrentResourceId(firstResource.getId());
        contextProfile.setIsComplete(false);
        return contextProfile;
    }

    private ContextProfileEvent createNewContextProfileEvent(UUID contextProfileId, UUID resourceId) {
        ContextProfileEvent event = new ContextProfileEvent();
        event.setContextProfileId(contextProfileId);
        event.setResourceId(resourceId);
        return event;
    }

    private Resource findFirstResourceByContextId(UUID contextId) {
        return resourceService.findFirstByContextIdOrderBySequence(contextId);
    }

    private int calculateScoreByQuestionType(String questionType, List<AnswerDto> userAnswers,
                                             List<AnswerDto> correctAnswers) {
        QuestionTypeEnum enumType = QuestionTypeEnum.fromString(questionType);
        switch (enumType) {
            case TrueFalse:
            case SingleChoice:
                return calculateScoreForSimpleOption(userAnswers.get(0).getValue(), correctAnswers.get(0).getValue());
            default:
                return 0;
            //TODO: Implement the logic for the other question types
        }
    }

    /**
     * Simple option method works for true_false and single_option question types
     *
     * @param userAnswer    Answer provided by the user
     * @param correctAnswer Correct answer for the question
     * @return the score
     */
    private int calculateScoreForSimpleOption(String userAnswer, String correctAnswer) {
        return userAnswer.equalsIgnoreCase(correctAnswer) ? 100 : 0;
    }

    private EventSummaryDataDto calculateEventSummary(List<ContextProfileEvent> contextProfileEvents,
                                                      boolean calculateSkipped) {
        EventSummaryDataDto result = new EventSummaryDataDto();
        long totalTimeSpent = 0;
        short sumReaction = 0;
        short sumScore = 0;
        short totalReactions = 0;
        short totalCorrect = 0;
        short totalAnswered = 0;

        for (ContextProfileEvent contextProfileEvent : contextProfileEvents) {
            PostRequestResourceDto eventDataDto =
                    gson.fromJson(contextProfileEvent.getEventData(), PostRequestResourceDto.class);
            totalTimeSpent += eventDataDto.getTimeSpent();

            if (eventDataDto.getReaction() > 0) {
                sumReaction += eventDataDto.getReaction();
                totalReactions++;
            }

            if (calculateSkipped || !eventDataDto.getIsSkipped()) {
                sumScore += eventDataDto.getScore();
                totalCorrect += eventDataDto.getScore() == 100 ? 1 : 0;
                totalAnswered++;
            }
        }

        result.setTotalTimeSpent(totalTimeSpent);
        result.setAverageReaction((short) Math.round(sumReaction / totalReactions));
        result.setAverageScore((short) Math.round(sumScore / totalAnswered));
        result.setTotalCorrect(totalCorrect);
        result.setTotalAnswered(totalAnswered);

        return result;
    }

}
