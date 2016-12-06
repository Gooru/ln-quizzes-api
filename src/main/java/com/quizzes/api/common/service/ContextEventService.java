package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostRequestResourceDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.response.AnswerDto;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.utils.JsonUtil;
import org.jooq.tools.json.JSONArray;
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
    JsonUtil jsonUtil;

    @Autowired
    Gson gson;


    public StartContextEventResponseDto startContextEvent(UUID contextId, UUID profileId) {
        try {
            Context context = contextService.findById(contextId);
            ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);

            List<ContextProfileEvent> events = new ArrayList<>();

            if (contextProfile == null) {
                Resource firstResource = getFirstResourceByContextId(contextId);
                contextProfile = setCurrentResource(new ContextProfile(), contextId, profileId, firstResource);
            } else if (contextProfile.getIsComplete()) {
                contextProfileEventService.deleteByContextProfileId(contextProfile.getId());
                Resource firstResource = getFirstResourceByContextId(contextId);
                contextProfile = setCurrentResource(contextProfile, contextId, profileId, firstResource);
            } else {
                events = contextProfileEventService.findByContextProfileId(contextProfile.getId());
            }

            CollectionDto collection = new CollectionDto();
            collection.setId(context.getCollectionId().toString());

            StartContextEventResponseDto result = new StartContextEventResponseDto();
            result.setId(contextId);
            result.setCurrentResourceId(contextProfile.getCurrentResourceId());
            result.setCollection(collection);
            result.setEventsResponse(convertContextProfileToMap(events));

            return result;
        } catch (Exception e) {
            logger.error("We could not start the context " + contextId + " for user " + profileId, e);
            throw new InternalServerException("We could not start the context " + contextId + ".", e);
        }
    }

    private Resource getFirstResourceByContextId(UUID contextId) {
        return resourceService.findFirstBySequenceByContextId(contextId);
    }

    private ContextProfile setCurrentResource(ContextProfile contextProfile, UUID contextId,
                                              UUID profileId, Resource resource) {
        contextProfile.setContextId(contextId);
        contextProfile.setProfileId(profileId);
        contextProfile.setCurrentResourceId(resource.getId());
        contextProfile.setIsComplete(false);

        return contextProfileService.save(contextProfile);
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

    private void saveEvent(UUID contextProfileId, OnResourceEventPostRequestDto body) {
        PostRequestResourceDto resourceData = body.getPreviousResource();

        Resource previousResource = resourceService.findById(resourceData.getResourceId());
        Map<String, Object> previousResourceData = jsonParser.parseMap(previousResource.getResourceData());

        ContextProfileEvent event = contextProfileEventService.
                findByContextProfileIdAndResourceId(contextProfileId, previousResource.getId());

        if (event == null) {
            event = new ContextProfileEvent();
            event.setContextProfileId(contextProfileId);
            event.setResourceId(previousResource.getId());
        }

        JsonElement jsonAnswers = gson.toJsonTree(previousResourceData.get("correctAnswer"));
        JsonArray correctAnswers = jsonAnswers.getAsJsonArray();
        List<AnswerDto> answers = resourceData.getAnswer();

        //TODO: Add logic to calculate the score
        //resourceData.setScore();

        event.setEventData(gson.toJson(resourceData));

        contextProfileEventService.save(event);
    }

    private List<Map<String, Object>> convertContextProfileToMap(List<ContextProfileEvent> events) {
        return events.stream().map(event -> {
            Map<String, Object> data = jsonParser.parseMap(event.getEventData());
            if (data.containsKey("answer") && data.get("answer").toString() != null) {
                List<Object> answers = jsonParser.parseList(data.get("answer").toString());
                data.put("answer", answers);
            } else {
                data.put("answer", new JSONArray());
            }
            return data;
        }).collect(Collectors.toList());
    }

}
