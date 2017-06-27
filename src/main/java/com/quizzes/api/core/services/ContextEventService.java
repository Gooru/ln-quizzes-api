package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.EventContextDto;
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
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.exceptions.NoAttemptsLeftException;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextProfileEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.core.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.core.services.content.AnalyticsContentService;
import com.quizzes.api.core.services.content.ClassMemberService;
import com.quizzes.api.core.services.content.CollectionService;
import com.quizzes.api.core.services.messaging.ActiveMQClientService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.quizzes.api.core.enums.QuestionTypeEnum.ExtendedText;

@Service
public class ContextEventService {

    private static final int CORRECT_SCORE = 100;
    private static final int INCORRECT_SCORE = 0;

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
    private ClassMemberService classMemberService;

    @Autowired
    private QuizzesUtils quizzesUtils;

    @Autowired
    private Gson gson;

    @Transactional
    public StartContextEventResponseDto processStartContextEvent(UUID contextId, UUID profileId,
                                                                 EventContextDto eventContext, String token) {
        ContextEntity context = contextService.findById(contextId);
        classMemberService.validateClassMember(context.getClassId(), profileId, token);
        return doStartContextEvent(context, profileId, eventContext, token);
    }

    @Transactional
    public OnResourceEventResponseDto processOnResourceEvent(UUID contextId, UUID profileId, UUID resourceId,
                                                             OnResourceEventPostRequestDto onResourceEventBody,
                                                             String token) {
        ContextEntity context = contextService.findById(contextId);
        PostRequestResourceDto previousResourceBody = onResourceEventBody.getPreviousResource();
        ContextProfileEntity currentContextProfile =
                currentContextProfileService.findCurrentContextProfile(contextId, profileId);
        if (currentContextProfile.getIsComplete()) {
            throw new InternalServerException("Attempt ID " + currentContextProfile.getContextProfileId() +
                    " was completed, no more events allowed. Context ID " + contextId +
                    " and Assignee ID " + profileId);
        }
        CollectionDto collectionDto =
                collectionService.getCollectionOrAssessment(currentContextProfile.getCollectionId(),
                        currentContextProfile.getIsCollection(), token);
        ResourceDto currentResource = findResourceInResourceList(collectionDto.getResources(), resourceId);
        if (currentResource == null) {
            throw new ContentNotFoundException("Current Resource ID: " + resourceId + " was not found in the" +
                    " list of resources for Collection ID " + collectionDto.getId());
        }
        ResourceDto previousResource =
                findResourceInResourceList(collectionDto.getResources(), previousResourceBody.getResourceId());
        if (previousResource == null) {
            throw new ContentNotFoundException("Previous Resource ID: " + resourceId + " was not found in the" +
                    " list of resources for Collection ID " + collectionDto.getId());
        }

        return doOnResourceEvent(context, currentContextProfile, resourceId, previousResource, previousResourceBody,
                collectionDto, onResourceEventBody.getEventContext(), token);
    }

    @Transactional
    public void processFinishContextEvent(UUID contextId, UUID profileId, EventContextDto eventContext, String token) {
        ContextEntity context = contextService.findById(contextId);
        ContextProfileEntity currentContextProfile =
                currentContextProfileService.findCurrentContextProfile(contextId, profileId);

        // If this attempt was already completed do nothing
        if (currentContextProfile.getIsComplete()) {
            return;
        }

        doFinishContextEvent(context, currentContextProfile, eventContext, token);
    }

    private StartContextEventResponseDto doStartContextEvent(ContextEntity context, UUID profileId,
                                                             EventContextDto eventContext, String token) {
        try {
            ContextProfileEntity currentContextProfile =
                    currentContextProfileService.findCurrentContextProfile(context.getContextId(), profileId);
            if (currentContextProfile.getIsComplete()) {
                // Return subsequent (new) attempt
                return createStartContextEvent(context, profileId, eventContext, token);
            }
            // Returns resumed (incomplete) attempt
            return resumeStartContextEvent(context, currentContextProfile);
        } catch (ContentNotFoundException e) {
            // Returns first attempt
            return createStartContextEvent(context, profileId, eventContext, token);
        }
    }

