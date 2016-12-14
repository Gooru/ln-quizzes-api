package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ContextPostRequestDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
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
     * Creates a new context, if the {@link Collection} exists then creates a new {@link Context} using the same
     * Collection
     */
    /**
     * A {@link Collection} is the Quizzes representation of an Assessment in a Content Provider
     * and in the Content Provider are original Assessments (an Assessment that it's create from scratch)
     * and copied Assessments (copied from another Assessment)
     * In an Original Assessment the Assessment ID is the same as the field Assessment parent ID, this means that
     * the Assessment has no parent or is not copied from other Assessment.
     * A copied Assessment has it's own unique ID and in the field parent ID has the ID of the Assessment it is being copied from
     * In Quizzes each {@link Collection} has it's owner, and that owner matches or represents a Content Provider owner.
     * A Quizzes owner can use a Assessment he owns in the Content Provider or a "Public" Assessment from a different owner
     * but in the case of Assessments owned by others then the Quizzes user should copy the Assessment and use that copy.
     * Some Examples are:
     * ++Collection ID = c1 with external ID (Assessment ID) = a1 external parent ID (parent Assessment ID) = a1 and owner ID = o1
     *   this is an original Assessment, not copied
     *
     *   en gooru tengo a2 parent a1 o2
     *   lo publico
     *
     *
     *   PROBLEMA, soy o2
     *   en gooru tengo a1 es de o1
     *   lo veo en el search
     *   siendo o2 selecciono a1 en itslearning
     *   creo 1 collection, se copia a1 en a2 de o2
     *   publico a2
     *   busco y selecciono a2
     *   busca en gooru y se trae a2 que es de o2
     *   crea un collection con a2 de o2
     *
     *
     *
     *
     *
     *
     * ++Collection ID = c2 with external ID (Assessment ID) = a2 external parent ID (parent Assessment ID) = a2 and owner ID = o2
     *   since the owner is different but the parent id is the same then a copy of the Assessment is required
     * ++Collection ID = c3 with external ID (Assessment ID) = a3 external parent ID (parent Assessment ID) = a1 and owner ID = o2
     *   this is an owner's o2 second copy of Assessment ID = a1
     * ++Collection ID = c4 with external ID (Assessment ID) = a4 external parent ID (parent Assessment ID) = a2 and owner ID = o2
     *   this is an owner's o2 copy of Assessment ID = a2
     *
     * One input parameter is the external ID (Assessment ID) on the Content Provider
     * so in Quizzes we should use that ID as both external ID and also external parent ID
     * because a o2 user might create a {@link Collection} using a1 as external ID
     * by any of external ID o external parent ID and also by the Quizzes owner {@link com.quizzes.api.common.model.jooq.tables.pojos.Profile}
     * @param contextPostRequestDto  information about the new {@link Context}
     * @param lms {@link Lms} of the {@link Collection} and the Owner and Assignees
     * @return The only value in the result is the context ID
     */
    public IdResponseDto createContext(ContextPostRequestDto contextPostRequestDto, Lms lms) {
        Profile owner = findOrCreateProfile(contextPostRequestDto.getOwner(), lms);
        Collection collection = collectionService.findByExternalId(contextPostRequestDto.getExternalCollectionId());
        if (collection != null){
            if (!collection.getOwnerProfileId().equals(owner.getId())){
                // This means there is a Collection created but has a different owner
                // then this is not the correct collection
                collection = null;
            }
        }
        if (collection == null){
            collection = collectionService.findByExternalParentId(contextPostRequestDto.getExternalCollectionId());
            if (!collection.getOwnerProfileId().equals(owner.getId())){
                // Again this means there is a Collection created but has a different owner
                // then this is not the correct collection
                collection = null;
            }
        }

        if (collection == null){
            collection = collectionContentService.createCollection(contextPostRequestDto.getExternalCollectionId(), owner);
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

    /**
     *
     * @param contextId the id of the context to update
     * @param contextPutRequestDto the assignees and contextData to update
     * @param lms the LMS id
     * @return the updated Context
     */
    public Context update(UUID contextId, ContextPutRequestDto contextPutRequestDto, Lms lms) {
        Context context = findById(contextId);
        if (context == null) {
            logger.error("Error updating context: " + contextId + " was not found");
            throw new ContentNotFoundException("We couldn't find a context with id :" + contextId);
        }

        List<ProfileDto> profiles = contextPutRequestDto.getAssignees();
        //deletes all the groupProfiles for the group in 1 operation
        groupProfileService.delete(context.getGroupId());

        //checks if the assignees exists, if not, creates the assignee profile
        if (profiles != null && !profiles.isEmpty()){
            List<String> requestExternalProfileIds = profiles.stream().map(profile -> profile.getId()).collect(Collectors.toList());
            List<String> foundExternalProfileIds = profileService.findExternalProfileIds(requestExternalProfileIds, lms);
            //we are creating new profiles
            //we are not updating existing info of existing profiles
            List<Profile> notFoundProfiles = profiles.stream()
                    .filter(profile -> !foundExternalProfileIds.contains(profile.getId()))
                    .map(profile -> {Profile newProfile = new Profile();
                                    newProfile.setExternalId(profile.getId());
                                    newProfile.setLmsId(lms);
                                    newProfile.setProfileData(profileDtoToJsonObject(profile).toString());
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

    public ContextGetResponseDto getContext(UUID contextId) {
        ContextOwnerEntity contextOwner = contextRepository.findContextOwnerByContextId(contextId);
        if (contextOwner != null) {
            ContextGetResponseDto response = new ContextGetResponseDto();
            List<UUID> assignees = profileService.findAssignedIdsByContextId(contextId);

            CollectionDto collectionDto = new CollectionDto();
            collectionDto.setId(contextOwner.getCollectionId().toString());

            response.setCollection(collectionDto);
            response.setId(contextId);
            response.setContextData(gson.fromJson(contextOwner.getContextData(), ContextDataDto.class));

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

    public List<ContextAssignedGetResponseDto> getAssignedContexts(UUID assigneeId) {
        List<ContextOwnerEntity> contexts = contextRepository.findContextOwnerByAssigneeId(assigneeId);
        return contexts.stream()
                .map(context -> {
                    ContextAssignedGetResponseDto response = new ContextAssignedGetResponseDto();

                    CollectionDto collectionDto = new CollectionDto();
                    collectionDto.setId(context.getCollectionId().toString());

                    response.setCollection(collectionDto);
                    response.setId(context.getId());
                    response.setCreatedDate(context.getCreatedAt().getTime());
                    response.setContextData(gson.fromJson(context.getContextData(), ContextDataDto.class));

                    IdResponseDto ownerId = new IdResponseDto();
                    ownerId.setId(context.getOwnerProfileId());
                    response.setOwner(ownerId);

                    return response;
                })
                .collect(Collectors.toList());
    }

    /**
     * Looks for a {@link Profile} by External ID and {@link Lms}
     * if the profile doesn't exists the {@link Profile} is created
     * @param profileDto Profile data
     * @param lmsId Lms
     * @return the found or created Profile
     */
    private Profile findOrCreateProfile(ProfileDto profileDto, Lms lmsId) {
        Profile profile = profileService.findByExternalIdAndLmsId(profileDto.getId(), lmsId);
        if (profile == null) {
            profile = new Profile();
            profile.setExternalId(profileDto.getId());
            profile.setLmsId(lmsId);

            JsonObject jsonObject = profileDtoToJsonObject(profileDto);

            profile.setProfileData(jsonObject.toString());
            profile = profileService.save(profile);
        }
        return profile;
    }

    private void assignProfilesToGroup(UUID groupId, List<ProfileDto> profiles, Lms lmsId) {
        for (ProfileDto profileDto : profiles) {
            Profile profile = findOrCreateProfile(profileDto, lmsId);
            GroupProfile groupProfile = new GroupProfile();
            groupProfile.setGroupId(groupId);
            groupProfile.setProfileId(profile.getId());
            groupProfileService.save(groupProfile);
        }
    }

    private JsonObject profileDtoToJsonObject(ProfileDto profileDto){
        JsonElement jsonElement = gson.toJsonTree(profileDto);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.remove("id");
        return jsonObject;
    }
}
