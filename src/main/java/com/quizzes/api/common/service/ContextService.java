package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.controller.AssignmentDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.entities.ContextAssigneeEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.content.CollectionContentService;
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

    public Context createContext(AssignmentDto assignmentDto, Lms lms) {
        Profile owner = findProfile(assignmentDto.getOwner(), lms);
        Collection collection =
                collectionContentService.createCollectionCopy(assignmentDto.getExternalCollectionId(), owner);

        if (collection != null) {
            Group group = groupService.createGroup(owner.getId());
            assignProfilesToGroup(group.getId(), assignmentDto.getAssignees(), lms);

            Context context = new Context();
            context.setCollectionId(collection.getId());
            context.setGroupId(group.getId());
            context.setContextData(new Gson().toJson(assignmentDto.getContextData()));

            return contextRepository.save(context);
        }

        return null;
    }

    public Context findById(UUID contextId) {
        return contextRepository.findById(contextId);
    }

    public Context update(UUID contextId, ContextPutRequestDto contextPutRequestDto, Lms lms) {
        Gson gson = new Gson();
        Context context = findById(contextId);
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
        ContextDataDto contextDataDto = gson.fromJson(context.getContextData(), ContextDataDto.class);
        contextDataDto.setMetadata(contextPutRequestDto.getContextData().getMetadata());
        contextDataDto.setMetadata(contextPutRequestDto.getContextData().getMetadata());
        context.setContextData(gson.toJson(contextDataDto));
        return contextRepository.save(context);
    }

    public ContextGetResponseDto getContext(UUID contextId) {
        ContextOwnerEntity contextOwner = contextRepository.findContextOwnerByContextId(contextId);
        if (contextOwner != null) {
            ContextGetResponseDto response = new ContextGetResponseDto();
            List<UUID> assignees = profileService.findAssignedIdsByContextId(contextId);

            CollectionDto collectionDto = new CollectionDto();
            collectionDto.setId(contextOwner.getCollectionId().toString());

            response.setCollection(collectionDto);
            response.setId(contextId);
            response.setContextDataResponse(jsonParser.parseMap(contextOwner.getContextData()));

            IdResponseDto ownerId = new IdResponseDto();
            ownerId.setId(contextOwner.getOwnerProfileId());
            response.setOwner(ownerId);

            response.setAssignees(assignees.stream()
                    .map(assigneeId -> {
                        IdResponseDto id = new IdResponseDto();
                        id.setId(assigneeId);
                        return id;
                    })
                    .collect(Collectors.toList()));

            return response;
        }

        logger.info("Getting context: " + contextId + " was not found");
        return null;
    }

    public List<Context> findContextByOwnerId(UUID profileId) {

        return contextRepository.findByOwnerId(profileId);

    }

    public List<CreatedContextGetResponseDto> findCreatedContexts(UUID ownerId) {
        List<CreatedContextGetResponseDto> result = new ArrayList<>();
        Map<UUID, List<ContextAssigneeEntity>> contextByOwnerList =
                contextRepository.findContextAssigneeByOwnerId(ownerId);

        if (contextByOwnerList != null && contextByOwnerList.entrySet() != null) {
            contextByOwnerList.forEach(
                    (key, value) -> {
                        CreatedContextGetResponseDto createdContextGetResponseDto = new CreatedContextGetResponseDto();
                        createdContextGetResponseDto.setId(key);
                        if (!value.isEmpty()) {
                            ContextAssigneeEntity firstEntryValue = value.get(0);
                            createdContextGetResponseDto.setContextResponse(jsonParser.parseMap(firstEntryValue.getContextData()));
                            CollectionDto collectionDto = new CollectionDto(firstEntryValue.getCollectionId().toString());
                            createdContextGetResponseDto.setCollection(collectionDto);
                            List<IdResponseDto> assignees = value.stream().map(profile -> {
                                IdResponseDto assignee = new IdResponseDto();
                                assignee.setId(profile.getAssigneeProfileId());
                                return assignee;
                            }).collect(Collectors.toList());
                            createdContextGetResponseDto.setAssignees(assignees);
                        }
                        result.add(createdContextGetResponseDto);

                    }
            );
        }
        return result;
    }

    public List<ContextAssignedGetResponseDto> getAssignedContexts(UUID assigneeId) {
        List<ContextOwnerEntity> contexts = contextRepository.findContextOwnerByAssigneeId(assigneeId);
        return contexts.stream()
                .map(context -> {
                    ContextAssignedGetResponseDto response = new ContextAssignedGetResponseDto();

                    CollectionDto collectionDto = new CollectionDto();
                    collectionDto.setId(context.getCollectionId().toString());

                    response.setCollection(collectionDto);
                    response.setId(context.getId());
                    response.setContextDataResponse(jsonParser.parseMap(context.getContextData()));

                    IdResponseDto ownerId = new IdResponseDto();
                    ownerId.setId(context.getOwnerProfileId());
                    response.setOwner(ownerId);

                    return response;
                })
                .collect(Collectors.toList());
    }

    private Profile findProfile(ProfileDto profileDto, Lms lmsId) {
        Profile profile = profileService.findByExternalIdAndLmsId(profileDto.getId(), lmsId);
        if (profile == null) {
            profile = new Profile();
            profile.setExternalId(profileDto.getId());
            profile.setLmsId(lmsId);

            JsonObject jsonObject = new Gson().toJsonTree(profileDto).getAsJsonObject();
            jsonObject.remove("id");

            profile.setProfileData(jsonObject.toString());
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
