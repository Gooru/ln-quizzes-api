package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ProfileGetResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class ProfileService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    Gson gson;

    public Profile findById(UUID profileId) {
        Profile profile = profileRepository.findById(profileId);
        if (profile == null) {
            logger.error("We could not find the profile with ID: " + profileId);
            throw new ContentNotFoundException("We could not find the profile with ID: " + profileId + ".");
        }
        return profile;
    }

    public ProfileGetResponseDto findProfileResponseDtoById(UUID profileId, ArrayList<String> fieldsToReturn) throws ClassNotFoundException {
        Profile profile = findById(profileId);
        try {
            ProfileGetResponseDto result = gson.fromJson(profile.getProfileData(), ProfileGetResponseDto.class);
            result.setId(profileId.toString());
            result.setExternalId(profile.getExternalId());

            if (fieldsToReturn != null) {
                ArrayList<Field> profileFields = new ArrayList();
                profileFields.addAll(Arrays.asList(result.getClass().getSuperclass().getDeclaredFields()));
                profileFields.addAll(Arrays.asList(result.getClass().getDeclaredFields()));
                result = (ProfileGetResponseDto) returnObjectWithFieldsInList(profileFields, fieldsToReturn, result);
            }

            return result;
        } catch (Exception e) {
            logger.error("There was an error finding the profile ID: " + profileId, e);
            throw new InternalServerException("There was an error finding the profile ID: " + profileId + ".", e);
        }
    }

    private Object returnObjectWithFieldsInList(ArrayList<Field> objectFields,
                                               ArrayList<String> fieldsToReturn,
                                               Object object) {
        objectFields.stream().filter(property -> !fieldsToReturn.contains(property.getName())).forEach(property -> {
            property.setAccessible(true);
            try {
                property.set(object, null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        return object;
    }

    public UUID findIdByExternalIdAndLmsId(String externalId, Lms lms) {
        UUID profileId = profileRepository.findIdByExternalIdAndLmsId(externalId, lms);
        if(profileId == null){
            throw new ContentNotFoundException("Profile with external ID: " + externalId + " was not found");
        }
        return profileId;
    }

    public UUID findIdByExternalIdAndClientId(String externalId, UUID clientId) {
        UUID profileId = profileRepository.findIdByExternalIdAndClientId(externalId, clientId);
        if(profileId == null){
            throw new ContentNotFoundException("Profile with external ID: " + externalId + " and client: " + clientId +
                    " was not found");
        }
        return profileId;
    }

    public IdResponseDto findIdResponseDtoByExternalIdAndLmsId(String externalId, Lms lms) {
        UUID id = findIdByExternalIdAndLmsId(externalId, lms);
        return new IdResponseDto(id);
    }

    public Profile findByExternalIdAndLmsId(String externalId, Lms lms) {
        return profileRepository.findByExternalIdAndLmsId(externalId, lms);
    }

    public Profile save(Profile profile) {
        return profileRepository.save(profile);
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
     * Finds the list of profiles that exist in the profile table
     *
     * @param externalProfileIds The list of external profile Ids to find
     * @param lms                the profile lms
     * @return The Id list of the found profiles
     */
    public List<Profile> findProfilesByExternalIdAndLms(List<String> externalProfileIds, Lms lms) {
        return profileRepository.findProfilesByExternalIdAndLms(externalProfileIds, lms);
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

    public Profile saveProfileBasedOnExternalUser(ExternalUserDto externalUser, Lms lms, UUID clientId) {
        Profile profile = new Profile();

        profile.setExternalId(externalUser.getExternalId());
        profile.setClientId(clientId);
        profile.setLmsId(lms);

        JsonObject jsonObject = removeExternalIdFromExternalUserDto(externalUser);
        profile.setProfileData(jsonObject.toString());

        return save(profile);
    }

    private JsonObject removeExternalIdFromExternalUserDto(ExternalUserDto externalUser) {
        JsonElement jsonElement = gson.toJsonTree(externalUser);
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.remove("externalId");
        return jsonObject;
    }

}
