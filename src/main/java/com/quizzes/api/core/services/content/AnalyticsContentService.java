package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.content.AnswerObjectEventContent;
import com.quizzes.api.core.dtos.content.ContextCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.ContextReactionEventContentDto;
import com.quizzes.api.core.dtos.content.ContextResourceEventContentDto;
import com.quizzes.api.core.dtos.content.EventCollectionContentDto;
import com.quizzes.api.core.dtos.content.EventReactionContentDto;
import com.quizzes.api.core.dtos.content.EventResourceContentDto;
import com.quizzes.api.core.dtos.content.PayloadObjectCollectionEventContentDto;
import com.quizzes.api.core.dtos.content.PayloadObjectResourceEventContentDto;
import com.quizzes.api.core.dtos.content.SessionEventContentDto;
import com.quizzes.api.core.dtos.content.UserEventContentDto;
import com.quizzes.api.core.dtos.content.VersionEventContentDto;
import com.quizzes.api.core.rest.clients.AnalyticsRestClient;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.util.QuizzesUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AnalyticsContentService {

    private final static String COLLECTION_PLAY = QuizzesUtils.COLLECTION.concat(".play");
    private final static String RESOURCE_PLAY = QuizzesUtils.COLLECTION.concat(".resource.play");
    private final static String REACTION_CREATE = "reaction.create";
    private final static String START = "start";
    private final static String STOP = "stop";
    private final static String SKIPPED = "skipped";
    private final static String CORRECT = "correct";
    private final static String INCORRECT = "incorrect";

    @Autowired
    private AnalyticsRestClient analyticsRestClient;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private QuizzesUtils quizzesUtils;

    public void collectionPlayStart(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                    boolean isCollection, String token, Long startTime) {

        EventCollectionContentDto playEvent = createCollectionEventDto(collectionId, classId, contextProfileId,
                contextProfileId, profileId, isCollection, token, START, startTime, null);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void collectionPlayStop(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                   boolean isCollection, String token, Long startTime) {

        EventCollectionContentDto stopEvent = createCollectionEventDto(collectionId, classId, contextProfileId,
                contextProfileId, profileId, isCollection, token, STOP, startTime, quizzesUtils.getCurrentTimestamp());
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void resourcePlayStart(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                  boolean isCollection, String token, ResourceDto resource, Long startTime, UUID eventId) {

        EventResourceContentDto playEvent = createResourceEventDto(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, START, resource, null, startTime, null);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    public void resourcePlayStop(UUID collectionId, UUID classId, UUID contextProfileId, UUID profileId,
                                 boolean isCollection, String token, ResourceDto resource,
                                 PostRequestResourceDto answerResource, Long startTime, Long endTime, UUID eventId) {

        EventResourceContentDto stopEvent = createResourceEventDto(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, STOP, resource, answerResource, startTime, endTime);
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void reactionCreate(UUID collectionId, UUID classId, UUID contextProfileId, UUID eventId, UUID profileId,
                               boolean isCollection, String token, String reaction, Long timestamp, UUID resourceId) {

        EventReactionContentDto playEvent = createReactionEventDto(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, reaction, resourceId, timestamp);
        analyticsRestClient.notifyEvent(playEvent, token);
    }

    private EventCollectionContentDto createCollectionEventDto(UUID collectionId, UUID classId, UUID sessionId,
                                                               UUID eventId, UUID profileId, boolean isCollection,
                                                               String token, String type, Long startTime, Long endTime) {
        CollectionDto collection = getCollection(collectionId, isCollection);

        return EventCollectionContentDto.builder()
                .eventId(eventId)
                .eventName(COLLECTION_PLAY)
                .session(createSessionEventDto(sessionId, token))
                .user(new UserEventContentDto(profileId))
                .context(createContextCollectionEventDto(collection, classId, type))
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .payLoadObject(new PayloadObjectCollectionEventContentDto(true))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private EventResourceContentDto createResourceEventDto(UUID collectionId, UUID classId, UUID sessionId, UUID eventId,
                                                           UUID profileId, boolean isCollection, String token, String type,
                                                           ResourceDto resource, PostRequestResourceDto answerResource,
                                                           Long startTime, Long endTime) {
        CollectionDto collection = getCollection(collectionId, isCollection);

        return EventResourceContentDto.builder()
                .eventId(eventId)
                .eventName(RESOURCE_PLAY)
                .session(createSessionEventDto(sessionId, token))
                .user(new UserEventContentDto(profileId))
                .context(createContextResourceEventDto(collection, classId, type, sessionId, resource))
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .payLoadObject(createResourcePayloadObject(type, resource, answerResource))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private PayloadObjectResourceEventContentDto createResourcePayloadObject(String type, ResourceDto resource,
                                                                             PostRequestResourceDto answerResource) {
        if (type.equals(START)) {
            return PayloadObjectResourceEventContentDto.builder().questionType(
                    quizzesUtils.getGooruQuestionType(resource.getMetadata().getType())).build();
        }
        return PayloadObjectResourceEventContentDto.builder()
                .questionType(quizzesUtils.getGooruQuestionType(resource.getMetadata().getType()))
                .attemptStatus(getAttemptStatus(answerResource))
                .taxonomyIds(getTaxonomyIds(resource))
                .answerObject(createAnswerObject(answerResource, resource.getMetadata().getCorrectAnswer(),
                        resource.getMetadata().getInteraction().getChoices()))
                .build();
    }

    private Map<String, String> getTaxonomyIds(ResourceDto resource) {
        Map<String, String> taxonomies = new HashMap<>();
        resource.getMetadata().getTaxonomy().forEach((taxonomyKey, taxonomyDescription) -> {
            Map<String, String> taxonomyValues = (Map<String, String>) taxonomyDescription;
            taxonomies.put(taxonomyKey, taxonomyValues.get("code"));
        });
        return taxonomies;
    }

    private List<AnswerObjectEventContent> createAnswerObject(PostRequestResourceDto userAnswer,
                                                              List<AnswerDto> correctAnswers,
                                                              List<ChoiceDto> possibleAnswers) {
        if(userAnswer.getAnswer() == null){
            return Collections.EMPTY_LIST;
        }

        // Define which Answer Object depending on Question Type

        final int[] order = {0};
        return userAnswer.getAnswer().stream().map(answer -> {
            order[0]++;
            return AnswerObjectEventContent.builder()
                    .text(quizzesUtils.decodeAnswer(answer.getValue()))
                    .timeStamp(userAnswer.getTimeSpent())
                    .skip(userAnswer.getIsSkipped())
                    .order(order[0])
                    .status(userAnswer.getIsSkipped() ? SKIPPED : (userAnswer.getScore() == 100 ? CORRECT : INCORRECT))
                    .build();
        }).collect(Collectors.toList());
    }

    private String getAttemptStatus(PostRequestResourceDto answerResource) {
        return answerResource.getIsSkipped() ? SKIPPED : (answerResource.getScore() == 100 ? CORRECT : INCORRECT);
    }

    private EventReactionContentDto createReactionEventDto(UUID collectionId, UUID classId, UUID sessionId, UUID eventId,
                                                           UUID profileId, boolean isCollection, String token,
                                                           String reaction, UUID resourceId, Long time) {
        CollectionDto collection = getCollection(collectionId, isCollection);

        return EventReactionContentDto.builder()
                .eventId(UUID.randomUUID())
                .session(createSessionEventDto(sessionId, token))
                .user(new UserEventContentDto(profileId))
                .context(createContextReactionEventDto(collection, classId, reaction, eventId, resourceId))
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .eventName(REACTION_CREATE)
                .startTime(time)
                .endTime(time)
                .build();
    }

    private CollectionDto getCollection(UUID collectionId, boolean isCollection) {
        return isCollection ?
                collectionService.getCollection(collectionId) : collectionService.getAssessment(collectionId);
    }

    private ContextCollectionEventContentDto createContextCollectionEventDto(CollectionDto collection, UUID classId,
                                                                             String type) {
        return ContextCollectionEventContentDto.builder()
                .contentGooruId(UUID.fromString(collection.getId()))
                .collectionType(collection.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .questionCount(collection.getIsCollection() ?
                        getQuestionCount(collection.getResources()) : collection.getResources().size())
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private ContextResourceEventContentDto createContextResourceEventDto(CollectionDto collection, UUID classId,
                                                                         String type, UUID collectionEventId, ResourceDto resource) {
        return ContextResourceEventContentDto.builder()
                .contentGooruId(resource.getId())
                .collectionType(collection.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .parentEventId(collectionEventId)
                .parentGooruId(UUID.fromString(collection.getId()))
                .resourceType(resource.getIsResource() ? QuizzesUtils.RESOURCE : QuizzesUtils.QUESTION)
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private ContextReactionEventContentDto createContextReactionEventDto(CollectionDto collection, UUID classId,
                                                                         String reaction, UUID eventId,
                                                                         UUID resourceId) {
        return ContextReactionEventContentDto.builder()
                .contentGooruId(resourceId)
                .parentEventId(eventId)
                .parentGooruId(UUID.fromString(collection.getId()))
                .reactionType(reaction)
                .unitGooruId(collection.getUnitId())
                .classGooruId(classId)
                .lessonGooruId(collection.getLessonId())
                .courseGooruId(collection.getCourseId())
                .build();
    }

    private int getQuestionCount(List<ResourceDto> resources) {
        return (int) resources.stream().filter(resource -> !resource.getIsResource()).count();
    }

    private SessionEventContentDto createSessionEventDto(UUID contextProfileId, String token) {
        return SessionEventContentDto.builder()
                .apiKey(UUID.fromString(configurationService.getApiKey()))
                .sessionId(contextProfileId)
                .sessionToken(token).build();
    }

}
