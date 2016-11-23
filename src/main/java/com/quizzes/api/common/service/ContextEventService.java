package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.content.CollectionContentService;
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


    public StartContextEventResponseDto startContextEvent(UUID contextId, UUID profileId) {
        Context context = contextService.findById(contextId);
        if (context != null) {
            ContextProfile contextProfile = contextProfileService.findContextProfileByContextIdAndProfileId(contextId, profileId);
            if (contextProfile == null) {
                Resource firstResource = resourceService.findFirstBySequenceByContextId(contextId);
                contextProfile = new ContextProfile();
                contextProfile.setContextId(contextId);
                contextProfile.setProfileId(profileId);
                contextProfile.setCurrentResourceId(firstResource.getId());
                contextProfile = contextProfileService.save(contextProfile);
            }

            //TODO: Implement the response functionality here (QZ-170)

            CollectionDto collection = new CollectionDto();
            collection.setId(String.valueOf(contextRepository.findCollectionIdByContextId(contextId)));

            List<ContextProfileEvent> attempts = contextProfileEventService.findAttemptsByContextProfileIdAndResourceId(
                    contextProfile.getProfileId(), contextProfile.getCurrentResourceId());

            List<Map<String, Object>> list = convertContextProfileToJson(attempts);

            return new StartContextEventResponseDto(
                    UUID.randomUUID(), collection, contextProfile.getCurrentResourceId(), list);
        }
        logger.error("Getting context: " + contextId + " was not found");
        throw new ContentNotFoundException("We couldn't find a context with id: " + contextId);
    }

    private List<Map<String, Object>> convertContextProfileToJson(List<ContextProfileEvent> attempts) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ContextProfileEvent context : attempts) {
            Map<String, Object> data = jsonParser.parseMap(context.getEventData());
            if (data.containsKey("answer") && data.get("answer").toString() != null) {
                List<Object> answers = jsonParser.parseList(data.get("answer").toString());
                data.put("answer", answers);
            } else {
                data.put("answer", new JSONArray());
            }
            list.add(data);
        }
        return list;
    }


}
