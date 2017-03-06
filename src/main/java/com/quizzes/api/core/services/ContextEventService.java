package com.quizzes.api.core.services;

import com.google.common.base.Functions;
import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.OnResourceEventPostRequestDto;
import com.quizzes.api.core.dtos.OnResourceEventResponseDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.PostResponseResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.StartContextEventResponseDto;
import com.quizzes.api.core.dtos.TaxonomySummaryDto;
import com.quizzes.api.core.dtos.messaging.FinishContextEventMessageDto;
import com.quizzes.api.core.dtos.messaging.OnResourceEventMessageDto;
import com.quizzes.api.core.dtos.messaging.StartContextEventMessageDto;
import com.quizzes.api.core.enums.CollectionSetting;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.enums.settings.ShowFeedbackOptions;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.repositories.ContextProfileEventRepository;
import com.quizzes.api.core.services.content.CollectionService;
import com.quizzes.api.core.services.content.AnalyticsContentService;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
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
    private AnalyticsContentService analyticsContentService;

    @Autowired
    private ContextProfileEventRepository contextProfileEventRepository;

    @Autowired
    private QuizzesUtils quizzesUtils;

    @Autowired
    private Gson gson;

    public StartContextEventResponseDto processStartContextEvent(UUID contextId, UUID profileId, String token) {
        ContextProfileEntity entity =
                currentContextProfileService.findCurrentContextProfileByContextIdAndProfileId(contextId, profileId);

        if (entity.getCurrentContextProfileId() == null) {
            return createCurrentContextProfile(entity, token);
        } else if (entity.getIsComplete()) {
            // Starts a new attempt, so we reset the current Resource ID
            entity.setCurrentResourceId(null);
            return createContextProfile(entity, token);
        }

        return resumeStartContextEvent(entity);
    }

    public OnResourceEventResponseDto processOnResourceEvent(UUID contextId, UUID profileId, UUID resourceId,
                                                             OnResourceEventPostRequestDto body, String token) {
        ContextProfileEntity context = currentContextProfileService
                .findCurrentContextProfileByContextIdAndProfileId(contextId, profileId);

        if (context.getCurrentContextProfileId() == null || (context.getCurrentContextProfileId() != null && context.getIsComplete())) {
            throw new InvalidRequestException("Context " + contextId + " not started on resource " + resourceId);
        }

        PostRequestResourceDto resourceDto = getPreviousResource(body);

        CollectionDto collectionDto = collectionService.getCollectionOrAssessment(context.getCollectionId(),
                context.getIsCollection());
        List<ResourceDto> collectionResources = collectionDto.getResources();
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
        List<TaxonomySummaryDto> collectionTaxonomy = calculateTaxonomySummary(contextProfileEvents, false, collectionDto, eventSummary);
        ContextProfile contextProfile = updateContextProfile(context.getContextProfileId(),
                currentResource.getId(), gson.toJson(eventSummary), gson.toJson(collectionTaxonomy));

        ContextProfileEvent newEvent = doOnResourceEventTransaction(contextProfile, contextProfileEvent);
        if (context.getClassId() != null) {
            sendOnResourceEventMessage(contextProfile, resourceDto, eventSummary);
            sendAnalyticsEvent(context, profileId, token, currentResource, newEvent.getCreatedAt().getTime(), true);
        }

        if (collectionDto.getMetadata().getSetting() == null) {
            return new OnResourceEventResponseDto();
        }

        ShowFeedbackOptions showFeedback = ShowFeedbackOptions.fromValue(
                collectionDto.getMetadata().getSetting(CollectionSetting.ShowFeedback.getLiteral(),
                        ShowFeedbackOptions.Never.getLiteral()).toString()
        );

        if (!showFeedback.equals(ShowFeedbackOptions.Immediate)) {
            return new OnResourceEventResponseDto();
        }
        return new OnResourceEventResponseDto(resourceDto.getScore());
    }

    private void sendAnalyticsEvent(ContextProfileEntity context, UUID profileId, String token, ResourceDto resource,
                                    long time, boolean isPlayEvent) {
        if(isPlayEvent){
            analyticsContentService.resourcePlay(context.getCollectionId(), context.getClassId(),
                    context.getContextProfileId(), profileId, context.getIsCollection(), token, resource, time);
        }
        //TODO: Stop event with validation if it's not the last event
    }

    private PostRequestResourceDto getPreviousResource(OnResourceEventPostRequestDto body) {
        PostRequestResourceDto resource = body.getPreviousResource();
        resource.setIsSkipped(resource.getAnswer() == null);
        return resource;
    }

    private ContextProfile updateContextProfile(UUID contextProfileId, UUID currentResourceId, String eventSummary, String taxonomySummary) {
        ContextProfile contextProfile = contextProfileService.findById(contextProfileId);
        contextProfile.setCurrentResourceId(currentResourceId);
        contextProfile.setEventSummaryData(eventSummary);
        contextProfile.setTaxonomySummaryData(taxonomySummary);
        return contextProfile;
    }

    public void processFinishContextEvent(UUID contextId, UUID profileId, String token) {
        CurrentContextProfile currentContextProfile =
                currentContextProfileService.findByContextIdAndProfileId(contextId, profileId);
        ContextProfile contextProfile = contextProfileService.findById(currentContextProfile.getContextProfileId());

        if (contextProfile.getIsComplete()) {
            return;
        }

        Context context = contextService.findById(contextId);
        finishContextEvent(context, contextProfile, token);
    }

    private void finishContextEvent(Context context, ContextProfile contextProfile, String token) {
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getId());

        CollectionDto collectionDto = collectionService.getCollectionOrAssessment(context.getCollectionId(),
                context.getIsCollection());

        List<ResourceDto> resources = collectionDto.getResources();
        List<ResourceDto> resourcesToCreate = getResourcesToCreate(contextProfileEvents, resources);

        List<ContextProfileEvent> contextProfileEventsToCreate =
                createSkippedContextProfileEvents(contextProfile.getId(), resourcesToCreate);

        // Fill the list of Context Profile Events to calculate the summary
        contextProfileEvents.addAll(contextProfileEventsToCreate);

        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, true);
        List<TaxonomySummaryDto> taxonomySummaryList = calculateTaxonomySummary(contextProfileEvents,
                true, collectionDto, eventSummary);
        contextProfile.setEventSummaryData(gson.toJson(eventSummary));
        contextProfile.setTaxonomySummaryData(gson.toJson(taxonomySummaryList));
        contextProfile.setIsComplete(true);

        doFinishContextEventTransaction(contextProfile, contextProfileEventsToCreate);

        //If entity does not have class is an anonymous user or it's in preview mode
        if (context.getClassId() != null) {
            sendFinishContextEventMessage(context.getId(), contextProfile.getProfileId(), eventSummary);
            analyticsContentService.collectionStop(
                    context.getCollectionId(), context.getClassId(), contextProfile.getId(), contextProfile.getProfileId(),
                    context.getIsCollection(), token, contextProfile.getCreatedAt().getTime());
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
    public ContextProfileEvent doOnResourceEventTransaction(ContextProfile contextProfile, ContextProfileEvent contextProfileEvent) {
        contextProfileService.save(contextProfile);
        return contextProfileEventService.save(contextProfileEvent);
    }

    @Transactional
    public void doFinishContextEventTransaction(ContextProfile contextProfile,
                                                List<ContextProfileEvent> eventsToCreate) {
        contextProfileService.save(contextProfile);
        eventsToCreate.stream().forEach(event -> contextProfileEventService.save(event));
    }

    private StartContextEventResponseDto createCurrentContextProfile(ContextProfileEntity entity, String token) {
        CurrentContextProfile currentContextProfile = createCurrentContextProfileObject(
                entity.getContextId(), entity.getProfileId(), entity.getContextProfileId());
        doCurrentContextEventTransaction(currentContextProfile);
        return processStartContext(entity, new ArrayList<>(), token);
    }

    private StartContextEventResponseDto processStartContext(ContextProfileEntity entity,
                                                             List<ContextProfileEvent> contextProfileEvents,
                                                             String token) {
        //If entity does not have class is an anonymous user or it's in preview mode
        if (entity.getClassId() != null) {
            sendStartEventMessage(entity.getContextId(), entity.getProfileId(), entity.getCurrentResourceId(), true);
            analyticsContentService.collectionPlay(entity.getCollectionId(), entity.getClassId(), entity.getContextProfileId(),
                    entity.getProfileId(), entity.getIsCollection(), token);
        }
        return prepareStartContextEventResponse(entity.getContextId(), entity.getCurrentResourceId(),
                entity.getCollectionId(), contextProfileEvents);
    }

    private StartContextEventResponseDto createContextProfile(ContextProfileEntity entity, String token) {
        ContextProfile contextProfile = createContextProfileObject(entity.getContextId(), entity.getProfileId());
        doCreateContextProfileTransaction(contextProfile);
        return processStartContext(entity, new ArrayList<>(), token);
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
     * <p>
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
        boolean isCorrect = IntStream.rangeClosed(0, correctAnswers.size() - 1)
                .allMatch(i -> correctAnswers.get(i).getValue().trim()
                        .equalsIgnoreCase(userAnswers.get(i).getValue().trim()));

        return isCorrect ? 100 : 0;
    }

    /**
     * Compares user and correct answers, including the answer order
     * <p>
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
        boolean isCorrect = IntStream.rangeClosed(0, correctAnswers.size() - 1)
                .allMatch(i -> correctAnswers.get(i).getValue().equals(userAnswers.get(i).getValue()));

        return isCorrect ? 100 : 0;
    }

    /**
     * Compares user and correct answers, order is not important
     * <p>
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

    private List<TaxonomySummaryDto> calculateTaxonomySummary(List<ContextProfileEvent> contextProfileEvents,
                                                        boolean calculateSkipped,
                                                        CollectionDto collectionDto, EventSummaryDataDto eventSummary) {

        // Calculating the collection taxonomy summary
        Map<String, TaxonomySummaryDto> collectionTaxonomyMap = new HashMap<>();
        if (collectionDto.getMetadata() != null && collectionDto.getMetadata().getTaxonomy() != null &&
                !collectionDto.getMetadata().getTaxonomy().isEmpty()) {
            List<UUID> allEventResourceIds = contextProfileEvents.stream()
                    .map(ContextProfileEvent::getResourceId)
                    .collect(Collectors.toList());
            collectionTaxonomyMap.putAll(collectionDto.getMetadata().getTaxonomy().keySet().stream()
                    .map(key -> {
                        TaxonomySummaryDto taxonomySummaryDto = mapEventSummaryToTaxonomySummary(eventSummary);
                        taxonomySummaryDto.setTaxonomyId(key);
                        taxonomySummaryDto.setResources(allEventResourceIds);
                        return taxonomySummaryDto;
                    })
                    .collect(Collectors.toMap(TaxonomySummaryDto::getTaxonomyId, Function.identity())));
        }
        // Calculating additional resource's taxonomy
        Map<UUID, Set<String>> resourcesNotInCollectionTaxonomy = collectionDto.getResources().stream()
                .filter(resource -> (resource.getMetadata() != null)
                        && (resource.getMetadata().getTaxonomy() != null)
                        && (!collectionTaxonomyMap.keySet().containsAll(resource.getMetadata().getTaxonomy().entrySet()))
                )
                .collect(Collectors.toMap(ResourceDto::getId,
                        resource -> resource.getMetadata().getTaxonomy().keySet()));

        List<ContextProfileEvent> eventsWithTaxonomy = contextProfileEvents.stream()
                .filter(event -> resourcesNotInCollectionTaxonomy.keySet().contains(event.getResourceId()))
                .collect(Collectors.toList());

        Map<String, List<ContextProfileEvent>> eventsByTaxonomy = new HashMap<>();
        for (ContextProfileEvent event : eventsWithTaxonomy) {
            List<String> eventTaxonomyList = resourcesNotInCollectionTaxonomy.get(event.getResourceId()).stream()
                    .filter(taxonomyId -> !collectionTaxonomyMap.keySet().contains(Functions.identity()))
                    .collect(Collectors.toList());
            for (String taxonomyId : eventTaxonomyList) {
                if (eventsByTaxonomy.containsKey(taxonomyId)) {
                    List<ContextProfileEvent> eventsInTaxonomy = eventsByTaxonomy.get(taxonomyId);
                    eventsInTaxonomy.add(event);
                }
                else {
                    List<ContextProfileEvent> eventsInTaxonomy = new ArrayList<>();
                    eventsInTaxonomy.add(event);
                    eventsByTaxonomy.put(taxonomyId, eventsInTaxonomy);
                }
            }
        }

        List<TaxonomySummaryDto> eventTaxonomyList = new ArrayList<>();
        eventsByTaxonomy.entrySet().stream().forEach(entry -> {
            EventSummaryDataDto eventSummaryByTaxonomy = this.calculateEventSummary(entry.getValue(), calculateSkipped);
            TaxonomySummaryDto taxonomySummaryDto = mapEventSummaryToTaxonomySummary(eventSummaryByTaxonomy);
            taxonomySummaryDto.setTaxonomyId(entry.getKey());
            List<UUID> resourceIdListByTaxonomy = entry.getValue().stream()
                    .map(event -> event.getResourceId())
                    .distinct()
                    .collect(Collectors.toList());
            taxonomySummaryDto.setResources(resourceIdListByTaxonomy);
            eventTaxonomyList.add(taxonomySummaryDto);
        });

        List<TaxonomySummaryDto> result = new ArrayList<>();
        result.addAll(collectionTaxonomyMap.values());
        result.addAll(eventTaxonomyList);

        return result;
    }

    private TaxonomySummaryDto mapEventSummaryToTaxonomySummary(EventSummaryDataDto eventSummaryDataDto) {
        TaxonomySummaryDto result = new TaxonomySummaryDto();
        result.setAverageReaction(eventSummaryDataDto.getAverageReaction());
        result.setAverageScore(eventSummaryDataDto.getAverageScore());
        result.setTotalTimeSpent(eventSummaryDataDto.getTotalTimeSpent());
        result.setTotalCorrect(eventSummaryDataDto.getTotalCorrect());
        result.setTotalAnswered(eventSummaryDataDto.getTotalAnswered());
        return result;
    }

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
