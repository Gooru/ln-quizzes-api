package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ContextEventsResponseDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ProfileEventResponseDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.StartContextEventMessageDto;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ContextEventService {

    @Autowired
    ContextProfileService contextProfileService;

    @Autowired
    ContextProfileEventService contextProfileEventService;

    @Autowired
    ContextService contextService;

    @Autowired
    ContextRepository contextRepository;

    @Autowired
    CurrentContextProfileService currentContextProfileService;

    @Autowired
    ActiveMQClientService activeMQClientService;

    @Autowired
    Gson gson;

    public StartContextEventResponseDto processStartContextEvent(UUID contextId, UUID profileId) {
        Context context = contextService.findByIdAndAssigneeId(contextId, profileId);

        try {
            CurrentContextProfile currentContextProfile =
                    currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
            ContextProfile contextProfile = contextProfileService.findById(currentContextProfile.getContextProfileId());
            if (contextProfile.getIsComplete()) {
                return createStartContextEvent(context, profileId);
            } else {
                return resumeStartContextEvent(context, contextProfile);
            }
        } catch (ContentNotFoundException cne) {
            return createStartContextEvent(context, profileId);
        }
    }

    public void processOnResourceEvent(UUID contextId, UUID profileId, UUID resourceId,
                                       OnResourceEventPostRequestDto body) {
        // TODO Replace this logic
        /*
        Context context = contextService.findByIdAndAssigneeId(contextId, profileId);
        CurrentContextProfile currentContextProfile =
                currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);

        PostRequestResourceDto resourceDto = body.getPreviousResource();
        resourceDto.setIsSkipped(resourceDto.getAnswer().isEmpty());

        List<Resource> collectionResources = resourceService.findByCollectionId(context.getCollectionId());
        Resource currentResource = findResourceInContext(collectionResources, resourceId, contextId);
        Resource previousResource = findResourceInContext(collectionResources, resourceDto.getResourceId(), contextId);

        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(currentContextProfile.getContextProfileId());
        ContextProfileEvent contextProfileEvent = contextProfileEvents.stream()
                .filter(event -> event.getResourceId().equals(previousResource.getId())).findFirst().orElse(null);
        resourceService.findById(resourceDto.getResourceId());

        if (contextProfileEvent == null) {
            contextProfileEvent = createContextProfileEvent(currentContextProfile.getContextProfileId(),
                    previousResource.getId());
            if (!resourceDto.getIsSkipped()) {
                resourceDto.setScore(calculateScore(previousResource, resourceDto.getAnswer()));
            }
            contextProfileEvents.add(contextProfileEvent);
        } else {
            resourceDto = updateExistingResourceDto(contextProfileEvent, previousResource, resourceDto);
        }

        contextProfileEvent.setEventData(gson.toJson(resourceDto));

        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, false);
        ContextProfile contextProfile = contextProfileService.findById(currentContextProfile.getContextProfileId());
        contextProfile.setCurrentResourceId(currentResource.getId());
        contextProfile.setEventSummaryData(gson.toJson(eventSummary));

        doOnResourceEventTransaction(contextProfile, contextProfileEvent);
        sendOnResourceEventMessage(contextProfile, resourceDto, eventSummary);
        */
    }


    public void processFinishContextEvent(UUID contextId, UUID profileId) {
        // TODO Replace this logic
        /*
        CurrentContextProfile currentContextProfile =
                currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
        ContextProfile contextProfile = contextProfileService.findById(currentContextProfile.getContextProfileId());

        // If Context Profile is complete then the process is halted
        if (contextProfile.getIsComplete()) {
            return;
        }

        // TODO We can make an improvement to retrieve the resources by Context ID
        Context context = contextService.findById(contextId);
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getId());
        List<UUID> contextProfileEventResourceIds = contextProfileEvents.stream()
                .map(ContextProfileEvent::getResourceId).collect(Collectors.toList());
        List<Resource> resources = resourceService.findByCollectionId(context.getCollectionId());
        List<Resource> resourcesToCreate = resources.stream()
                .filter(resource -> !contextProfileEventResourceIds.contains(resource.getId()))
                .collect(Collectors.toList());
        List<ContextProfileEvent> contextProfileEventsToCreate =
                createSkippedContextProfileEvents(contextProfile.getId(), resourcesToCreate);
        // Fill the list of Context Profile Events to calculate the summary
        contextProfileEvents.addAll(contextProfileEventsToCreate);
        // Prepare Context Profile to be marked as complete
        contextProfile.setIsComplete(true);
        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, true);
        contextProfile.setEventSummaryData(gson.toJson(eventSummary));

        doFinishContextEventTransaction(contextProfile, currentContextProfile, contextProfileEventsToCreate);

        sendFinishContextEventMessage(contextId, profileId, eventSummary);
        */
    }

    public ContextEventsResponseDto getContextEvents(UUID contextId, UUID ownerId) {
        Context context = contextService.findByIdAndOwnerId(contextId, ownerId);
        Map<UUID, List<AssigneeEventEntity>> assigneeEvents =
                contextProfileEventService.findByContextId(contextId);
        ContextEventsResponseDto response = new ContextEventsResponseDto();
        response.setContextId(contextId);

        CollectionDto collection = new CollectionDto();
        collection.setId(context.getCollectionId().toString());
        response.setCollection(collection);

        List<ProfileEventResponseDto> profileEvents = assigneeEvents.entrySet().stream().map(entity -> {
            List<AssigneeEventEntity> assigneeEventEntityList = entity.getValue();
            ProfileEventResponseDto profileEvent = new ProfileEventResponseDto();
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
            profileEvent.setContextProfileSummary(eventSummaryDataDto);

            return profileEvent;

        }).collect(Collectors.toList());
        response.setProfileEvents(profileEvents);
        return response;
    }

    @Transactional
    public void doCreateStartContextEventTransaction(final ContextProfile contextProfile) {
        ContextProfile savedContextProfile = contextProfileService.save(contextProfile);
        CurrentContextProfile currentContextProfile = createCurrentContextProfile(savedContextProfile);
        currentContextProfileService.delete(currentContextProfile);
        currentContextProfileService.create(currentContextProfile);
    }

    @Transactional
    public void doOnResourceEventTransaction(ContextProfile contextProfile, ContextProfileEvent contextProfileEvent) {
        contextProfileService.save(contextProfile);
        contextProfileEventService.save(contextProfileEvent);
    }

    @Transactional
    public void doFinishContextEventTransaction(ContextProfile contextProfile,
                                                CurrentContextProfile currentContextProfile,
                                                List<ContextProfileEvent> eventsToCreate) {
        contextProfileService.save(contextProfile);
        eventsToCreate.stream().forEach(event -> contextProfileEventService.save(event));
    }

    private StartContextEventResponseDto createStartContextEvent(final Context context, UUID profileId) {
        ContextProfile newContextProfile = createContextProfile(context.getId(), profileId);
        doCreateStartContextEventTransaction(newContextProfile);
        sendStartEventMessage(newContextProfile, true);
        return prepareStartContextEventResponse(context, newContextProfile, new ArrayList<>());
    }

    private StartContextEventResponseDto resumeStartContextEvent(final Context context,
                                                                 final ContextProfile contextProfile) {
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getId());
        sendStartEventMessage(contextProfile, false);
        return prepareStartContextEventResponse(context, contextProfile, contextProfileEvents);
    }

    private CurrentContextProfile createCurrentContextProfile(final ContextProfile contextProfile) {
        CurrentContextProfile currentContextProfile = new CurrentContextProfile();
        currentContextProfile.setContextId(contextProfile.getContextId());
        currentContextProfile.setProfileId(contextProfile.getProfileId());
        currentContextProfile.setContextProfileId(contextProfile.getId());
        return currentContextProfile;
    }

    /*
    private List<ContextProfileEvent> createSkippedContextProfileEvents(UUID contextProfileId,
                                                                        List<Resource> resources) {
        return resources.stream()
                .map(resource -> {
                    ContextProfileEvent contextProfileEvent = new ContextProfileEvent();
                    contextProfileEvent.setContextProfileId(contextProfileId);
                    contextProfileEvent.setResourceId(resource.getId());
                    contextProfileEvent.setEventData(gson.toJson(createSkippedEventData(resource.getId())));
                    return contextProfileEvent;
                }).collect(Collectors.toList());
    }
    */

    private PostResponseResourceDto createSkippedEventData(UUID resourceId) {
        PostResponseResourceDto evenData = new PostResponseResourceDto();
        evenData.setResourceId(resourceId);
        evenData.setScore(0);
        evenData.setTimeSpent(0);
        evenData.setIsSkipped(true);
        evenData.setReaction(0);
        evenData.setAnswer(Collections.emptyList());
        return evenData;
    }

    private StartContextEventResponseDto prepareStartContextEventResponse(Context context,
                                                                          ContextProfile contextProfile,
                                                                          List<ContextProfileEvent> contextProfileEvents) {
        StartContextEventResponseDto response = new StartContextEventResponseDto();
        response.setId(context.getId());
        //response.setCurrentResourceId(contextProfile.getCurrentResourceId());
        response.setCollection(new CollectionDto(context.getCollectionId().toString()));
        response.setEvents(contextProfileEvents.stream()
                .map(event -> gson.fromJson(event.getEventData(), PostResponseResourceDto.class))
                .collect(Collectors.toList()));
        return response;
    }

    private void sendStartEventMessage(ContextProfile contextProfile, boolean isNewAttempt) {
        StartContextEventMessageDto startEventMessage = new StartContextEventMessageDto();
        startEventMessage.setIsNewAttempt(isNewAttempt);
        //startEventMessage.setCurrentResourceId(contextProfile.getCurrentResourceId());
        activeMQClientService.sendStartContextEventMessage(contextProfile.getContextId(),
                contextProfile.getProfileId(), startEventMessage);
    }

    /*
    private void sendOnResourceEventMessage(ContextProfile contextProfile,
                                            PostRequestResourceDto previousResource,
                                            EventSummaryDataDto eventSummary) {
        OnResourceEventMessageDto onResourceEventMessage = new OnResourceEventMessageDto();
        onResourceEventMessage.setCurrentResourceId(contextProfile.getCurrentResourceId());
        onResourceEventMessage.setPreviousResource(previousResource);
        onResourceEventMessage.setEventSummary(eventSummary);
        activeMQClientService.sendOnResourceEventMessage(contextProfile.getContextId(), contextProfile.getProfileId(),
                onResourceEventMessage);
    }
    */

    private void sendFinishContextEventMessage(UUID contextId, UUID profileId, EventSummaryDataDto eventSummary) {
        FinishContextEventMessageDto finishContextEventMessage = new FinishContextEventMessageDto();
        finishContextEventMessage.setEventSummary(eventSummary);
        activeMQClientService.sendFinishContextEventMessage(contextId, profileId, finishContextEventMessage);
    }

    private ContextProfile createContextProfile(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setContextId(contextId);
        contextProfile.setProfileId(profileId);
        contextProfile.setIsComplete(false);
        contextProfile.setEventSummaryData(gson.toJson(calculateEventSummary(Collections.EMPTY_LIST, false)));
        return contextProfile;
    }

    /*
    private ContextProfileEvent createContextProfileEvent(UUID contextProfileId, UUID resourceId) {
        ContextProfileEvent event = new ContextProfileEvent();
        event.setContextProfileId(contextProfileId);
        event.setResourceId(resourceId);
        return event;
    }
    */

    /*
    private Resource findFirstResourceByContextId(UUID contextId) {
        return null;
        //return resourceService.findFirstByContextIdOrderBySequence(contextId);
    }
    */

    private int calculateScoreByQuestionType(String questionType, List<AnswerDto> userAnswers,
                                             List<AnswerDto> correctAnswers) {
        QuestionTypeEnum enumType = QuestionTypeEnum.fromString(questionType);
        switch (enumType) {
            case TrueFalse:
            case SingleChoice:
                return calculateScoreForSimpleOption(userAnswers.get(0).getValue(), correctAnswers.get(0).getValue());
            case DragAndDrop:
                return calculateScoreForDragAndDrop(userAnswers, correctAnswers);
            case MultipleChoice:
            case MultipleChoiceImage:
            case MultipleChoiceText:
                return calculateScoreForMultipleChoice(userAnswers, correctAnswers);
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

    /**
     * Drag and Drop method
     *
     * @param userAnswers    Answers provided by the user
     * @param correctAnswers Correct answers for the question
     * @return the score
     */
    private int calculateScoreForDragAndDrop(List<AnswerDto> userAnswers, List<AnswerDto> correctAnswers) {
        if (userAnswers.size() < correctAnswers.size()) {
            return 0;
        }
        boolean isAnswerCorrect =
                IntStream.range(0, correctAnswers.size() - 1)
                        .allMatch(i -> correctAnswers.get(i).getValue().equals(userAnswers.get(i).getValue()));

        return isAnswerCorrect ? 100 : 0;
    }

    /**
     * Multiple Answer method compares the answers with the correct answer ignoring the order
     * Works for multiple_choice, multiple_choice_image and multiple_choice_text
     *
     * @param userAnswers    Answers provided by the user
     * @param correctAnswers Correct answers for the question
     * @return the score
     */
    private int calculateScoreForMultipleChoice(List<AnswerDto> userAnswers, List<AnswerDto> correctAnswers) {
        if (userAnswers.size() != correctAnswers.size()) {
            return 0;
        }

        boolean result = correctAnswers.stream().map(AnswerDto::getValue).collect(Collectors.toList())
                .containsAll(userAnswers.stream().map(AnswerDto::getValue).collect(Collectors.toList()));
        return result ? 100 : 0;
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
        result.setAverageReaction(totalReactions > 0 ? (short) Math.round(sumReaction / totalReactions) : 0);
        result.setAverageScore(totalAnswered > 0 ? (short) Math.round(sumScore / totalAnswered) : 0);
        result.setTotalCorrect(totalCorrect);
        result.setTotalAnswered(totalAnswered);

        return result;
    }

    /*
    private PostRequestResourceDto updateExistingResourceDto(ContextProfileEvent contextProfileEvent,
                                                             Resource resourceInfo,
                                                             PostRequestResourceDto resource) {
        PostRequestResourceDto oldResource =
                gson.fromJson(contextProfileEvent.getEventData(), PostRequestResourceDto.class);

        resource.setTimeSpent(resource.getTimeSpent() + oldResource.getTimeSpent());

        if (!resource.getIsSkipped()) {
            resource.setScore(calculateScore(resourceInfo, resource.getAnswer()));
        } else if (!oldResource.getIsSkipped()) {
            oldResource.setTimeSpent(resource.getTimeSpent());
            oldResource.setReaction(resource.getReaction());
            return oldResource;
        }

        return resource;
    }

    private int calculateScore(Resource resourceInfo, List<AnswerDto> answer) {
        QuestionDataDto question = gson.fromJson(resourceInfo.getResourceData(), QuestionDataDto.class);
        return calculateScoreByQuestionType(question.getType(), answer,
                question.getCorrectAnswer());
    }

    private Resource findResourceInContext(List<Resource> resources, UUID resourceId, UUID contextId) {
        Resource resourceInList = resources.stream()
                .filter(r -> r.getId().equals(resourceId)).findFirst().orElse(null);
        if (resourceInList == null) {
            throw new ContentNotFoundException("Resource ID: " + resourceId + " is not part of " +
                    "the Context ID: " + contextId);
        }
        return resourceInList;
    }
    */

}
