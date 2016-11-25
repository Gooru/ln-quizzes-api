package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProfileRepository profileRepository;

    public Profile findById(UUID id) {
        return null;
    }

    public IdResponseDto findIdByExternalIdAndLmsId(String externalId, Lms lms) {
        UUID id = profileRepository.findIdByExternalIdAndLmsId(externalId, lms);
        IdResponseDto result = new IdResponseDto();
        result.setId(id);
        return result;
    }

    public Profile findByExternalIdAndLmsId(String externalId, Lms lms) {
        return profileRepository.findByExternalIdAndLmsId(externalId, lms);
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
    }

    public List<Profile> save(List<Profile> profiles) {
        return profileRepository.save(profiles);
    }

    public List<UUID> findAssignedIdsByContextId(UUID contextId) {
        return profileRepository.findAssignedIdsByContextId(contextId);
    }

    /**
     * Finds the list of external profile Ids that exist in the profile table
     *
     * @param externalProfileIds The list of external profile Ids to find
     * @param lms                the profile lms
     * @return The Id list of the found profiles
     */
    public List<UUID> findExternalProfileIds(List<UUID> externalProfileIds, Lms lms) {
        return profileRepository.findExternalProfileIds(externalProfileIds, lms);
    }

    /**
     * Finds the list of external profile Ids that exist in the profile table
     *
     * @param externalProfileIds The list of external profile Ids to find
     * @param lms                the profile lms
     * @return The Id list of the found profiles
     */
    public List<UUID> findProfileIdsByExternalIdAndLms(List<UUID> externalProfileIds, Lms lms) {
        return profileRepository.findProfileIdsByExternalIdAndLms(externalProfileIds, lms);
    }

    /**
     * Find the profile that exist in a context
     *
     * @param contextId The context parent
     * @param profileId the profile id
     * @return The profile found
     */
    public Profile findAssigneeInContext(UUID contextId, UUID profileId) {
        return profileRepository.findAssigneeInContext(contextId, profileId);
    }

}