    private OnResourceEventResponseDto doOnResourceEvent(ContextEntity context,
                                                         ContextProfileEntity contextProfileEntity, UUID resourceId,
                                                         ResourceDto previousResource,
                                                         PostRequestResourceDto previousResourceEventData,
                                                         CollectionDto collectionDto, EventContextDto eventContext,
                                                         String token) {
        boolean isSkipEvent = isSkipEvent(previousResource.getIsResource(), previousResourceEventData);
        int score = calculateScore(isSkipEvent, previousResource.getIsResource(),
                previousResource.getMetadata().getType(), previousResourceEventData.getAnswer(),
                previousResource.getMetadata().getCorrectAnswer());

        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfileEntity.getContextProfileId());
        ContextProfileEvent contextProfileEvent = contextProfileEvents.stream()
                .filter(event -> event.getResourceId().equals(previousResource.getId()))
                .findFirst().orElse(null);
        if (contextProfileEvent == null) {
            PostRequestResourceDto eventData = buildContextProfileEventData(isSkipEvent,
                    previousResource.getIsResource(), score, previousResourceEventData.getTimeSpent(),
                    previousResourceEventData.getReaction(), previousResourceEventData.getAnswer());
            contextProfileEvent = buildContextProfileEvent(contextProfileEntity.getContextProfileId(),
                    previousResource.getId(), eventData);
            contextProfileEvents.add(contextProfileEvent);
        } else {
            PostRequestResourceDto eventData =
                    gson.fromJson(contextProfileEvent.getEventData(), PostRequestResourceDto.class);
            eventData.setTimeSpent(eventData.getTimeSpent() + previousResourceEventData.getTimeSpent());
            eventData.setReaction(previousResourceEventData.getReaction());
            if (!isSkipEvent) {
                eventData.setIsSkipped(isSkipEvent);
                eventData.setScore(score);
                eventData.setAnswer(previousResourceEventData.getAnswer());
            }
            contextProfileEvent.setEventData(gson.toJson(eventData));
        }
        contextProfileEventService.save(contextProfileEvent);

        EventSummaryDataDto eventSummary =
                calculateEventSummary(contextProfileEvents, getQuestionTypeMap(collectionDto), false);
        List<TaxonomySummaryDto> taxonomySummaries =
                calculateTaxonomySummary(contextProfileEvents, collectionDto, false);
        ContextProfile savedContextProfile =
                saveContextProfile(contextProfileEntity.getContextProfileId(), resourceId, false, eventSummary,
                        taxonomySummaries);

        if (contextProfileEntity.getClassId() != null) {
            previousResourceEventData.setScore(score);
            previousResourceEventData.setIsSkipped(isSkipEvent);
            sendOnResourceEventMessage(savedContextProfile, previousResourceEventData, eventSummary);
            sendAnalyticsEvent(context, savedContextProfile, UUID.randomUUID(), previousResource,
                    previousResourceEventData, eventContext, token);
        }

