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
import com.quizzes.api.core.dtos.content.ResourceContentDto;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.StartContextEventMessageDto;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.model.entities.AssigneeEventEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextProfileWithContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.rest.clients.AssessmentRestClient;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
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
    CollectionRestClient collectionRestClient;

    @Autowired
    AssessmentRestClient assessmentRestClient;

    @Autowired
    Gson gson;

    public StartContextEventResponseDto processStartContextEvent(UUID contextId, UUID profileId) {
        ContextProfileWithContextEntity entity = contextService.findProfileIdInContext(contextId, profileId);
        try {
            currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
            if (entity.getIsComplete()) {
                return createContextProfile(entity);
            } else {
                return resumeStartContextEvent(entity);
            }
        } catch (ContentNotFoundException cne) {
            return createCurrentContextProfile(entity);
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


    public void processFinishContextEvent(UUID contextId, UUID profileId, String token) {
        CurrentContextProfile currentContextProfile =
                currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
        ContextProfile contextProfile = contextProfileService.findById(currentContextProfile.getContextProfileId());

        if (contextProfile.getIsComplete()) {
            return;
        }

        Context context = contextService.findById(contextId);
        finishContextEvent(context, contextProfile, currentContextProfile, token);
    }

    private void finishContextEvent(Context context, ContextProfile contextProfile,
                                    CurrentContextProfile currentContextProfile, String token) {
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getId());

        List<ResourceContentDto> resources = getCollectionResources(context.getCollectionId(),
                context.getIsCollection(), token);
        List<ResourceContentDto> resourcesToCreate = getResourcesToCreate(contextProfileEvents, resources);

        List<ContextProfileEvent> contextProfileEventsToCreate =
                createSkippedContextProfileEvents(contextProfile.getId(), resourcesToCreate);

        // Fill the list of Context Profile Events to calculate the summary
        contextProfileEvents.addAll(contextProfileEventsToCreate);

        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, true);
        contextProfile.setEventSummaryData(gson.toJson(eventSummary));
        contextProfile.setIsComplete(true);

        doFinishContextEventTransaction(contextProfile, currentContextProfile, contextProfileEventsToCreate);

        sendFinishContextEventMessage(context.getId(), contextProfile.getProfileId(), eventSummary);
    }

    public ContextEventsResponseDto getContextEvents(UUID contextId, UUID ownerId) {
        ContextEntity context = contextService.findCreatedContext(contextId, ownerId);
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
    public void doCreateContextProfileTransaction(final ContextProfile contextProfile) {
        ContextProfile savedContextProfile = contextProfileService.save(contextProfile);
        CurrentContextProfile currentContextProfile = createCurrentContextProfileObject(
                savedContextProfile.getContextId(), contextProfile.getProfileId(), savedContextProfile.getId());
        doCurrentContextEventTransaction(currentContextProfile);
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
        currentContextProfileService.delete(currentContextProfile);
        contextProfileService.save(contextProfile);
        eventsToCreate.stream().forEach(event -> contextProfileEventService.save(event));
    }

    private StartContextEventResponseDto createCurrentContextProfile(ContextProfileWithContextEntity entity) {
        CurrentContextProfile currentContextProfile = createCurrentContextProfileObject(
                entity.getContextId(), entity.getProfileId(), entity.getContextProfileId());
        doCurrentContextEventTransaction(currentContextProfile);
        return processStartContext(entity, new ArrayList<>());
    }

    private StartContextEventResponseDto processStartContext(ContextProfileWithContextEntity entity,
                                                             List<ContextProfileEvent> contextProfileEvents) {
        sendStartEventMessage(entity.getContextId(), entity.getProfileId(), entity.getCurrentResourceId(), true);
        return prepareStartContextEventResponse(entity.getContextId(), entity.getCurrentResourceId(),
                entity.getCollectionId(), contextProfileEvents);
    }

    private StartContextEventResponseDto createContextProfile(ContextProfileWithContextEntity entity) {
        ContextProfile contextProfile = createContextProfileObject(entity.getContextId(), entity.getProfileId());
        doCreateContextProfileTransaction(contextProfile);
        return processStartContext(entity, new ArrayList<>());
    }

    private StartContextEventResponseDto resumeStartContextEvent(ContextProfileWithContextEntity entity) {
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(entity.getContextProfileId());
        return processStartContext(entity, contextProfileEvents);
    }

    private CurrentContextProfile createCurrentContextProfileObject(UUID contextId,
                                                                    UUID profileId,
                                                                    UUID contextProfileId) {
        CurrentContextProfile currentContextProfile = new CurrentContextProfile();
        currentContextProfile.setContextId(contextId);
        currentContextProfile.setProfileId(profileId);
        currentContextProfile.setContextProfileId(contextProfileId);
        return currentContextProfile;
    }


    private List<ContextProfileEvent> createSkippedContextProfileEvents(UUID contextProfileId,
                                                                        List<ResourceContentDto> resources) {
        return resources.stream()
                .map(resource -> {
                    UUID resourceId = UUID.fromString(resource.getId());
                    ContextProfileEvent contextProfileEvent = new ContextProfileEvent();
                    contextProfileEvent.setContextProfileId(contextProfileId);
                    contextProfileEvent.setResourceId(resourceId);
                    contextProfileEvent.setEventData(gson.toJson(createSkippedEventData(resourceId)));
                    return contextProfileEvent;
                }).collect(Collectors.toList());
    }


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

    private StartContextEventResponseDto prepareStartContextEventResponse(
            UUID contextId, UUID currentResourceId, UUID collectionId, List<ContextProfileEvent> contextProfileEvents) {
        StartContextEventResponseDto response = new StartContextEventResponseDto();
        response.setContextId(contextId);
        response.setCurrentResourceId(currentResourceId);
        response.setCollectionId(collectionId);
        response.setEvents(contextProfileEvents.stream()
                .map(event -> gson.fromJson(event.getEventData(), PostResponseResourceDto.class))
                .collect(Collectors.toList()));

        return response;
    }

    private void sendStartEventMessage(UUID contextId, UUID profileId, UUID currentResourceId, boolean isNewAttempt) {
        StartContextEventMessageDto startEventMessage = new StartContextEventMessageDto();
        startEventMessage.setIsNewAttempt(isNewAttempt);
        startEventMessage.setCurrentResourceId(currentResourceId);
        activeMQClientService.sendStartContextEventMessage(contextId, profileId, startEventMessage);
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

    private ContextProfile createContextProfileObject(UUID contextId, UUID profileId) {
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
                return calculateScoreForOrderedMultipleChoice(userAnswers, correctAnswers);
            case TextEntry:
                return calculateScoreForCaseInsensitiveOrderedMultipleChoice(userAnswers, correctAnswers);
            case MultipleChoice:
            case MultipleChoiceImage:
            case MultipleChoiceText:
            case HotTextWord:
            case HotTextSentence:
                return calculateScoreForMultipleChoice(userAnswers, correctAnswers);
            case ExtendedText:
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
     * Compares user and correct answers, including the answer order
     * Values are trimmed and case is ignored
     *
     * Works for text_entry
     *
     * @param userAnswers    Answers provided by the user
     * @param correctAnswers Correct answers for the question
     * @return the score
     */
    private int calculateScoreForCaseInsensitiveOrderedMultipleChoice(List<AnswerDto> userAnswers, List<AnswerDto> correctAnswers) {
        if (userAnswers.size() < correctAnswers.size()) {
            return 0;
        }
        boolean isAnswerCorrect = IntStream.range(0, correctAnswers.size() - 1)
                .allMatch(i -> correctAnswers.get(i).getValue().trim()
                        .equalsIgnoreCase(userAnswers.get(i).getValue().trim()));

        return isAnswerCorrect ? 100 : 0;
    }

    /**
     * Compares user and correct answers, including the answer order
     *
     * Works for drag_and_drop
     *
     * @param userAnswers    Answers provided by the user
     * @param correctAnswers Correct answers for the question
     * @return the score
     */
    private int calculateScoreForOrderedMultipleChoice(List<AnswerDto> userAnswers, List<AnswerDto> correctAnswers) {
        if (userAnswers.size() < correctAnswers.size()) {
            return 0;
        }
        boolean isAnswerCorrect =
                IntStream.range(0, correctAnswers.size() - 1)
                        .allMatch(i -> correctAnswers.get(i).getValue().equals(userAnswers.get(i).getValue()));

        return isAnswerCorrect ? 100 : 0;
    }

    /**
     * Compares user and correct answers, order is not important
     *
     * Works for multiple_choice, multiple_choice_image, multiple_choice_text, hot_text_word and
     * hot_text_sentence
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

    @Transactional
    private void doCurrentContextEventTransaction(CurrentContextProfile currentContextProfile) {
        currentContextProfileService.delete(currentContextProfile);
        currentContextProfileService.create(currentContextProfile);
    }

    private List<ResourceContentDto> getResourcesToCreate(List<ContextProfileEvent> contextProfileEvents,
                                                          List<ResourceContentDto> resources) {
        List<UUID> contextProfileEventResourceIds = contextProfileEvents.stream()
                .map(ContextProfileEvent::getResourceId).collect(Collectors.toList());

        return resources.stream()
                .filter(resource -> !contextProfileEventResourceIds.contains(UUID.fromString(resource.getId())))
                .collect(Collectors.toList());
    }

    private List<ResourceContentDto> getCollectionResources(UUID collectionId, boolean isCollection, String token) {
        return isCollection ? collectionRestClient.getCollectionResources(collectionId.toString(), token) :
                assessmentRestClient.getAssessmentQuestions(collectionId.toString(), token);
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
