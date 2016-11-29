package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.ResourceCommonDto;
import com.quizzes.api.common.dto.ResourcePostRequestDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.dto.controller.response.AnswerDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.utils.JsonUtil;
import org.jooq.tools.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

import java.io.StringReader;
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
        Context context = validateContext(contextId);
        validateProfileInContext(contextId, profileId);

        ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);
        //TODO: If context_profile is complete we need to remove all the events

        if (contextProfile == null) {
            Resource firstResource = resourceService.findFirstBySequenceByContextId(contextId);
            contextProfile = new ContextProfile();
            contextProfile.setContextId(contextId);
            contextProfile.setProfileId(profileId);
            contextProfile.setCurrentResourceId(firstResource.getId());
            contextProfile = contextProfileService.save(contextProfile);
        }

        CollectionDto collection = new CollectionDto();
        collection.setId(context.getCollectionId().toString());

        List<ContextProfileEvent> events = contextProfileEventService
                .findByContextProfileId(contextProfile.getId());

        StartContextEventResponseDto result = new StartContextEventResponseDto();
        result.setId(contextId);
        result.setCurrentResourceId(contextProfile.getCurrentResourceId());
        result.setCollection(collection);
        result.setEventsResponse(convertContextProfileToMap(events));
        return result;
    }

    private Context validateContext(UUID contextId) {
        Context context = contextService.findById(contextId);
        if (context == null) {
            logger.error("Getting context: " + contextId + " was not found");
            throw new ContentNotFoundException("We couldn't find a context with id: " + contextId);
        }
        return context;
    }

    public void finishContextEvent(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = validateContextProfile(contextId, profileId);

        if (!contextProfile.getIsComplete()) {
            contextProfile.setIsComplete(true);
            contextProfileService.save(contextProfile);
        }
    }

    public void onResourceEvent(UUID contextId, UUID resourceId, UUID profileId, OnResourceEventPostRequestDto body) {
        ContextProfile contextProfile = validateContextProfile(contextId, profileId);
        Resource resource = validateResource(resourceId);
        saveEvent(contextProfile, body);

        contextProfile.setCurrentResourceId(resource.getId());
        contextProfileService.save(contextProfile);
    }

    private void validateProfileInContext(UUID contextId, UUID profileId) {
        Profile profile = profileService.findAssigneeInContext(contextId, profileId);
        if (profile == null) {
            logger.error("Getting profile: " + profileId + " was not found");
            throw new ContentNotFoundException("We couldn't find a profile with id: " + profileId
                    + " for context " + contextId);
        }
    }

    private Resource validateResource(UUID resourceId) {
        Resource resource = resourceService.findById(resourceId);
        if (resource == null) {
            logger.error("Getting resource: " + resourceId + " was not found");
            throw new ContentNotFoundException("We couldn't find a resource with id: " + resourceId);
        }
        return resource;
    }

    private ContextProfile validateContextProfile(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = contextProfileService.findByContextIdAndProfileId(contextId, profileId);
        if (contextProfile == null) {
            logger.error("Getting context_profile: " + contextId + " was not found");
            throw new ContentNotFoundException("We couldn't find a context with id: " + contextId + " for this user.");
        }
        return contextProfile;
    }

    private void saveEvent(ContextProfile contextProfile, OnResourceEventPostRequestDto body) {
        ResourcePostRequestDto resourceData = body.getPreviousResource();

        Resource previousResource = validateResource(resourceData.getResourceId());
        Map<String, Object> previousResourceData = jsonParser.parseMap(previousResource.getResourceData());

        ContextProfileEvent event = contextProfileEventService.
                findByContextProfileIdAndResourceId(contextProfile.getId(), previousResource.getId());

        if(event == null){
            event = new ContextProfileEvent();
            event.setContextProfileId(contextProfile.getId());
            event.setResourceId(previousResource.getId());
        }

        //TODO: Add logic to calculate the score

        JsonElement jsonAnswers = gson.toJsonTree(previousResourceData.get("correctAnswer"));
        JsonArray correctAnswers = jsonAnswers.getAsJsonArray();
        List<AnswerDto> answers = resourceData.getAnswer();
        resourceData.setScore(100);
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
