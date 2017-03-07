package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.ContextPutRequestDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.ClassMemberService;
import com.quizzes.api.core.services.content.CollectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ContextService {

    @Autowired
    private ContextProfileService contextProfileService;

    @Autowired
    private ContextRepository contextRepository;

    @Autowired
    private ClassMemberService classMemberService;

    @Autowired
    private CollectionService collectionService;

    @Autowired
    private Gson gson;

    @Transactional
    public UUID createContext(ContextPostRequestDto contextDto, UUID profileId, String token) throws
            InvalidOwnerException {
        validateCollectionOwnerInContext(profileId, contextDto.getCollectionId(), contextDto.getIsCollection());

        Context context = createContextObject(contextDto, profileId);
        List<UUID> assigneeIds = new ArrayList<>();

        if (contextDto.getClassId() != null) {
            assigneeIds.addAll(classMemberService.getClassMemberIds(contextDto.getClassId(), token));
        }

        // Saves all processed data
        Context savedContext = contextRepository.save(context);
        if (!assigneeIds.isEmpty()) {
            assigneeIds.forEach(assigneeId ->
                    contextProfileService.save(createContextProfileObject(savedContext.getId(), assigneeId)));
        }

        return savedContext.getId();
    }

    /**
     * Only saves collectionId, profileId and isCollection for anonymous contexts
     *
     * @param collectionId collection ID
     * @param profileId    we use an UUID with zeros for anonymous
     * @return the context ID
     */
    @Transactional
    public UUID createContextWithoutClassId(UUID collectionId, UUID profileId) {
        CollectionDto collection = collectionService.getCollectionOrAssessment(collectionId);
        Context context = new Context();
        context.setCollectionId(collectionId);
        context.setProfileId(profileId);
        context.setIsCollection(collection.getIsCollection());

        Context savedContext = contextRepository.save(context);
        contextProfileService.save(createContextProfileObject(savedContext.getId(), profileId));
        return savedContext.getId();
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

    public List<ContextEntity> findCreatedContexts(UUID profileId) {
        return contextRepository.findCreatedContextsByProfileId(profileId);
    }

    public ContextEntity findCreatedContext(UUID contextId, UUID profileId) throws ContentNotFoundException {
        ContextEntity context = contextRepository.findCreatedContextByContextIdAndProfileId(contextId, profileId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId
                    + " and Owner Profile ID: " + profileId);
        }
        return context;
    }

    public List<AssignedContextEntity> findAssignedContexts(UUID profileId) {
        return contextRepository.findAssignedContextsByProfileId(profileId);
    }

    public AssignedContextEntity findAssignedContext(UUID contextId, UUID profileId) throws ContentNotFoundException {
        AssignedContextEntity context =
                contextRepository.findAssignedContextByContextIdAndProfileId(contextId, profileId);
        if (context == null) {
            throw new ContentNotFoundException("Context not found for Context ID: " + contextId
                    + " and Assignee Profile ID: " + profileId);
        }
        return context;
    }

    public List<ContextEntity> findMappedContext(UUID classId, UUID collectionId, Map<String, String> contextMap,
                                                 UUID profileId, String token) throws InvalidAssigneeException {
        if (!classMemberService.containsMemberId(classId, profileId, token)) {
            throw new InvalidAssigneeException("Profile Id: " + profileId + " is not a valid Assignee " +
                    "(member of the Class Id: " + classId + ")");
        }
        return contextRepository.findMappedContexts(classId, collectionId, contextMap);
    }

    private void validateCollectionOwnerInContext(UUID profileId, UUID collectionId, boolean isCollection)
            throws InvalidOwnerException {
        UUID ownerId = isCollection ?
                collectionService.getCollection(collectionId).getOwnerId() :
                collectionService.getAssessment(collectionId).getOwnerId();

        if (!ownerId.equals(profileId)) {
            throw new InvalidOwnerException("Profile ID: " + profileId + " is not the owner of the collection ID: " +
                    collectionId);
        }
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

    private ContextProfile createContextProfileObject(UUID contextId, UUID profileId) {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setContextId(contextId);
        contextProfile.setProfileId(profileId);
        contextProfile.setIsComplete(false);
        contextProfile.setEventSummaryData(gson.toJson(new EventSummaryDataDto()));
        return contextProfile;
    }

}
