package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
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

    @Autowired
    Gson gson;

    /**
     * Find the profile by ID
     *
     * @param profileId the profile id
     * @return The profile found
     */
    public Profile findById(UUID profileId) {
        return profileRepository.findById(profileId);
    }

    /**
     * Find the profile data by Id
     *
     * @param profileId the profile id
     * @return The profile data found
     */
    public ProfileDto findProfileDataById(UUID profileId) {
        Profile profile = profileRepository.findById(profileId);
        ProfileDto result = gson.fromJson(profile.getProfileData(), ProfileDto.class);
        result.setId(profileId.toString());
        return result;
    }

    /**
     * Find the profile ID
     *
     * @param externalId the user ID in the client
     * @param lms the client name in quizzes
     * @return The profile data found
     */
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
    public List<String> findExternalProfileIds(List<String> externalProfileIds, Lms lms) {
        return profileRepository.findExternalProfileIds(externalProfileIds, lms);
    }

    /**
     * Finds the list of external profile Ids that exist in the profile table
     *
     * @param externalProfileIds The list of external profile Ids to find
     * @param lms                the profile lms
     * @return The Id list of the found profiles
     */
    public List<UUID> findProfileIdsByExternalIdAndLms(List<String> externalProfileIds, Lms lms) {
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
