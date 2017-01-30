package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.*;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.ContextAssigneeEntity;
import com.quizzes.api.core.model.entities.ContextOwnerEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.mappers.EntityMapper;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.CollectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    ContextProfileEventService contextProfileEventService;

    @Autowired
    ContextService contextService;

    @Autowired
    ContextRepository contextRepository;

    @Autowired
    CollectionService collectionContentService;

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
     * this is a Collection from an original Assessment, not copied
     * ++Collection ID = c2 with external ID (Assessment ID) = a2 external parent ID (parent Assessment ID) = a1 and owner ID = o2
     * this is another's owner (ID = o2) copy of the Assessment ID = a1 and this is the parent Assessment ID
     * ++Collection ID = c4 with external ID (Assessment ID) = a4 external parent ID (parent Assessment ID) = a2 and owner ID = o3
     * this is an owner's o3 copy of Assessment ID = a2
     * ++Collection ID = c5 with external ID (Assessment ID) = a5 external parent ID (parent Assessment ID) = a5 and owner ID = o4
     * this is an owner's o4 own Assessment ID = a5
     *
     * @param contextPostRequestDto information to create the new {@link Context}
     * @param lms                   {@link Lms} of the {@link Collection} and the Owner and Assignees
     * @return The only value in the result is the context ID
     */
    @Transactional
    public IdResponseDto createContext(ContextPostRequestDto contextPostRequestDto) {
        // TODO Replace this logic
        /*
        Profile owner = profileService.findByExternalIdAndLmsId(contextPostRequestDto.getOwner().getId(), lms);
        if (owner == null) {
            owner = createProfile(contextPostRequestDto.getOwner(), lms);
        }

        Collection collection = collectionService.findByOwnerProfileIdAndExternalParentId(owner.getId(), contextPostRequestDto.getExternalCollectionId());

        if (collection == null) {
            collection = collectionService.findByExternalId(contextPostRequestDto.getExternalCollectionId());
            if (collection == null
                    || (collection != null && !collection.getOwnerProfileId().equals(owner.getId()))) {
                // the collection is noll OR the collection has a different owner
                //collection = collectionContentService.createCollection(contextPostRequestDto.getExternalCollectionId(), owner);
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
        */
        return null;
    }

    /**
     * @param contextId            the id of the context to update
     * @param contextPutRequestDto the assignees and contextData to update
     * @param lms                  the LMS id
     * @return the updated Context
     */
    @Transactional
    public Context update(UUID contextId, UUID profileId, ContextPutRequestDto contextPutRequestDto) {
        // TODO Replace this logic
        /*
        Context context = findByIdAndOwnerId(contextId, profileId);

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
        */
        return null;
    }

    public Context findById(UUID contextId) {
        Context context = contextRepository.findById(contextId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId);
        }
        return context;
    }

    public Context findByIdAndOwnerId(UUID contextId, UUID ownerId) {
        ContextOwnerEntity contextOwner = contextRepository.findContextOwnerById(contextId);
        if (contextOwner == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId);
        }
        if (!contextOwner.getOwnerProfileId().equals(ownerId)) {
            throw new InvalidOwnerException("Invalid Owner ID: " + ownerId + " for Context ID: " + contextId);
        }
        return EntityMapper.mapContextEntityToContext(contextOwner);
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

    /**
     * Finds the list of all {@link ContextOwnerEntity} for an assignee based on four criteria.
     * 1 - assigneeId, mandatory
     * 2 - isActive flag, optional param, default is true
     * 3 - startDate, optional, default is null
     * 4 - dueDate, optional, default is null
     *
     * @param assigneeId      This is mandatory
     * @param isActive        if null, then the default is true, can't be used with startDate or dueDate
     * @param startDateMillis start date milliseconds, if not null the query looks for records with startDate >= than this param, can't be used with isActive
     * @param dueDateMillis   due date milliseconds, if not null the query looks for records with dueDate <= than this param, can't be used with isActive
     * @return the list of {@link ContextAssigneeEntity} found
     */

    public List<ContextAssignedGetResponseDto> getAssignedContexts(UUID assigneeId, Boolean isActive, Long startDateMillis, Long dueDateMillis) {
        return contextRepository.findContextOwnerByAssigneeIdAndFilters(assigneeId, isActive, startDateMillis, dueDateMillis).stream()
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

    public Context findByIdAndAssigneeId(UUID contextId, UUID assigneeId) {
        List<ContextAssigneeEntity> assigneeEntities = contextRepository.findContextAssigneeByContextId(contextId);
        if (assigneeEntities.isEmpty()) {
            throw new ContentNotFoundException("Context not found for ID: " + contextId);
        }
        ContextAssigneeEntity contextAssigneeEntity = assigneeEntities.stream()
                .filter(entity -> entity.getAssigneeProfileId().equals(assigneeId))
                .findAny()
                .orElse(null);
        if (contextAssigneeEntity == null) {
            throw new InvalidAssigneeException("Profile ID: " + assigneeId + " not assigned to the context ID: " + contextId);
        }
        Context result = new Context();
        result.setId(contextAssigneeEntity.getId());
        result.setCollectionId(contextAssigneeEntity.getCollectionId());
        result.setContextData(contextAssigneeEntity.getContextData());
        return result;
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

}