        ShowFeedbackOptions showFeedback = ShowFeedbackOptions.fromValue(
                collectionDto.getMetadata().getSetting(CollectionSetting.ShowFeedback,
                        ShowFeedbackOptions.Never.getLiteral()).toString());
        if (ShowFeedbackOptions.Immediate.equals(showFeedback)) {
            return new OnResourceEventResponseDto(score);
        }
        return new OnResourceEventResponseDto();
    }

    private void doFinishContextEvent(ContextEntity context, ContextProfileEntity contextProfile,
                                      EventContextDto eventContext, String token) {
        CollectionDto collectionDto = collectionService.getCollectionOrAssessment(context.getCollectionId(),
                context.getIsCollection(), token);
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getContextProfileId());
        List<UUID> createdResourceIds = contextProfileEvents.stream()
                .map(ContextProfileEvent::getResourceId)
                .collect(Collectors.toList());
        List<ResourceDto> pendingResources = collectionDto.getResources().stream()
                .filter(resource -> !createdResourceIds.contains(resource.getId()))
                .collect(Collectors.toList());
        List<ContextProfileEvent> pendingContextProfileEvents = pendingResources.stream()
                .map(pendingResource -> buildContextProfileEvent(contextProfile.getContextProfileId(),
                        pendingResource.getId(),
                        buildContextProfileEventData(true, pendingResource.getIsResource(), 0, 0, 0, null)))
                .collect(Collectors.toList());

        // Fill in the pending ContextProfileEvent data to calculate the summaries
        contextProfileEvents.addAll(pendingContextProfileEvents);
        EventSummaryDataDto eventSummary =
                calculateEventSummary(contextProfileEvents, getQuestionTypeMap(collectionDto), true);
        List<TaxonomySummaryDto> taxonomySummaries =
                calculateTaxonomySummary(contextProfileEvents, collectionDto, true);
        // Save ContextProfile data
        ContextProfile savedContextProfile =
                saveContextProfile(contextProfile.getContextProfileId(), contextProfile.getCurrentResourceId(), true,
                        eventSummary, taxonomySummaries);
        // Save pending ContextProfileEvents
        pendingContextProfileEvents.stream().forEach(event -> contextProfileEventService.save(event));

        // If there is Class ID the event message is propagated
        if (context.getClassId() != null) {
            sendFinishContextEventMessage(context.getContextId(), contextProfile.getProfileId(), eventSummary);
            analyticsContentService.collectionPlayStop(context, savedContextProfile, eventContext, token);
        }
    }

    private StartContextEventResponseDto createStartContextEvent(ContextEntity context, UUID profileId,
                                                                 EventContextDto eventContext, String token) {
        validateProfileAttemptsLeft(context, profileId, token);
        ContextProfile savedContextProfile = contextProfileService.save(buildContextProfile(context.getContextId(),
                profileId));
        CurrentContextProfile currentContextProfile = buildCurrentContextProfile(context.getContextId(), profileId,
                savedContextProfile.getId());
        currentContextProfileService.delete(currentContextProfile);
        currentContextProfileService.create(currentContextProfile);
        StartContextEventResponseDto eventResponse = buildStartContextEventResponse(context.getContextId(),
                context.getCollectionId(), savedContextProfile.getCurrentResourceId(), Collections.EMPTY_LIST);

        // If there is Class ID the event message is propagated
        if (context.getClassId() != null) {
            sendStartEventMessage(context.getContextId(), profileId, savedContextProfile.getCurrentResourceId(), true);
            analyticsContentService.collectionPlayStart(context, savedContextProfile, eventContext, token);
        }
        return eventResponse;
    }

    private StartContextEventResponseDto resumeStartContextEvent(ContextEntity context,
                                                                 ContextProfileEntity contextProfile) {
        List<ContextProfileEvent> contextProfileEvents =
                contextProfileEventService.findByContextProfileId(contextProfile.getContextProfileId());
        StartContextEventResponseDto eventResponse = buildStartContextEventResponse(context.getContextId(),
                context.getCollectionId(), contextProfile.getCurrentResourceId(), contextProfileEvents);

        // If there is Class ID the event message is propagated
        if (context.getClassId() != null) {
            sendStartEventMessage(context.getContextId(), contextProfile.getProfileId(),
                    contextProfile.getCurrentResourceId(), false);
        }
        return eventResponse;
    }

    private void sendStartEventMessage(UUID contextId, UUID profileId, UUID currentResourceId, boolean isNewAttempt) {
        StartContextEventMessageDto startEventMessage = new StartContextEventMessageDto();
        startEventMessage.setCurrentResourceId(currentResourceId);
        startEventMessage.setIsNewAttempt(isNewAttempt);
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

    private void sendAnalyticsEvent(ContextEntity context, ContextProfile contextProfile, UUID resourceEventId,
                                    ResourceDto previousResource, PostRequestResourceDto answerResource,
                                    EventContextDto eventContext, String token) {
        Long endTime = quizzesUtils.getCurrentTimestamp();
        Long startTime = endTime - answerResource.getTimeSpent();

        analyticsContentService.resourcePlayStart(context, contextProfile, resourceEventId, previousResource, startTime,
                eventContext, token);
        analyticsContentService.reactionCreate(context, contextProfile, resourceEventId,
                String.valueOf(answerResource.getReaction()), startTime, previousResource.getId(), eventContext, token);
        analyticsContentService.resourcePlayStop(context, contextProfile, resourceEventId, previousResource,
                answerResource, startTime, endTime, eventContext, token);
    }

    private StartContextEventResponseDto buildStartContextEventResponse(UUID contextId, UUID collectionId,
                                                                        UUID currentResourceId,
                                                                        List<ContextProfileEvent> contextProfileEvents) {
        StartContextEventResponseDto response = new StartContextEventResponseDto();
        response.setContextId(contextId);
        response.setCollectionId(collectionId);
        response.setCurrentResourceId(currentResourceId);
        response.setEvents(contextProfileEvents.stream()
                .map(event -> {
                    PostResponseResourceDto eventData =
                            gson.fromJson(event.getEventData(), PostResponseResourceDto.class);
                    eventData.setResourceId(event.getResourceId());
                    eventData.setIsResource(null);
                    return eventData;
                })
                .collect(Collectors.toList()));
        return response;
    }

    private ContextProfile buildContextProfile(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setContextId(contextId);
        contextProfile.setProfileId(profileId);
        contextProfile.setIsComplete(false);
        contextProfile.setEventSummaryData(gson.toJson(
                calculateEventSummary(Collections.EMPTY_LIST, new HashMap<>(), false)));
        return contextProfile;
    }

    private CurrentContextProfile buildCurrentContextProfile(UUID contextId, UUID profileId, UUID contextProfileId) {
        CurrentContextProfile currentContextProfile = new CurrentContextProfile();
        currentContextProfile.setContextId(contextId);
        currentContextProfile.setProfileId(profileId);
        currentContextProfile.setContextProfileId(contextProfileId);
        return currentContextProfile;
    }

    private ContextProfileEvent buildContextProfileEvent(UUID contextProfileId, UUID resourceId,
                                                         PostRequestResourceDto eventData) {
        ContextProfileEvent contextProfileEvent = new ContextProfileEvent();
        contextProfileEvent.setContextProfileId(contextProfileId);
        contextProfileEvent.setResourceId(resourceId);
        contextProfileEvent.setEventData(gson.toJson(eventData));
        return contextProfileEvent;
    }

    private PostRequestResourceDto buildContextProfileEventData(boolean isSkipped, boolean isResource,
                                                                int score, long timeSpent, int reaction,
                                                                List<AnswerDto> answerList) {
        PostRequestResourceDto eventData = new PostRequestResourceDto();
        eventData.setIsSkipped(isSkipped);
        eventData.setIsResource(isResource);
        eventData.setScore(score);
        eventData.setTimeSpent(timeSpent);
        eventData.setReaction(reaction);
        eventData.setAnswer(answerList);
        return eventData;
    }

    private ContextProfile saveContextProfile(UUID contextProfileId, UUID currentResourceId, boolean isComplete,
                                              EventSummaryDataDto eventSummary,
                                              List<TaxonomySummaryDto> taxonomySummaries) {
        ContextProfile contextProfile = contextProfileService.findById(contextProfileId);
        contextProfile.setCurrentResourceId(currentResourceId);
        contextProfile.setEventSummaryData(gson.toJson(eventSummary));
        contextProfile.setTaxonomySummaryData(gson.toJson(taxonomySummaries));
        contextProfile.setIsComplete(isComplete);
        return contextProfileService.save(contextProfile);
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
    private int calculateScoreForCaseInsensitiveOrderedMultipleChoice(List<AnswerDto> userAnswers,
                                                                      List<AnswerDto> correctAnswers) {
        if (userAnswers.size() < correctAnswers.size()) {
            return 0;
        }
        boolean isCorrect = IntStream.rangeClosed(0, correctAnswers.size() - 1)
                .allMatch(i -> correctAnswers.get(i).getValue()
                        .equalsIgnoreCase(userAnswers.get(i).getValue().trim()));
        return isCorrect ? CORRECT_SCORE : INCORRECT_SCORE;
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

    private EventSummaryDataDto calculateEventSummary(Collection<ContextProfileEvent> contextProfileEvents,
                                                      Map<UUID, String> questionTypeMap, boolean calculateSkipped) {
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

            if (!eventDataDto.getIsResource() && (calculateSkipped || !eventDataDto.getIsSkipped())) {
                //TODO: This code was commented by Gooru's requirements. It will be enabled again once Gooru notify us.
                //String questionType = questionTypeMap.get(eventDataDto.getResourceId());
                //QuestionTypeEnum questionTypeEnum = QuestionTypeEnum.getEnum(questionType);
                //if (questionTypeEnum != ExtendedText) {
                    sumScore += eventDataDto.getScore();
                    totalCorrect += eventDataDto.getScore() == 100 ? 1 : 0;
                    totalAnswered++;
                //}
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
                                                              CollectionDto collectionDto,
                                                              boolean calculateSkipped) {
        Map<String, Set<ContextProfileEvent>> eventsByTaxonomy =
                getEventsByTaxonomy(contextProfileEvents, collectionDto.getResources());
        Map<UUID, String> questionTypeMap = getQuestionTypeMap(collectionDto);
        return calculateSummaryByTaxonomy(eventsByTaxonomy, questionTypeMap, calculateSkipped);
    }

    private Map<String, Set<ContextProfileEvent>> getEventsByTaxonomy(List<ContextProfileEvent> contextProfileEvents,
                                                                      List<ResourceDto> resources) {
        Map<UUID, Set<String>> taxonomiesByResource = resources.stream()
                .filter(resource -> resource.getMetadata() != null && resource.getMetadata().getTaxonomy() != null)
                .collect(Collectors.toMap(ResourceDto::getId,
                        resource -> resource.getMetadata().getTaxonomy().keySet()));
        Map<String, Set<ContextProfileEvent>> eventsByTaxonomy = new HashMap<>();
        for (ContextProfileEvent event : contextProfileEvents) {
            Set<String> taxonomies = taxonomiesByResource.get(event.getResourceId());
            if (taxonomies != null) {
                for (String taxonomy : taxonomies) {
                    if (!eventsByTaxonomy.containsKey(taxonomy)) {
                        eventsByTaxonomy.put(taxonomy, new HashSet<>());
                    }
                    eventsByTaxonomy.get(taxonomy).add(event);
                }
            }
        }
        return eventsByTaxonomy;
    }

    private List<TaxonomySummaryDto> calculateSummaryByTaxonomy(Map<String, Set<ContextProfileEvent>> eventsByTaxonomy,
                                                                Map<UUID, String> questionTypeMap,
                                                                boolean calculateSkipped) {
        return eventsByTaxonomy.entrySet().stream()
                .map(entry -> {
                    String taxonomy = entry.getKey();
                    Set<ContextProfileEvent> events = entry.getValue();
                    EventSummaryDataDto eventSummary = calculateEventSummary(events, questionTypeMap, calculateSkipped);
                    List<UUID> resourceIds = events.stream()
                            .map(ContextProfileEvent::getResourceId)
                            .collect(Collectors.toList());
                    return buildTaxonomySummary(taxonomy, eventSummary, resourceIds);
                })
                .collect(Collectors.toList());
    }

    private TaxonomySummaryDto buildTaxonomySummary(String taxonomy, EventSummaryDataDto eventSummary,
                                                    List<UUID> resourceIds) {
        TaxonomySummaryDto taxonomySummary = new TaxonomySummaryDto();
        taxonomySummary.setTaxonomyId(taxonomy);
        taxonomySummary.setAverageReaction(eventSummary.getAverageReaction());
        taxonomySummary.setAverageScore(eventSummary.getAverageScore());
        taxonomySummary.setTotalTimeSpent(eventSummary.getTotalTimeSpent());
        taxonomySummary.setTotalCorrect(eventSummary.getTotalCorrect());
        taxonomySummary.setTotalAnswered(eventSummary.getTotalAnswered());
        taxonomySummary.setResources(resourceIds);
        return taxonomySummary;
    }

    private boolean isSkipEvent(boolean isResource, PostRequestResourceDto eventData) {
        return isResource ? (eventData.getTimeSpent() == 0) : (eventData.getAnswer() == null);
    }

    private int calculateScore(boolean isSkipped, boolean isResource, String questionType,
                               List<AnswerDto> userAnswers, List<AnswerDto> correctAnswers) {
        return isSkipped ? 0 : (isResource ? 0 : calculateScoreByQuestionType(questionType, userAnswers,
                correctAnswers));
    }

    private int calculateScoreByQuestionType(String questionType, List<AnswerDto> userAnswers,
                                             List<AnswerDto> correctAnswers) {
        QuestionTypeEnum enumType = QuestionTypeEnum.getEnum(questionType);
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
        }
    }

    private ResourceDto findResourceInResourceList(List<ResourceDto> resources, UUID resourceId) {
        return resources.stream().filter(r -> r.getId().equals(resourceId)).findFirst().orElse(null);
    }

    private void validateProfileAttemptsLeft(ContextEntity context, UUID profileId, String authToken) {
        CollectionDto collectionDto = collectionService.getCollectionOrAssessment(context.getCollectionId(),
                context.getIsCollection(), authToken);
        int allowedAttempts =
                ((Double) collectionDto.getMetadata().getSetting(CollectionSetting.AttemptsAllowed, -1.0)).intValue();
        int contextAttempts = contextProfileService.findContextProfileIdsByContextIdAndProfileId(context.getContextId(),
                profileId).size();
        if (allowedAttempts != -1 && allowedAttempts <= contextAttempts) {
            throw new NoAttemptsLeftException("No attempts left for Profile ID " + profileId + " on Context ID "
                    + context.getContextId());
        }
    }

    private Map<UUID, String> getQuestionTypeMap(CollectionDto collectionDto) {
        return collectionDto.getResources().stream()
                .filter(resource -> !resource.getIsResource() && resource.getMetadata() != null)
                .collect(Collectors.toMap(ResourceDto::getId, resource -> resource.getMetadata().getType()));

    }

}
