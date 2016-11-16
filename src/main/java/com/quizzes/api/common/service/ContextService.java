package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.entities.ContextByOwner;
import com.quizzes.api.common.model.entities.AssignedContextEntity;
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
import java.util.stream.Collectors;

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
        Profile owner = findProfile(assignmentDTO.getOwner(), lms);
        Collection collection =
                collectionContentService.createCollectionCopy(assignmentDTO.getExternalCollectionId(), owner);

        if (collection != null) {
            Group group = groupService.createGroup(owner.getId());
            assignProfilesToGroup(group.getId(), assignmentDTO.getAssignees(), lms);

            Context context = new Context();
            context.setCollectionId(collection.getId());
            context.setGroupId(group.getId());
            context.setContextData(new Gson().toJson(assignmentDTO.getContextData()));

            return contextRepository.save(context);
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

        List<ProfileDto> profiles = contextPutRequestDto.getAssignees();
        if (profiles != null && profiles.size() > 0) {
            List<UUID> contextProfileIds = contextProfileService.findContextProfileIdsByContextId(contextId);
            addContextProfiles(profiles, contextProfileIds, lms, contextId);
            deleteContextProfiles(profiles, contextProfileIds);
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

    public List<Context> findContextByOwnerId(UUID profileId) {

        return contextRepository.findByOwnerId(profileId);

    }

    public List<CreatedContextGetResponseDto> findCreatedContexts(UUID profileId) {
        List<CreatedContextGetResponseDto> result = new ArrayList<>();
        Map<UUID, List<ContextByOwner>> contextByOwnerList = contextRepository.findContextByOwnerId(profileId);

        for (Map.Entry<UUID, List<ContextByOwner>> contextByOwnerEntry : contextByOwnerList.entrySet()) {
            CreatedContextGetResponseDto createdContextGetResponseDto = new CreatedContextGetResponseDto();
            createdContextGetResponseDto.setId(contextByOwnerEntry.getKey());
            List<ContextByOwner> contextByOwnerValueList = contextByOwnerEntry.getValue();
            if (!contextByOwnerValueList.isEmpty()){
                ContextByOwner firstEntryValue = contextByOwnerValueList.get(0);
                createdContextGetResponseDto.setContextResponse(jsonParser.parseMap(firstEntryValue.getContextData()));
                CollectionDTO collectionDto = new CollectionDTO(firstEntryValue.getCollectionId().toString());
                createdContextGetResponseDto.setCollection(collectionDto);
                List<ProfileDto> assignees = new ArrayList<>();
                for (ContextByOwner  contextByOwnerValueEntry : contextByOwnerValueList){
                    ProfileDto assignee = new ProfileDto();
                    assignee.setId(contextByOwnerValueEntry.getAssigneeId().toString());
                    assignees.add(assignee);
                }
                createdContextGetResponseDto.setAssignees(assignees);
            }

            result.add(createdContextGetResponseDto);

        }

        return result;
    }

    public List<ContextAssignedGetResponseDto> getAssignedContexts(UUID profileId) {
        List<AssignedContextEntity> contexts = contextRepository.findAssignedContextsByProfileId(profileId);
        return contexts.stream()
                .map(entity -> {
                    Context context = entity.getContext();
                    Profile owner = entity.getOwner();

                    ContextAssignedGetResponseDto contextAssigned = new ContextAssignedGetResponseDto();
                    contextAssigned.setId(context.getId());
                    contextAssigned.setCollection(new CollectionDTO(context.getCollectionId().toString()));

                    Map<String, Object> contextDataMap = jsonParser.parseMap(context.getContextData());
                    contextAssigned.setContextResponse(contextDataMap);

                    Map<String, Object> ownerDataMap = jsonParser.parseMap(owner.getProfileData());
                    contextAssigned.setOwnerResponse(ownerDataMap);

                    return contextAssigned;
                })
                .collect(Collectors.toList());
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

    private Profile findProfile(ProfileDto profileDto, Lms lmsId) {
        Profile profile = profileService.findByExternalIdAndLmsId(profileDto.getId(), lmsId);
        if (profile == null) {
            profile = new Profile();
            profile.setExternalId(profileDto.getId());
            profile.setLmsId(lmsId);
            profile.setProfileData(new Gson().toJson(profileDto));
            profile = profileService.save(profile);
        }
        return profile;
    }

    private void assignProfilesToGroup(UUID groupId, List<ProfileDto> profiles, Lms lmsId) {
        for (ProfileDto profileDto : profiles) {
            Profile profile = findProfile(profileDto, lmsId);
            GroupProfile groupProfile = new GroupProfile();
            groupProfile.setGroupId(groupId);
            groupProfile.setProfileId(profile.getId());
            groupProfileService.save(groupProfile);
        }
    }

    private void deleteOldContextProfiles(List<UUID> idsToDelete) {
        for (UUID id : idsToDelete) {
            contextProfileService.delete(id);
        }
    }

    private void addContextProfiles(List<ProfileDto> profiles, List<UUID> contextProfileIds, Lms lmsId, UUID contextId) {
        List<ProfileDto> newProfiles = profiles.stream()
                .filter(e -> (contextProfileIds.stream()
                        .filter(d -> e.getId().equals(d.toString()))
                        .count()) < 1)
                .collect(Collectors.toList());

        for (ProfileDto profileDto : newProfiles) {
            Profile profile = findProfile(profileDto, lmsId);
            ContextProfile contextProfile = new ContextProfile();
            contextProfile.setContextId(contextId);
            contextProfile.setProfileId(profile.getId());
            contextProfileService.save(contextProfile);
        }
    }

    private void deleteContextProfiles(List<ProfileDto> profiles, List<UUID> contextProfileIds) {
        List<UUID> idsToDelete = contextProfileIds.stream()
                .filter(e -> (profiles.stream()
                        .filter(d -> d.getId().equals(e.toString()))
                        .count()) < 1)
                .collect(Collectors.toList());
        deleteOldContextProfiles(idsToDelete);
    }

}
