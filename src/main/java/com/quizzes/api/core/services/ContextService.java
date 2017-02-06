package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.ContextPutRequestDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.dtos.content.CollectionContentDto;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextAssigneeEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.entities.ContextOwnerEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.model.mappers.EntityMapper;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.rest.clients.AssessmentRestClient;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import com.quizzes.api.core.rest.clients.ClassMemberRestClient;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
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

    @Autowired
    ContextProfileService contextProfileService;

    @Autowired
    ContextProfileEventService contextProfileEventService;

    @Autowired
    ContextService contextService;

    @Autowired
    ContextRepository contextRepository;

    @Autowired
    CollectionRestClient collectionRestClient;

    @Autowired
    AssessmentRestClient assessmentRestClient;

    @Autowired
    ClassMemberRestClient classMemberRestClient;

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    @Autowired
    private Gson gson;

    @Transactional
    public IdResponseDto createContext(ContextPostRequestDto contextDto, UUID profileId, String token) {
        //TODO: Validate collection, class if exist, profile (could be anonymous)
        validateCollectionOwner(contextDto.getCollectionId(), contextDto.getIsCollection(), profileId, token);

        Context context = createContextObject(contextDto, profileId);
        context = contextRepository.save(context);

        if (contextDto.getClassId() != null) {
            ClassMemberContentDto classMember =
                    classMemberRestClient.getClassMembers(contextDto.getClassId().toString(), token);
            createContextProfiles(classMember.getMemberIds(), context.getId());
        }

        return new IdResponseDto(context.getId());
    }

    private Context createContextObject(ContextPostRequestDto contextDto, UUID profileId) {
        Context context = new Context();
        context.setProfileId(profileId);
        context.setClassId(contextDto.getClassId());
        context.setCollectionId(contextDto.getCollectionId());
        context.setContextData(gson.toJson(contextDto.getContextData()));
        context.setIsCollection(contextDto.getIsCollection());
        return context;
    }

    private UUID getCollectionOwnerId(String collectionId, boolean isCollection, String token) {
        CollectionContentDto collectionContentDto = isCollection ?
                collectionRestClient.getCollection(collectionId, token) :
                assessmentRestClient.getAssessment(collectionId, token);

        return collectionContentDto.getOwnerId();
    }

    private void validateCollectionOwner(UUID collectionId, boolean isCollection, UUID profileId, String token) {
        UUID ownerId = getCollectionOwnerId(collectionId.toString(), isCollection, token);
        if (!ownerId.equals(profileId)) {
            throw new InvalidOwnerException("Profile ID: " + profileId + " is not the owner of the collection ID:" +
                    collectionId + ".");
        }
    }

    private void createContextProfiles(List<UUID> memberIds, UUID contextId) {
        if (memberIds != null && !memberIds.isEmpty()) {
            memberIds.forEach(memberId -> {
                ContextProfile contextProfile = createContextProfile(contextId, memberId);
                contextProfileService.save(contextProfile);
            });
        }
    }

    private ContextProfile createContextProfile(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setContextId(contextId);
        contextProfile.setProfileId(profileId);
        contextProfile.setIsComplete(false);
        contextProfile.setEventSummaryData(gson.toJson(new EventSummaryDataDto()));
        return contextProfile;
    }

    /**
     * @param contextId            the id of the context to update
     * @param contextPutRequestDto the assignees and contextData to update
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

    public List<ContextGetResponseDto> findCreatedContexts(UUID profileId) {
        List<ContextEntity> contexts = contextRepository.findCreatedContextsByProfileId(profileId);
        return contexts.stream().map(context -> mapContextEntityToContextGetResponseDto(context))
                .collect(Collectors.toList());
    }

    public ContextGetResponseDto findCreatedContext(UUID contextId, UUID profileId)
            throws ContentNotFoundException, InvalidOwnerException {
        ContextEntity context = contextRepository.findCreatedContextByContextIdAndProfileId(contextId, profileId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId
                    + " and Owner Profile ID: " + profileId);
        }
        return mapContextEntityToContextGetResponseDto(context);
    }

    public List<ContextGetResponseDto> findAssignedContexts(UUID profileId) {
        List<AssignedContextEntity> contexts = contextRepository.findAssignedContextsByProfileId(profileId);
        return contexts.stream().map(context -> mapAssignedContextEntityToContextGetResponseDto(context))
                .collect(Collectors.toList());
    }

    public ContextGetResponseDto findAssignedContext(UUID contextId, UUID profileId)
            throws ContentNotFoundException, InvalidOwnerException {
        AssignedContextEntity context =
                contextRepository.findAssignedContextByContextIdAndProfileId(contextId, profileId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId
                    + " and Assignee Profile ID: " + profileId);
        }
        return mapAssignedContextEntityToContextGetResponseDto(context);
    }



    public ContextGetResponseDto getAssignedContextByContextIdAndAssigneeId(UUID contextId, UUID assigneeId)
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

    private ContextGetResponseDto mapContextOwnerEntityToContextAssignedDto(ContextOwnerEntity contextOwner) {
        ContextGetResponseDto contextAssigned = new ContextGetResponseDto();
        // TODO Fix this
        /*
        contextAssigned.setId(contextOwner.getId());
        contextAssigned.setCollection(new CollectionDto(contextOwner.getCollectionId().toString()));
        contextAssigned.setCreatedDate(contextOwner.getCreatedAt().getTime());
        contextAssigned.setHasStarted(contextOwner.getContextProfileId() != null);
        contextAssigned.setOwner(new IdResponseDto(contextOwner.getOwnerProfileId()));
        contextAssigned.setContextData(gson.fromJson(contextOwner.getContextData(), ContextDataDto.class));
        */

        return contextAssigned;
    }

    private ContextGetResponseDto mapContextEntityToContextGetResponseDto(ContextEntity context) {
        ContextGetResponseDto createdContext = new ContextGetResponseDto();
        createdContext.setContextId(context.getId());
        createdContext.setCollectionId(context.getCollectionId());
        createdContext.setIsCollection(context.getIsCollection());
        createdContext.setClassId(context.getClassId());
        createdContext.setIsActive(context.getIsActive());
        createdContext.setStartDate(context.getStartDate() != null ? context.getStartDate().getTime() : 0);
        createdContext.setDueDate(context.getDueDate() != null ? context.getDueDate().getTime() : 0);
        createdContext.setContextData(gson.fromJson(context.getContextData(), ContextDataDto.class));
        createdContext.setCreatedDate(context.getCreatedAt().getTime());
        createdContext.setModifiedDate(context.getUpdatedAt().getTime());
        return createdContext;
    }

    private ContextGetResponseDto mapAssignedContextEntityToContextGetResponseDto(AssignedContextEntity context) {
        ContextGetResponseDto createdContext = mapContextEntityToContextGetResponseDto(context);
        createdContext.setProfileId(context.getProfileId());
        createdContext.setHasStarted(context.getContextProfileId() != null);
        createdContext.setIsActive(null);
        createdContext.setModifiedDate(null);
        return createdContext;
    }

}
