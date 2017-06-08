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

        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, false);
        List<TaxonomySummaryDto> taxonomySummaries = calculateTaxonomySummary(contextProfileEvents, collectionDto,
                eventSummary, false);
        ContextProfile savedContextProfile = saveContextProfile(contextProfileEntity.getContextProfileId(), resourceId,
                false, eventSummary, taxonomySummaries);

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
        EventSummaryDataDto eventSummary = calculateEventSummary(contextProfileEvents, true);
        List<TaxonomySummaryDto> taxonomySummaries = calculateTaxonomySummary(contextProfileEvents, collectionDto,
                eventSummary, true);
        // Save ContextProfile data
        ContextProfile savedContextProfile =
                saveContextProfile(contextProfile.getContextProfileId(), contextProfile.getCurrentResourceId(), true,
                        eventSummary, taxonomySummaries);
        // Save pending ContextProfileEvents
        pendingContextProfileEvents.stream().forEach(event -> contextProfileEventService.save(event));

        // If there is Class ID the event message is propagated
        if (context.getClassId() != null) {
            sendFinishContextEventMessage(context.getContextId(), contextProfile.getContextProfileId(), eventSummary);
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
        contextProfile.setEventSummaryData(gson.toJson(calculateEventSummary(Collections.EMPTY_LIST, false)));
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

            if (!eventDataDto.getIsResource() && (calculateSkipped || !eventDataDto.getIsSkipped())) {
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

    /**
     * A Collection can have a set of Taxonomies, we will call these Collection Taxonomy, also every Resource in the
     * Collection can have a set of Taxonomies.
     * This method summarizes the resources of the Collection by Taxonomy.
     * The Collection Taxonomy Summaries are general for the Collection, this means that all the Resources will be
     * summarized en every Collection Taxonomy.
     * The Resources that has Taxonomies not summarized in the Collection Taxonomy also are Summarized and grouped by
     * Taxonomy ID.
     * Both the Collection Taxonomy Summary List and the additional Taxonomy Summaries are returned by this calculation
     *
     * @param contextProfileEvents All the Events answered or skipped
     * @param calculateSkipped     If true the Events skipped are calculated in the summary
     * @param collectionDto        Contains the metadata (if exists) and the Collection Taxonomy List (if exists)
     * @param eventSummary         Since we already calculated this information and it is the same for each Collection
     *                             Taxonomy we will pass this as a parameter.
     * @return A List of Taxonomy Summaries, some for the Collection and some for individual Resources with Taxonomies
     * not in the Collection Taxonomy Map.
     */
    private List<TaxonomySummaryDto> calculateTaxonomySummary(List<ContextProfileEvent> contextProfileEvents,
                                                              CollectionDto collectionDto,
                                                              EventSummaryDataDto eventSummary,
                                                              boolean calculateSkipped) {

        // Calculating the collection taxonomy summary
        Map<String, TaxonomySummaryDto> collectionTaxonomyMap = calculateCollectionTaxonomy(collectionDto,
                contextProfileEvents == null ? Collections.EMPTY_LIST : contextProfileEvents, eventSummary);

        // Calculating additional resource's taxonomy

        // Converting ContextProfileEvents into a Map of Taxonomy with the List of Events for each Taxonomy
        // we pass the IDs of the Taxonomys already in the collection so we can exclude those from the calculation
        // sinse those are already summarized.
        Map<String, List<ContextProfileEvent>> eventsByTaxonomy = getEventsByTaxonomy(contextProfileEvents,
                collectionDto.getResources(), collectionTaxonomyMap.keySet());

        // Mapping the eventsByTaxonomy to a List of summarized DTOs
        List<TaxonomySummaryDto> eventTaxonomyList = mapEventsByTaxonomyToTaxonomySummaryList(eventsByTaxonomy,
                calculateSkipped);

        List<TaxonomySummaryDto> result = new ArrayList<>();
        result.addAll(collectionTaxonomyMap.values());
        result.addAll(eventTaxonomyList);

        return result;
    }

    /**
     * When there are a Taxonomy for the whole Collection this means that all the events in the Collection are summarized
     * for all and every "Collection Taxonomy" so all the summaries have the same values, the same Resources (all the resources)
     * and the difference is the Taxonomy ID
     *
     * @param collectionDto        Contains the metadata (if exists) and the Collection Taxonomy List (if exists)
     * @param contextProfileEvents each Event have a Resource ID.
     * @param eventSummary         Since we already calculated this information and it is the same for each Collection
     *                             Taxonomy we will pass this as a parameter.
     * @return The List on Collection Taxonomy Summaries
     */
    private Map<String, TaxonomySummaryDto> calculateCollectionTaxonomy(CollectionDto collectionDto,
                                                                        List<ContextProfileEvent> contextProfileEvents,
                                                                        EventSummaryDataDto eventSummary) {
        Map<String, TaxonomySummaryDto> result = new HashMap<>();
        if (collectionDto.getMetadata() != null && collectionDto.getMetadata().getTaxonomy() != null &&
                !collectionDto.getMetadata().getTaxonomy().isEmpty()) {
            // All the Event Resources in the ContextProfile are summarized in the Collection Taxonomy
            List<UUID> allEventResourceIds = contextProfileEvents.stream()
                    .map(ContextProfileEvent::getResourceId)
                    .collect(Collectors.toList());

            result.putAll(collectionDto.getMetadata().getTaxonomy().keySet().stream()
                    .map(key -> {
                        TaxonomySummaryDto taxonomySummaryDto = mapEventSummaryToTaxonomySummary(eventSummary);
                        taxonomySummaryDto.setTaxonomyId(key);
                        taxonomySummaryDto.setResources(allEventResourceIds);
                        return taxonomySummaryDto;
                    })
                    .collect(Collectors.toMap(TaxonomySummaryDto::getTaxonomyId, Function.identity())));
        }
        return result;
    }

    /**
     * This method receives a list of context events, each content event has an ID to a resource and each resource can have
     * a list of Standards (Taxonomy), this is optional.
     * We will return a Map of Standards (Taxonomy) with its {@link ContextProfileEvent}.
     *
     * @param contextProfileEvents  events with the resource ID
     * @param collectionResources   resources with an optional taxonomy map
     * @param collectionTaxonomyIds taxonomy IDs already in the collection, we need to exclude the events matching
     *                              this list because those are already summarized
     * @return a Map with the Taxonomy ID and the List of events in that taxonomy
     */
    private Map<String, List<ContextProfileEvent>> getEventsByTaxonomy(List<ContextProfileEvent> contextProfileEvents,
                                                                       List<ResourceDto> collectionResources,
                                                                       Set<String> collectionTaxonomyIds) {

        // Filtering the Collection Resources to exclude Resources already in the Collection Taxonomy
        Map<UUID, Set<String>> resourcesNotInCollectionTaxonomy = collectionResources.stream()
                .filter(resource -> (resource.getMetadata() != null)
                        && (resource.getMetadata().getTaxonomy() != null)
                        && (!collectionTaxonomyIds.containsAll(resource.getMetadata().getTaxonomy().entrySet()))
                )
                .collect(Collectors.toMap(ResourceDto::getId,
                        resource -> resource.getMetadata().getTaxonomy().keySet()));

        // Filtering ContextProfileEvents to exclude events with resources already in the Collection Taxonomy
        List<ContextProfileEvent> eventsWithTaxonomy = contextProfileEvents.stream()
                .filter(event -> resourcesNotInCollectionTaxonomy.keySet().contains(event.getResourceId()))
                .collect(Collectors.toList());

        // Mapping the ContextProfileEvent List into a Map of Taxonomies with ContextProfileEvents
        Map<String, List<ContextProfileEvent>> result = new HashMap<>();
        for (ContextProfileEvent event : eventsWithTaxonomy) {
            List<String> eventTaxonomyList = resourcesNotInCollectionTaxonomy.get(event.getResourceId()).stream()
                    .filter(taxonomyId -> !collectionTaxonomyIds.contains(taxonomyId))
                    .collect(Collectors.toList());
            for (String taxonomyId : eventTaxonomyList) {
                if (result.containsKey(taxonomyId)) {
                    List<ContextProfileEvent> eventsInTaxonomy = result.get(taxonomyId);
                    eventsInTaxonomy.add(event);
                } else {
                    List<ContextProfileEvent> eventsInTaxonomy = new ArrayList<>();
                    eventsInTaxonomy.add(event);
                    result.put(taxonomyId, eventsInTaxonomy);
                }
            }
        }
        return result;
    }

    /**
     * Maps the ContextProfileEvents by Taxonomy Map into a List of Taxonomy Summary DTOs
     *
     * @param eventsByTaxonomy a Map of Taxonomy IDs with the List of ContextProfileEvents in that Taxonomy
     * @param calculateSkipped skips the skipped events from the calculation if this is true.
     * @return
     */
    private List<TaxonomySummaryDto> mapEventsByTaxonomyToTaxonomySummaryList(
            Map<String, List<ContextProfileEvent>> eventsByTaxonomy, boolean calculateSkipped) {
        return eventsByTaxonomy.entrySet().stream()
                .map(entry -> {
                    EventSummaryDataDto eventSummaryByTaxonomy = this.calculateEventSummary(entry.getValue(), calculateSkipped);
                    TaxonomySummaryDto taxonomySummaryDto = mapEventSummaryToTaxonomySummary(eventSummaryByTaxonomy);
                    taxonomySummaryDto.setTaxonomyId(entry.getKey());
                    List<UUID> resourceIdListByTaxonomy = entry.getValue().stream()
                            .map(event -> event.getResourceId())
                            .distinct()
                            .collect(Collectors.toList());
                    taxonomySummaryDto.setResources(resourceIdListByTaxonomy);
                    return taxonomySummaryDto;
                })
                .collect(Collectors.toList());
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

}
