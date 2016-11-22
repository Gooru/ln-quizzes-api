package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
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
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.entities.ContextByOwnerEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
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
import com.quizzes.api.config.GsonConfiguration;
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

    private Gson gson = new GsonConfiguration().gson();

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

    /**
     *
     * @param contextId the id of the context to update
     * @param contextPutRequestDto the assignees and contextData to update
     * @param lms the LMS id
     * @return the updated Context
     */
    public Context update(UUID contextId, ContextPutRequestDto contextPutRequestDto, Lms lms) {
        Gson gson = new Gson();
        Context context = contextRepository.findById(contextId);
        if (context == null) {
            logger.error("Error updating context: " + contextId + " was not found");
            throw new ContentNotFoundException("We couldn't find a context with id :" + contextId);
        }

        List<ProfileDto> profiles = contextPutRequestDto.getAssignees();
        List<UUID> requestExternalProfileIds = profiles.stream().map(profile -> UUID.fromString(profile.getId())).collect(Collectors.toList());
        //deletes all the groupProfiles for the group in 1 operation
        groupProfileService.clearGroup(context.getGroupId());

        //checks if the assignees exists, if not, creates the assignee profile
        if (profiles != null && !profiles.isEmpty()){
            List<UUID> foundExternalProfileIds = profileService.findExternalProfileIds(requestExternalProfileIds, lms);
            //we are creating new profiles
            //we are not updating existing info of existing profiles
            List<Profile> notFoundProfiles = profiles.stream()
                    .filter(profile -> !foundExternalProfileIds.contains(profile.getId()))
                    .map(profile -> {Profile newProfile = new Profile();
                                    newProfile.setExternalId(profile.getId());
                                    newProfile.setLmsId(lms);
                                    newProfile.setProfileData(profileDtoToJson(profile).toString());
                                    return newProfile;
                                    }).collect(Collectors.toList());
            profileService.save(notFoundProfiles);
        }
        //At this point all the assignees in the context to update exists
        //now we need to get the profileIds of that assignees to add them to the group
        List<UUID> profileIds = profileService.findProfileIdsByExternalIdAndLms(requestExternalProfileIds, lms);

        //adds all the assignees to the group
        profileIds.forEach(id -> {
            GroupProfile newGroupProfile = new GroupProfile();
            newGroupProfile.setGroupId(context.getGroupId());
            newGroupProfile.setProfileId(id);
            groupProfileService.save(newGroupProfile);
        });

        //Update ContextData
        ContextDataDto contextDataDto = gson.fromJson(context.getContextData(), ContextDataDto.class);
        contextDataDto.setMetadata(contextPutRequestDto.getContextData().getMetadata());
        context.setContextData(gson.toJson(contextDataDto));
        return contextRepository.save(context);
    }

    public StartContextEventResponseDto startContextEvent(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = findContextProfile(contextId, profileId);

        CollectionDto collection = new CollectionDto();
        collection.setId(String.valueOf(contextRepository.findCollectionIdByContextId(contextId)));

        List<ContextProfileEvent> attempts = contextProfileEventService.findAttemptsByContextProfileIdAndResourceId(
                contextProfile.getProfileId(), contextProfile.getCurrentResourceId());

        List<Map<String, Object>> list = convertContextProfileToJson(attempts);

        StartContextEventResponseDto result = new StartContextEventResponseDto(
                UUID.randomUUID(), collection, contextProfile.getCurrentResourceId(), list);
        return result;
    }

    public ContextGetResponseDto getContext(UUID contextId) {
        ContextOwnerEntity contextOwner = contextRepository.findContextAndOwnerByContextId(contextId);
        if (contextOwner != null) {
            ContextGetResponseDto response = new ContextGetResponseDto();
            List<UUID> assignees = profileService.findAssignedIdsByContextId(contextId);

            CollectionDto collectionDto = new CollectionDto();
            collectionDto.setId(contextOwner.getCollectionId().toString());

            response.setCollection(collectionDto);
            response.setId(contextId);
            String x = contextOwner.getContextData();
            response.setContextDataResponse(jsonParser.parseMap(contextOwner.getContextData()));

            IdResponseDto ownerId = new IdResponseDto();
            ownerId.setId(contextOwner.getOwnerId());
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

    public List<CreatedContextGetResponseDto> findCreatedContexts(UUID profileId) {
        List<CreatedContextGetResponseDto> result = new ArrayList<>();
        Map<UUID, List<ContextByOwnerEntity>> contextByOwnerList = contextRepository.findContextByOwnerId(profileId);

        if (contextByOwnerList != null && contextByOwnerList.entrySet() != null) {
            contextByOwnerList.forEach(
                    (key, value) -> {
                        CreatedContextGetResponseDto createdContextGetResponseDto = new CreatedContextGetResponseDto();
                        createdContextGetResponseDto.setId(key);
                        if (!value.isEmpty()) {
                            ContextByOwnerEntity firstEntryValue = value.get(0);
                            createdContextGetResponseDto.setContextResponse(jsonParser.parseMap(firstEntryValue.getContextData()));
                            CollectionDto collectionDto = new CollectionDto(firstEntryValue.getCollectionId().toString());
                            createdContextGetResponseDto.setCollection(collectionDto);
                            List<IdResponseDto> assignees = value.stream().map(profile -> {
                                IdResponseDto assignee = new IdResponseDto();
                                assignee.setId(profile.getAssigneeId());
                                return assignee;}).collect(Collectors.toList());
                            createdContextGetResponseDto.setAssignees(assignees);
                        }
                        result.add(createdContextGetResponseDto);

                    }
            );
        }
        return result;
    }

    public List<ContextAssignedGetResponseDto> getAssignedContexts(UUID profileId) {
        List<ContextOwnerEntity> contexts = contextRepository.findAssignedContextsByProfileId(profileId);
        return contexts.stream()
                .map(entity -> {
                    ContextAssignedGetResponseDto response = new ContextAssignedGetResponseDto();

                    CollectionDto collectionDto = new CollectionDto();
                    collectionDto.setId(entity.getCollectionId().toString());

                    response.setCollection(collectionDto);
                    response.setId(entity.getContextId());
                    response.setContextDataResponse(jsonParser.parseMap(entity.getContextData()));

                    IdResponseDto ownerId = new IdResponseDto();
                    ownerId.setId(entity.getOwnerId());
                    response.setOwner(ownerId);

                    return response;
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

            JsonObject jsonObject = profileDtoToJson(profileDto);

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

    private JsonObject profileDtoToJson(ProfileDto profileDto){
        JsonElement jsonElement = gson.toJsonTree(profileDto);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.remove("id");
        return jsonObject;
    }
}
