package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.dtos.messaging.StartContextEventMessageDto;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.ContextProfileEventRepository;
import com.quizzes.api.core.services.content.CollectionService;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class ContextEventService {

    @Autowired
    private ContextProfileService contextProfileService;

    @Autowired
    private ContextProfileEventService contextProfileEventService;

    @Autowired
    private ContextService contextService;

    @Autowired
    private CurrentContextProfileService currentContextProfileService;

    @Autowired
    private ActiveMQClientService activeMQClientService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    ContextProfileEventRepository contextProfileEventRepository;

    @Autowired
    private Gson gson;

    public StartContextEventResponseDto processStartContextEvent(UUID contextId, UUID profileId) {
        ContextProfileEntity entity =
                currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId);

        if (entity.getCurrentContextProfileId() == null) {
            return createCurrentContextProfile(entity);
        } else if (entity.getIsComplete()) {
            return createContextProfile(entity);
        }

        return resumeStartContextEvent(entity);
    }

    public void processOnResourceEvent(UUID contextId, UUID profileId, UUID resourceId,
                                       OnResourceEventPostRequestDto body) {
        ContextProfileEntity context =
                currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId);

        if (context.getCurrentContextProfileId() == null || (context.getCurrentContextProfileId() != null && context.getIsComplete())) {
            throw new InvalidRequestException("Context " + contextId + " not started on resource " + resourceId);
        }

        PostRequestResourceDto resourceDto = getPreviousResource(body);

        List<ResourceDto> collectionResources =
                getCollectionResources(context.getCollectionId(), context.getIsCollection());
        ResourceDto currentResource = findResourceInContext(collectionResources, resourceId, contextId);
        ResourceDto previousResource = findResourceInContext(collectionResources, resourceDto.getResourceId(),
                contextId);

        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(context.getContextProfileId());
        ContextProfileEvent contextProfileEvent = contextProfileEvents.stream()
                .filter(event -> event.getResourceId().equals(previousResource.getId())).findFirst().orElse(null);

        if (contextProfileEvent == null) {
            contextProfileEvent = createContextProfileEvent(context.getContextProfileId(), previousResource.getId());
            contextProfileEvents.add(contextProfileEvent);
            resourceDto.setScore(!resourceDto.getIsSkipped() ?
                    calculateScore(previousResource, resourceDto.getAnswer()) :
                    0);
        } else {
            resourceDto = updateExistingResourceDto(contextProfileEvent, previousResource, resourceDto);
        }

        contextProfileEvent.setEventData(gson.toJson(resourceDto));

        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, false);
        ContextProfile contextProfile = updateContextProfile(context.getContextProfileId(),
                currentResource.getId(), gson.toJson(eventSummary));

        doOnResourceEventTransaction(contextProfile, contextProfileEvent);
        sendOnResourceEventMessage(contextProfile, resourceDto, eventSummary);
    }

    private PostRequestResourceDto getPreviousResource(OnResourceEventPostRequestDto body) {
        PostRequestResourceDto resource = body.getPreviousResource();
        resource.setIsSkipped(resource.getAnswer() == null);
        return resource;
    }

    private ContextProfile updateContextProfile(UUID contextProfileId, UUID currentResourceId, String eventSummary) {
        ContextProfile contextProfile = contextProfileService.findById(contextProfileId);
        contextProfile.setCurrentResourceId(currentResourceId);
        contextProfile.setEventSummaryData(eventSummary);
        return contextProfile;
    }


    public void processFinishContextEvent(UUID contextId, UUID profileId) {
        CurrentContextProfile currentContextProfile =
                currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
        ContextProfile contextProfile = contextProfileService.findById(currentContextProfile.getContextProfileId());

        if (contextProfile.getIsComplete()) {
            return;
        }

        Context context = contextService.findById(contextId);
        finishContextEvent(context, contextProfile);
    }

    private void finishContextEvent(Context context, ContextProfile contextProfile) {
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getId());

        List<ResourceDto> resources = getCollectionResources(context.getCollectionId(),
                context.getIsCollection());
        List<ResourceDto> resourcesToCreate = getResourcesToCreate(contextProfileEvents, resources);

        List<ContextProfileEvent> contextProfileEventsToCreate =
                createSkippedContextProfileEvents(contextProfile.getId(), resourcesToCreate);

        // Fill the list of Context Profile Events to calculate the summary
        contextProfileEvents.addAll(contextProfileEventsToCreate);

        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, true);
        contextProfile.setEventSummaryData(gson.toJson(eventSummary));
        contextProfile.setIsComplete(true);

        doFinishContextEventTransaction(contextProfile, contextProfileEventsToCreate);

        //If entity does not have class is an anonymous user or it's in preview mode
        if (context.getClassId() != null) {
            sendFinishContextEventMessage(context.getId(), contextProfile.getProfileId(), eventSummary);
        }
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
                                                List<ContextProfileEvent> eventsToCreate) {
        contextProfileService.save(contextProfile);
        eventsToCreate.stream().forEach(event -> contextProfileEventService.save(event));
    }

    private StartContextEventResponseDto createCurrentContextProfile(ContextProfileEntity entity) {
        CurrentContextProfile currentContextProfile = createCurrentContextProfileObject(
                entity.getContextId(), entity.getProfileId(), entity.getContextProfileId());
        doCurrentContextEventTransaction(currentContextProfile);
        return processStartContext(entity, new ArrayList<>());
    }

    private StartContextEventResponseDto processStartContext(ContextProfileEntity entity,
                                                             List<ContextProfileEvent> contextProfileEvents) {
        //If entity does not have class is an anonymous user or it's in preview mode
        if (entity.getClassId() != null) {
            sendStartEventMessage(entity.getContextId(), entity.getProfileId(), entity.getCurrentResourceId(), true);
        }
        return prepareStartContextEventResponse(entity.getContextId(), entity.getCurrentResourceId(),
                entity.getCollectionId(), contextProfileEvents);
    }

    private StartContextEventResponseDto createContextProfile(ContextProfileEntity entity) {
        ContextProfile contextProfile = createContextProfileObject(entity.getContextId(), entity.getProfileId());
        doCreateContextProfileTransaction(contextProfile);
        return processStartContext(entity, new ArrayList<>());
    }

    private StartContextEventResponseDto resumeStartContextEvent(ContextProfileEntity contextProfile) {
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getContextProfileId());

        //If entity does not have class is an anonymous user or it's in preview mode
        if (contextProfile.getClassId() != null) {
            sendStartEventMessage(contextProfile.getContextId(), contextProfile.getProfileId(),
                    contextProfile.getCurrentResourceId(), false);
        }
        return prepareStartContextEventResponse(contextProfile.getContextId(), contextProfile.getCurrentResourceId(),
                contextProfile.getCollectionId(), contextProfileEvents);
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
                                                                        List<ResourceDto> resources) {
        return resources.stream()
                .map(resource -> {
                    ContextProfileEvent contextProfileEvent = new ContextProfileEvent();
                    contextProfileEvent.setContextProfileId(contextProfileId);
                    contextProfileEvent.setResourceId(resource.getId());
                    contextProfileEvent.setEventData(gson.toJson(createSkippedEventData(resource.getId())));
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
        evenData.setAnswer(null);
        return evenData;
    }

    private StartContextEventResponseDto prepareStartContextEventResponse(UUID contextId,
                                                                          UUID currentResourceId,
                                                                          UUID collectionId,
                                                                          List<ContextProfileEvent>
                                                                                  contextProfileEvents) {
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

    private ContextProfileEvent createContextProfileEvent(UUID contextProfileId, UUID resourceId) {
        ContextProfileEvent event = new ContextProfileEvent();
        event.setContextProfileId(contextProfileId);
        event.setResourceId(resourceId);
        return event;
    }

    private int calculateScoreByQuestionType(String questionType, List<AnswerDto> userAnswers,
                                             List<AnswerDto> correctAnswers) {
        QuestionTypeEnum enumType = QuestionTypeEnum.fromString(questionType);
        switch (enumType) {
            case TrueFalse:
            case SingleChoice:
                return calculateScoreForSimpleOption(userAnswers, correctAnswers);
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
     * @param userAnswers    Answer provided by the user
     * @param correctAnswers Correct answer for the question
     * @return the score
     */
    private int calculateScoreForSimpleOption(List<AnswerDto> userAnswers, List<AnswerDto> correctAnswers) {
        if (userAnswers.isEmpty()) {
            return 0;
        }
        return userAnswers.get(0).getValue().equalsIgnoreCase(correctAnswers.get(0).getValue()) ? 100 : 0;
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

    private List<ResourceDto> getResourcesToCreate(List<ContextProfileEvent> contextProfileEvents,
                                                   List<ResourceDto> resources) {
        List<UUID> contextProfileEventResourceIds = contextProfileEvents.stream()
                .map(ContextProfileEvent::getResourceId).collect(Collectors.toList());

        return resources.stream()
                .filter(resource -> !contextProfileEventResourceIds.contains(resource.getId()))
                .collect(Collectors.toList());
    }

    private List<ResourceDto> getCollectionResources(UUID collectionId, boolean isCollection) {
        return isCollection ?
                collectionService.getCollectionResources(collectionId) :
                collectionService.getAssessmentQuestions(collectionId);
    }

    private PostRequestResourceDto updateExistingResourceDto(ContextProfileEvent contextProfileEvent,
                                                             ResourceDto resourceInfo,
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

    private int calculateScore(ResourceDto resource, List<AnswerDto> answer) {
        return calculateScoreByQuestionType(resource.getMetadata().getType(), answer,
                resource.getMetadata().getCorrectAnswer());
    }

    private ResourceDto findResourceInContext(List<ResourceDto> resources, UUID resourceId, UUID contextId) {
        ResourceDto resource = resources.stream().filter(r -> r.getId().equals(resourceId)).findFirst().orElse(null);
        if (resource == null) {
            throw new ContentNotFoundException("Resource ID: " + resourceId + " is not part of " +
                    "the Context ID: " + contextId);
        }
        return resource;
    }

}
