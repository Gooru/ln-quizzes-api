package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.AnswerDto;
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

import java.util.List;
import java.util.Optional;
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
                                 PostRequestResourceDto answerResource, Long startTime, UUID eventId) {

        EventResourceContentDto stopEvent = createResourceEventDto(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, STOP, resource, answerResource, startTime, quizzesUtils.getCurrentTimestamp());
        analyticsRestClient.notifyEvent(stopEvent, token);
    }

    public void reactionCreate(UUID collectionId, UUID classId, UUID contextProfileId, UUID eventId, UUID profileId,
                               boolean isCollection, String token, String reaction, UUID resourceId, long time) {

        EventReactionContentDto playEvent = createReactionEventDto(collectionId, classId, contextProfileId,
                eventId, profileId, isCollection, token, reaction, resourceId, time);
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

        //This case is only for startContext
        if(resource == null){
            resource = getFirstResource(collection.getResources());
        }

        return EventResourceContentDto.builder()
                .eventId(eventId)
                .eventName(RESOURCE_PLAY)
                .session(createSessionEventDto(sessionId, token))
                .user(new UserEventContentDto(profileId))
                .context(createContextResourceEventDto(collection, classId, type, eventId, resource))
                .version(new VersionEventContentDto(configurationService.getAnalyticsVersion()))
                .payLoadObject(createResourcePayloadObject(type, resource, answerResource))
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    private ResourceDto getFirstResource(List<ResourceDto> resources) {
        return resources.stream().filter(r -> r.getSequence() == 1).findFirst().get();
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
                .answerObject(createAnswerObject(answerResource.getAnswer(), resource.getMetadata().getCorrectAnswer()))
                .build();
    }

    private List<AnswerObjectEventContent> createAnswerObject(List<AnswerDto> answers, List<AnswerDto> correctAnswer) {
        answers.stream().map(answer -> {
            return AnswerObjectEventContent.builder()
                    .text(answer.getValue())
                    .answerId(answer.getId())
                    .build();
        }).collect(Collectors.toList());
        return null;
    }

    private String getAttemptStatus(PostRequestResourceDto answerResource) {
        return answerResource.getIsSkipped() ? SKIPPED : (answerResource.getScore() == 100 ? CORRECT : INCORRECT);
    }

    private EventReactionContentDto createReactionEventDto(UUID collectionId, UUID classId, UUID sessionId, UUID eventId,
                                                           UUID profileId, boolean isCollection, String token,
                                                           String reaction, UUID resourceId, Long time) {
        CollectionDto collection = getCollection(collectionId, isCollection);
        SessionEventContentDto session = createSessionEventDto(sessionId, token);
        ContextReactionEventContentDto context = createContextReactionEventDto(collection, classId, reaction, eventId,
                resourceId);

        return EventReactionContentDto.builder()
                .eventId(UUID.randomUUID())
                .session(session)
                .user(new UserEventContentDto(profileId))
                .context(context)
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
                                                                         String type, UUID eventId, ResourceDto resource) {
        return ContextResourceEventContentDto.builder()
                .contentGooruId(resource.getId())
                .collectionType(collection.getIsCollection() ? QuizzesUtils.COLLECTION : QuizzesUtils.ASSESSMENT)
                .type(type)
                .parentEventId(eventId)
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
