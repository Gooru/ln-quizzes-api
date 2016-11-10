package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
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
public class ContextService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContextProfileService contextProfileService;

    @Autowired
    JsonParser jsonParser;

    @Autowired
    ContextProfileEventService contextProfileEventService;

    @Autowired
    ProfileService profileService;

    @Autowired
    ContextService contextService;

    @Autowired
    ContextRepository contextRepository;

    @Autowired
    CollectionService collectionService;

    @Autowired
    GroupService groupService;

    @Autowired
    GroupProfileService groupProfileService;

    @Autowired
    CollectionContentService collectionContentService;

    public Context createContext(AssignmentDTO assignmentDTO, Lms lms) {
        //Get OwnerProfile
        Profile owner = findProfile(assignmentDTO.getOwner(), lms);

        //Create a new copy of the collection
        //TODO: Go to gooru to get the collection in transform the result into a quizzes collection

        Collection collection =
                collectionContentService.createCollectionCopy(assignmentDTO.getExternalCollectionId(), owner);

        if (collection != null) {
            collection = collectionService.save(collection);

            Group group = groupService.createGroup(collection.getOwnerProfileId());
            assignProfilesToGroup(group.getId(), assignmentDTO.getAssignees(), lms);

            Context context = new Context(null, collection.getId(), group.getId(),
                    new Gson().toJson(assignmentDTO.getContextData()), null);
            context = contextRepository.save(context);

            return context;
        }

        return null;
    }

    public Context update(UUID contextId, ContextPutRequestDto contextPutRequestDto, Lms lms) {
        Gson gson = new Gson();
        Context context = contextRepository.findById(contextId);
        if (context == null) {
            logger.error("Error updating context: " + contextId + " was not found");
            throw new ContentNotFoundException("We couldn't find a context with id :" + contextId);
        }

        if(contextPutRequestDto.getAssignees() != null){
            List<UUID> contextProfileIds = contextProfileService.findContextProfileIdsByContextId(contextId);
            for (ProfileDTO profileDTO : contextPutRequestDto.getAssignees()) {
                if (!contextProfileIds.contains(UUID.fromString(profileDTO.getId()))) {
                    Profile profile = findProfile(profileDTO, lms);
                    contextProfileService.save(new ContextProfile(null, context.getId(), profile.getId(), null, null, null));
                } else {
                    contextProfileIds.remove(UUID.fromString(profileDTO.getId()));
                }
            }
            deleteOldContextProfiles(contextProfileIds);
        }

        //Update ContextData
        ContextDataDTO contextDataDTO = gson.fromJson(context.getContextData(), ContextDataDTO.class);
        contextDataDTO.setMetadata(contextPutRequestDto.getContextData().getMetadata());
        contextDataDTO.setMetadata(contextPutRequestDto.getContextData().getMetadata());
        context.setContextData(gson.toJson(contextDataDTO));
        return contextRepository.save(context);
    }

    public StartContextEventResponseDto startContextEvent(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = findContextProfile(contextId, profileId);

        CollectionDTO collection = new CollectionDTO();
        collection.setId(String.valueOf(contextRepository.findCollectionIdByContextId(contextId)));

        List<ContextProfileEvent> attempts = contextProfileEventService.findAttemptsByContextProfileIdAndResourceId(
                contextProfile.getProfileId(), contextProfile.getCurrentResourceId());

        List<Map<String, Object>> list = convertContextProfileToJson(attempts);

        StartContextEventResponseDto result = new StartContextEventResponseDto(
                UUID.randomUUID(), collection, contextProfile.getCurrentResourceId(), list);
        return result;
    }

    public Context getContext(UUID contextId) {

        //TODO: replace this by findById method
        return contextRepository.mockedFindById(contextId);

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

    private ContextProfile findContextProfile(UUID contextId, UUID profileId) {
        ContextProfile contextProfile =
                contextProfileService.findContextProfileByContextIdAndProfileId(contextId, profileId);
        if (contextProfile == null) {
            contextProfile = contextProfileService.save(new ContextProfile(null, contextId, profileId, null, null, null));
        }
        return contextProfile;
    }

    private Profile findProfile(ProfileDTO profileDTO, Lms lms) {
        Profile profile = profileService.findByExternalIdAndLmsId(profileDTO.getId(), lms);
        if (profile == null) {
            profile = profileService
                    .save(new Profile(null, profileDTO.getId(), lms, new Gson().toJson(profileDTO), null));
        }
        return profile;
    }

    private void assignProfilesToGroup(UUID groupId, List<ProfileDTO> profiles, Lms lms) {
        Profile profile = null;
        for (ProfileDTO profileDTO : profiles) {
            profile = findProfile(profileDTO, lms);
            groupProfileService.save(new GroupProfile(null, groupId, profile.getId(), null));
        }
    }

    private void deleteOldContextProfiles(List<UUID> idsToDelete) {
        for(UUID id : idsToDelete){
            contextProfileService.delete(id);
        }
    }


}
