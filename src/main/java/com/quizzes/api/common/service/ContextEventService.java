package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import org.jooq.tools.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;

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


    public StartContextEventResponseDto startContextEvent(UUID contextId, UUID profileId) {
        Context context = validateContext(contextId);
        validateProfileInContext(contextId, profileId);

        ContextProfile contextProfile = contextProfileService.findContextProfileByContextIdAndProfileId(contextId, profileId);
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
                .findEventsByContextProfileId(contextProfile.getProfileId());

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

    private void validateProfileInContext(UUID contextId, UUID profileId) {
        Profile profile = profileService.findAssigneeInContext(contextId, profileId);
        if (profile == null) {
            logger.error("Getting profile: " + profileId + " was not found");
            throw new ContentNotFoundException("We couldn't find a profile with id: " + profileId
                    + " for context " + contextId);
        }
    }

    private List<Map<String, Object>> convertContextProfileToMap(List<ContextProfileEvent> events) {
        return events.stream().map(event -> {
            Map<String, Object> data = jsonParser.parseMap(event.getEventData());
            data.remove("id");
            data.put("resourceId", event.getId());
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
