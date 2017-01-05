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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ContextService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ContextProfileService contextProfileService;

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

    @Autowired
    private Gson gson;

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

        List<ProfileDto> profileDtos = contextPutRequestDto.getAssignees();

        //checks if the assignees exists, if not, creates the assignee profile
        if (profileDtos != null && !profileDtos.isEmpty()) {
            List<String> requestExternalProfileIds =
                    profileDtos.stream().map(profile -> profile.getId()).collect(Collectors.toList());

            //we get all the assignees on that group
            List<GroupProfile> assignedGroupProfiles =
                    groupProfileService.findGroupProfilesByGroupId(context.getGroupId());
            List<UUID> assignedProfilesIds = assignedGroupProfiles.stream().map(groupProfile ->
                    groupProfile.getProfileId()).collect(Collectors.toList());

            List<Profile> foundProfiles = profileService.findProfilesByExternalIdAndLms(requestExternalProfileIds, lms);
            Map<String, Profile> foundProfilesMap = foundProfiles.stream().collect(
                    Collectors.toMap(Profile::getExternalId, Function.identity()));
            List<String> foundProfilesAssigned = foundProfiles.stream().filter(profile ->
                    assignedProfilesIds.contains(profile.getId())).map(profile ->
                    profile.getExternalId()).collect(Collectors.toList());

            List<ProfileDto> notAssignedProfileDtos = profileDtos.stream().filter(profileDto ->
                    !foundProfilesAssigned.contains(profileDto.getId())).collect(Collectors.toList());

            notAssignedProfileDtos.stream().forEach(profileDto -> {

                if (!foundProfilesMap.containsKey(profileDto.getId())) {
                    Profile newProfile = new Profile();
                    newProfile.setExternalId(profileDto.getId());
                    newProfile.setLmsId(lms);
                    // TODO We need to remove the hardcoded client ID and get it from the owner Profile
                    // This Client ID belongs to Gooru client
                    newProfile.setClientId(UUID.fromString("8d8068c6-71e3-46f1-a169-2fceb3ed674b"));
                    newProfile.setProfileData(removeIdFromProfileDto(profileDto).toString());
                    Profile createdProfile = profileService.save(newProfile);
                    foundProfilesMap.put(newProfile.getExternalId(), createdProfile);
                }
                Profile profileToAssign = foundProfilesMap.get(profileDto.getId());
                GroupProfile newGroupProfile = new GroupProfile();
                newGroupProfile.setProfileId(profileToAssign.getId());
                newGroupProfile.setGroupId(context.getGroupId());
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
                            createdContextGetResponseDto.setContextData(gson.fromJson(firstEntryValue.getContextData(),
                                    ContextDataDto.class));
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
        } else {
            throw new ContentNotFoundException("We could not find the context: " + contextId + ".");
        }
        return response;
    }

    public List<ContextAssignedGetResponseDto> getAssignedContexts(UUID assigneeId) {
        return contextRepository.findContextOwnerByAssigneeId(assigneeId).stream()
                .map(context -> mapContextOwnerEntityToContextAssignedDto(context))
                .collect(Collectors.toList());
    }

    public ContextAssignedGetResponseDto getAssignedContextByContextIdAndAssigneeId(UUID contextId, UUID assigneeId)
            throws ContentNotFoundException {
        ContextOwnerEntity context = contextRepository.findContextOwnerByContextIdAndAssigneeId(contextId, assigneeId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for ID: " + contextId +
                    " and Assignee ID: " + assigneeId);
        }

        return mapContextOwnerEntityToContextAssignedDto(context);
    }

    private ContextAssignedGetResponseDto mapContextOwnerEntityToContextAssignedDto(ContextOwnerEntity contextOwner) {
        ContextAssignedGetResponseDto contextAssigned = new ContextAssignedGetResponseDto();
        contextAssigned.setId(contextOwner.getId());
        contextAssigned.setCollection(new CollectionDto(contextOwner.getCollectionId().toString()));
        contextAssigned.setCreatedDate(contextOwner.getCreatedAt().getTime());
        contextAssigned.setHasStarted(contextOwner.getContextProfileId() != null);
        contextAssigned.setOwner(new IdResponseDto(contextOwner.getOwnerProfileId()));
        contextAssigned.setContextData(gson.fromJson(contextOwner.getContextData(), ContextDataDto.class));

        return contextAssigned;
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
