package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.AnswerDto;
import com.quizzes.api.common.dto.ContextEventsResponseDto;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostRequestResourceDto;
import com.quizzes.api.common.dto.PostResponseResourceDto;
import com.quizzes.api.common.dto.ProfileEventResponseDto;
import com.quizzes.api.common.dto.QuestionDataDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.enums.QuestionTypeEnum;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.common.model.entities.AssigneeEventEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

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
    JsonParser jsonParser;

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
    Gson gson;

    public StartContextEventResponseDto startContextEvent(UUID contextId, UUID profileId) {
        try {
            Context context = contextService.findById(contextId);
            ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);

            List<ContextProfileEvent> events = new ArrayList<>();

            if (contextProfile == null) {
                contextProfile = new ContextProfile();
                contextProfile.setContextId(contextId);
                contextProfile.setProfileId(profileId);
                contextProfile = restartContextProfile(contextProfile);
            } else if (contextProfile.getIsComplete()) {
                contextProfileEventService.deleteByContextProfileId(contextProfile.getId());
                contextProfile = restartContextProfile(contextProfile);
            } else {
                events = contextProfileEventService.findByContextProfileId(contextProfile.getId());
            }

            CollectionDto collection = new CollectionDto();
            collection.setId(context.getCollectionId().toString());

            StartContextEventResponseDto result = new StartContextEventResponseDto();
            result.setId(contextId);
            result.setCurrentResourceId(contextProfile.getCurrentResourceId());
            result.setCollection(collection);
            result.setEventsResponse(events.stream().map(event ->
                    jsonParser.parseMap(event.getEventData())).collect(Collectors.toList()));

            return result;
        } catch (Exception e) {
            logger.error("We could not start the context " + contextId + " for user " + profileId, e);
            throw new InternalServerException("We could not start the context " + contextId + ".", e);
        }
    }

    private ContextProfile restartContextProfile(ContextProfile contextProfile) {
        Resource firstResource = findFirstResourceByContextId(contextProfile.getContextId());
        contextProfile.setCurrentResourceId(firstResource.getId());
        contextProfile.setIsComplete(false);
        return contextProfileService.save(contextProfile);
    }

    private Resource findFirstResourceByContextId(UUID contextId) {
        return resourceService.findFirstByContextIdOrderBySequence(contextId);
    }

    public void finishContextEvent(UUID contextId, UUID profileId) {
        try {
            ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);

            if (!contextProfile.getIsComplete()) {
                contextProfile.setIsComplete(true);
                contextProfileService.save(contextProfile);
            }
        } catch (Exception e) {
            logger.error("We could not finish the context " + contextId, e);
            throw new InternalServerException("We could not finish the context " + contextId + ".", e);
        }
    }

    public void onResourceEvent(UUID contextId, UUID resourceId, UUID profileId, OnResourceEventPostRequestDto body) {
        try {
            ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);
            Resource resource = resourceService.findById(resourceId);

            saveEvent(contextProfile.getId(), body);

            contextProfile.setCurrentResourceId(resource.getId());
            contextProfileService.save(contextProfile);
        } catch (Exception e) {
            logger.error("We could not register the event for the resource " + resourceId, e);
            throw new InternalServerException("We could not register the event for the resource " + resourceId + ".", e);
        }
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

    private void saveEvent(UUID contextProfileId, OnResourceEventPostRequestDto body) {
        PostRequestResourceDto resourceData = body.getPreviousResource();

        Resource previousResource = resourceService.findById(resourceData.getResourceId());

        QuestionDataDto previousResourceData = gson.fromJson(previousResource.getResourceData(), QuestionDataDto.class);

        ContextProfileEvent event = contextProfileEventService.
                findByContextProfileIdAndResourceId(contextProfileId, previousResource.getId());

        if (event == null) {
            event = new ContextProfileEvent();
            event.setContextProfileId(contextProfileId);
            event.setResourceId(previousResource.getId());
        }

        //Calculate score
        String questionType = previousResourceData.getType();
        List<AnswerDto> correctAnswers = previousResourceData.getCorrectAnswer();
        List<AnswerDto> userAnswers = resourceData.getAnswer();

        if (!userAnswers.isEmpty()) {
            resourceData.setIsSkipped(false);
            resourceData.setScore(calculateScoreByQuestionType(questionType, userAnswers, correctAnswers));
        }

        event.setEventData(gson.toJson(resourceData));
        contextProfileEventService.save(event);
    }

    private int calculateScoreByQuestionType(String questionType, List<AnswerDto> userAnswers, List<AnswerDto> correctAnswers) {
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

}
