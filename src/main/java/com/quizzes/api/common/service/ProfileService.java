package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ProfileGetResponseDto;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Autowired
    Gson gson;

    public Profile findById(UUID profileId) throws ClassNotFoundException {
        return profileRepository.findById(profileId);
    }

    public ProfileGetResponseDto findProfileResponseDtoById(UUID profileId) throws ClassNotFoundException {
        Profile profile = findById(profileId);
        ArrayList<String> list = new ArrayList<>();
        list.add("externalId");
        list.add("lastName");

        ProfileGetResponseDto result = null;
        if(profile != null) {
            result = gson.fromJson(profile.getProfileData(), ProfileGetResponseDto.class);
            result.setId(profileId.toString());
            result.setExternalId(profile.getExternalId());
        }
        ProfileGetResponseDto x = findByIdFields(result, list);
        return x;
    }

    public ProfileGetResponseDto findByIdFields(ProfileGetResponseDto profile, ArrayList<String> filter) throws ClassNotFoundException {
        ArrayList<Field> fields = new ArrayList();
        fields.addAll(Arrays.asList(profile.getClass().getSuperclass().getDeclaredFields()));
        fields.addAll(Arrays.asList(profile.getClass().getDeclaredFields()));

        ProfileGetResponseDto response = new ProfileGetResponseDto();

        filter.forEach(item -> {
            try {
                Field field = response.getClass().getDeclaredField(item);
                field.set(response, );
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        });

//        fields.stream().filter(property -> !filter.contains(property.getName())).forEach(property->{
//            property.setAccessible(true);
//            try {
//                property.set(profile, null);
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//        });

        return profile;
    }

    public UUID findIdByExternalIdAndLmsId(String externalId, Lms lms) {
        return profileRepository.findIdByExternalIdAndLmsId(externalId, lms);
    }

    public IdResponseDto findIdResponseDtoByExternalIdAndLmsId(String externalId, Lms lms) {
        UUID id = findIdByExternalIdAndLmsId(externalId, lms);
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
