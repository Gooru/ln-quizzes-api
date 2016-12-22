package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextPostRequestDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ProfileDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.common.model.entities.ContextAssigneeEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.Group;
import com.quizzes.api.common.model.jooq.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.content.CollectionContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JsonParser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContextService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContextProfileService contextProfileService;

    @Autowired
    JsonParser jsonParser;

    @Autowired
    private Gson gson;

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

    /**
     * A {@link Collection} is the Quizzes representation of an Assessment in a Content Provider
     * and in the Content Provider there are original Assessments (an Assessment that it's create from scratch)
     * and copied Assessments (copied from another Assessment)
     * In an Original Assessment the Assessment ID is the same as the field Assessment parent ID, this means that
     * the Assessment has no parent or is not copied from other Assessment.
     * A copied Assessment has it's own unique ID and in the field parent ID has the ID of the Assessment it is being copied from
     * In Quizzes each {@link Collection} has it's owner, and that owner matches or represents a Content Provider owner.
     * A Quizzes owner can use an Assessment he owns in the Content Provider or a "Public" Assessment from a different owner
     * but in the case of Assessments owned by others then the Quizzes user should copy the Assessment and use that copy.
     * Some Examples are:
     * ++Collection ID = c1 with external ID (Assessment ID) = a1 external parent ID (parent Assessment ID) = a1 and owner ID = o1
     *   this is a Collection from an original Assessment, not copied
     * ++Collection ID = c2 with external ID (Assessment ID) = a2 external parent ID (parent Assessment ID) = a1 and owner ID = o2
     *   this is another's owner (ID = o2) copy of the Assessment ID = a1 and this is the parent Assessment ID
     * ++Collection ID = c4 with external ID (Assessment ID) = a4 external parent ID (parent Assessment ID) = a2 and owner ID = o3
     *   this is an owner's o3 copy of Assessment ID = a2
     * ++Collection ID = c5 with external ID (Assessment ID) = a5 external parent ID (parent Assessment ID) = a5 and owner ID = o4
     *   this is an owner's o4 own Assessment ID = a5
     *
     * @param contextPostRequestDto information to create the new {@link Context}
     * @param lms                   {@link Lms} of the {@link Collection} and the Owner and Assignees
     * @return The only value in the result is the context ID
     */
    public IdResponseDto createContext(ContextPostRequestDto contextPostRequestDto, Lms lms) {
        Profile owner = profileService.findByExternalIdAndLmsId(contextPostRequestDto.getOwner().getId(), lms);
        if (owner == null) {
            owner = createProfile(contextPostRequestDto.getOwner(), lms);
        }

        Collection collection = collectionService.findByOwnerProfileIdAndExternalParentId(owner.getId(), contextPostRequestDto.getExternalCollectionId());

        if (collection == null) {
            collection = collectionService.findByExternalId(contextPostRequestDto.getExternalCollectionId());
            if (collection == null
                    || (collection != null && !collection.getOwnerProfileId().equals(owner.getId()))){
                // the collection is noll OR the collection has a different owner
                collection = collectionContentService.createCollection(contextPostRequestDto.getExternalCollectionId(), owner);
            }
        }

        Group group = groupService.createGroup(owner.getId());
        assignProfilesToGroup(group.getId(), contextPostRequestDto.getAssignees(), lms);

        Context context = new Context();
        context.setCollectionId(collection.getId());
        context.setGroupId(group.getId());
        context.setContextData(gson.toJson(contextPostRequestDto.getContextData()));

        context = contextRepository.save(context);
        IdResponseDto result = new IdResponseDto();
        result.setId(context.getId());

        return result;
    }

    public Context findById(UUID contextId) {
        return contextRepository.findById(contextId);
    }

    public Context findByIdAndOwnerId(UUID contextId, UUID ownerId) {
        return contextRepository.findByIdAndOwnerId(contextId, ownerId);
    }

    /**
     * @param contextId            the id of the context to update
     * @param contextPutRequestDto the assignees and contextData to update
     * @param lms                  the LMS id
     * @return the updated Context
     */
    public Context update(UUID contextId, UUID profileId, ContextPutRequestDto contextPutRequestDto, Lms lms) {
        Context context = findByIdAndOwnerId(contextId, profileId);
        if (context == null) {
            logger.error("Error updating context: " + contextId + " was not found");
            throw new ContentNotFoundException("We couldn't find a context with id :" + contextId);
        }

        List<ProfileDto> profiles = contextPutRequestDto.getAssignees();
        //deletes all the groupProfiles for the group in 1 operation
        groupProfileService.delete(context.getGroupId());

        //checks if the assignees exists, if not, creates the assignee profile
        if (profiles != null && !profiles.isEmpty()) {
            List<String> requestExternalProfileIds = profiles.stream().map(profile -> profile.getId()).collect(Collectors.toList());
            List<String> foundExternalProfileIds = profileService.findExternalProfileIds(requestExternalProfileIds, lms);
            //we are creating new profiles
            //we are not updating existing info of existing profiles
            List<Profile> notFoundProfiles = profiles.stream()
                    .filter(profile -> !foundExternalProfileIds.contains(profile.getId()))
                    .map(profile -> {
                        Profile newProfile = new Profile();
                        newProfile.setExternalId(profile.getId());
                        newProfile.setLmsId(lms);

                        // TODO We need to remove the hardcoded client ID and get it from the owner Profile
                        // This Client ID belongs to Gooru client
                        newProfile.setClientId(UUID.fromString("8d8068c6-71e3-46f1-a169-2fceb3ed674b"));

                        newProfile.setProfileData(removeIdFromProfileDto(profile).toString());
                        return newProfile;
                    }).collect(Collectors.toList());
            profileService.save(notFoundProfiles);
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
        }

        //Update ContextData
        ContextDataDto contextDataDto = gson.fromJson(context.getContextData(), ContextDataDto.class);
        contextDataDto.setMetadata(contextPutRequestDto.getContextData().getMetadata());
        context.setContextData(gson.toJson(contextDataDto));
        return contextRepository.save(context);
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
                            createdContextGetResponseDto.setContextData(gson.fromJson(firstEntryValue.getContextData(), ContextDataDto.class));
                            CollectionDto collectionDto = new CollectionDto(firstEntryValue.getCollectionId().toString());
                            createdContextGetResponseDto.setCollection(collectionDto);
                            List<IdResponseDto> assignees = value.stream().map(profile -> {
                                IdResponseDto assignee = new IdResponseDto();
                                assignee.setId(profile.getAssigneeProfileId());
                                return assignee;
                            }).collect(Collectors.toList());
                            createdContextGetResponseDto.setAssignees(assignees);
                            createdContextGetResponseDto.setCreatedDate(firstEntryValue.getCreatedAt().getTime());
                            createdContextGetResponseDto.setModifiedDate(firstEntryValue.getCreatedAt().getTime());
                            //@TODO Change this value to getModifiedAt when it is available from the DB
                        }
                        result.add(createdContextGetResponseDto);

                    }
            );
        }
        return result;
    }

    public CreatedContextGetResponseDto findCreatedContextByContextIdAndOwnerId(UUID contextId, UUID ownerId) {
        Map<UUID, List<ContextAssigneeEntity>> result =
                contextRepository.findContextAssigneeByContextIdAndOwnerId(contextId, ownerId);
        if (result.isEmpty()) {
            logger.error("We could not find the context: " + contextId);
            throw new ContentNotFoundException("We could not find the context: " + contextId + ".");
        }

        try{
            CreatedContextGetResponseDto response = null;

            if (!result.isEmpty() && result.containsKey(contextId)) {
                response = new CreatedContextGetResponseDto();

                List<ContextAssigneeEntity> assigneeEntities = result.get(contextId);
                response.setId(contextId);

                ContextAssigneeEntity firstEntity = assigneeEntities.get(0);
                response.setContextData(gson.fromJson(firstEntity.getContextData(), ContextDataDto.class));

                CollectionDto collection = new CollectionDto();
                collection.setId(firstEntity.getCollectionId().toString());
                response.setCollection(collection);

                List<IdResponseDto> assignees = assigneeEntities.stream().map(profile -> {
                    IdResponseDto assignee = new IdResponseDto();
                    assignee.setId(profile.getAssigneeProfileId());
                    return assignee;
                }).collect(Collectors.toList());

                response.setAssignees(assignees);
                response.setCreatedDate(firstEntity.getCreatedAt().getTime());
                response.setModifiedDate(firstEntity.getUpdatedAt().getTime());
            }
            return response;
        } catch (Exception e) {
            logger.error("There was an error finding the context: " + contextId, e);
            throw new InternalServerException("There was an error finding the context: " + contextId + ".", e);
        }
    }

    public List<ContextAssignedGetResponseDto> getAssignedContexts(UUID assigneeId) {
        List<ContextOwnerEntity> contexts = contextRepository.findContextOwnerByAssigneeId(assigneeId);
        return contexts.stream()
                .map(this::mapContextOwnerEntityToContextAssignedDto)
                .collect(Collectors.toList());
    }

    public ContextAssignedGetResponseDto getAssignedContextByContextIdAndAssigneeId(UUID contextId, UUID assigneeId) {
        ContextOwnerEntity context = contextRepository.findContextOwnerByContextIdAndAssigneeId(contextId, assigneeId);
        return mapContextOwnerEntityToContextAssignedDto(context);
    }

    private ContextAssignedGetResponseDto mapContextOwnerEntityToContextAssignedDto(ContextOwnerEntity context) {
        ContextAssignedGetResponseDto response = new ContextAssignedGetResponseDto();

        CollectionDto collection = new CollectionDto();
        collection.setId(context.getCollectionId().toString());

        response.setCollection(collection);
        response.setId(context.getId());
        response.setCreatedDate(context.getCreatedAt().getTime());
        response.setContextData(gson.fromJson(context.getContextData(), ContextDataDto.class));

        IdResponseDto ownerId = new IdResponseDto();
        ownerId.setId(context.getOwnerProfileId());
        response.setOwner(ownerId);

        return response;
    }

    /**
     * Creates a new {@link Profile}
     *
     * @param profileDto Profile data
     * @param lmsId      Lms
     * @return the created Profile
     */
    private Profile createProfile(ProfileDto profileDto, Lms lmsId) {
        Profile profile = new Profile();
        profile.setExternalId(profileDto.getId());
        profile.setLmsId(lmsId);

        // TODO We need to remove the hardcoded client ID and get it from the owner Profile
        // This Client ID belongs to Gooru client
        profile.setClientId(UUID.fromString("8d8068c6-71e3-46f1-a169-2fceb3ed674b"));

        JsonObject jsonObject = removeIdFromProfileDto(profileDto);

        profile.setProfileData(jsonObject.toString());
        return profileService.save(profile);
    }

    private void assignProfilesToGroup(UUID groupId, List<ProfileDto> profiles, Lms lmsId) {
        for (ProfileDto profileDto : profiles) {
            Profile profile = profileService.findByExternalIdAndLmsId(profileDto.getId(), lmsId);
            if (profile == null) {
                profile = createProfile(profileDto, lmsId);
            }
            GroupProfile groupProfile = new GroupProfile();
            groupProfile.setGroupId(groupId);
            groupProfile.setProfileId(profile.getId());
            groupProfileService.save(groupProfile);
        }
    }

    private JsonObject removeIdFromProfileDto(ProfileDto profileDto) {
        JsonElement jsonElement = gson.toJsonTree(profileDto);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.remove("id");
        return jsonObject;
    }
}
