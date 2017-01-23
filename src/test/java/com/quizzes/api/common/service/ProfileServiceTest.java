package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ProfileGetResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ProfileService.class)
public class ProfileServiceTest {

    @InjectMocks
    private ProfileService profileService = Mockito.spy(ProfileService.class);

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private Gson gson = new Gson();

    private UUID profileId;
    private UUID contextId;
    private UUID clientId;
    private String externalId;
    private Lms gooruLms;

    @Before
    public void beforeEachTest() {
        profileId = UUID.randomUUID();
        contextId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        externalId = UUID.randomUUID().toString();
        gooruLms = Lms.gooru;
    }

    @Test
    public void findById() throws Exception {
        when(profileRepository.findById(profileId)).thenReturn(new Profile());

        Profile result = profileService.findById(profileId);
        verify(profileRepository, times(1)).findById(eq(profileId));
    }

    @Test(expected = ContentNotFoundException.class)
    public void findByIdNotFound() throws Exception {
        when(profileRepository.findById(profileId)).thenReturn(null);
        profileService.findById(profileId);
    }

    @Test
    public void findProfileByIdAllFields() throws Exception {
        Profile profile = createProfile();
        when(profileRepository.findById(profileId)).thenReturn(profile);

        ProfileGetResponseDto result = profileService.findProfileResponseDtoById(profileId, null);

        verify(profileRepository, times(1)).findById(eq(profileId));
        assertNotNull("Result is null", result);
        assertEquals("Wrong Id", profileId.toString(), result.getId());
        assertEquals("Wrong first name", "firstName", result.getFirstName());
        assertEquals("Wrong last name", "lastName", result.getLastName());
        assertEquals("Wrong username", "username", result.getUsername());
        assertEquals("Wrong email", "user@email.com", result.getEmail());
        assertEquals("Wrong externalId", externalId, result.getExternalId());
    }

    @Test
    public void findProfileByIdWithEmailAndExternalId() throws Exception {
        Profile profile = createProfile();

        ArrayList<String> fields = new ArrayList<>();
        fields.add("email");
        fields.add("externalId");
        when(profileRepository.findById(profileId)).thenReturn(profile);

        ProfileGetResponseDto result = profileService.findProfileResponseDtoById(profileId, fields);

        verify(profileRepository, times(1)).findById(eq(profileId));
        assertNotNull("Result is null", result);
        assertNull("Id is not null", result.getId());
        assertNull("First name is not null", result.getFirstName());
        assertNull("Last name is not null", result.getLastName());
        assertNull("Username is not null", result.getUsername());
        assertEquals("Wrong email", "user@email.com", result.getEmail());
        assertEquals("Wrong externalId", externalId, result.getExternalId());
    }

    @Test
    public void returnObjectWithFieldsInList() throws Exception {
        ProfileGetResponseDto profile = new ProfileGetResponseDto();
        profile.setId(String.valueOf(profileId));
        profile.setExternalId(externalId);
        profile.setEmail("david@quizzes.com");
        profile.setFirstName("David");
        profile.setLastName("Artavia");
        profile.setUsername("dartavia");

        //Fields To Return
        ArrayList<String> fieldsToReturn = new ArrayList<>();
        fieldsToReturn.add("email");
        fieldsToReturn.add("externalId");

        //Object Fields
        ArrayList<Field> objectFields = new ArrayList();
        objectFields.addAll(Arrays.asList(profile.getClass().getSuperclass().getDeclaredFields()));
        objectFields.addAll(Arrays.asList(profile.getClass().getDeclaredFields()));

        ProfileGetResponseDto result = (ProfileGetResponseDto) WhiteboxImpl.invokeMethod(profileService,
                "returnObjectWithFieldsInList", objectFields, fieldsToReturn, profile);

        assertNotNull("Result is null", result);
        assertNull("Id is not null", result.getId());
        assertNull("First name is not null", result.getFirstName());
        assertNull("Last name is not null", result.getLastName());
        assertNull("Username is not null", result.getUsername());
        assertEquals("Wrong email", "david@quizzes.com", result.getEmail());
        assertEquals("Wrong externalId", externalId, result.getExternalId());
    }

    @Test
    public void findIdResponseDtoByExternalIdAndLmsId() throws Exception {
        when(profileRepository.findIdByExternalIdAndLmsId(externalId, gooruLms)).thenReturn(profileId);

        IdResponseDto result = profileService.findIdResponseDtoByExternalIdAndLmsId(externalId, gooruLms);
        verify(profileRepository, times(1)).findIdByExternalIdAndLmsId(eq(externalId), eq(gooruLms));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", profileId, result.getId());
    }

    @Test
    public void findIdByExternalIdAndLmsId() throws Exception {
        when(profileRepository.findIdByExternalIdAndLmsId(externalId, gooruLms)).thenReturn(profileId);
        UUID result = profileService.findIdByExternalIdAndLmsId(externalId, gooruLms);
        verify(profileRepository, times(1)).findIdByExternalIdAndLmsId(eq(externalId), eq(gooruLms));
        assertEquals("Wrong profileId", profileId, result);
    }

    @Test(expected = ContentNotFoundException.class)
    public void findIdByExternalIdAndLmsIdThrowsException() throws Exception {
        when(profileRepository.findIdByExternalIdAndLmsId(externalId, gooruLms)).thenReturn(null);
        profileService.findIdByExternalIdAndLmsId(externalId, gooruLms);
    }

    @Test
    public void findIdByExternalIdAndClientId() throws Exception {
        UUID result = profileService.findIdByExternalIdAndClientId(externalId, clientId);
        verify(profileRepository, times(1)).findIdByExternalIdAndClientId(eq(externalId), eq(clientId));
    }

    @Test
    public void findAssigneeInContext() throws Exception {
        when(profileRepository.findAssigneeInContext(contextId, profileId)).thenReturn(new Profile());
        Profile result = profileService.findAssigneeInContext(contextId, profileId);
        verify(profileRepository, times(1)).findAssigneeInContext(contextId, profileId);
        assertNotNull("Response is null", result);
        assertEquals("Response is not a profile", Profile.class, result.getClass());
    }

    @Test
    public void save() throws Exception {
        Profile profile = createProfile();

        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile savedProfile = profileService.save(profile);

        verify(profileRepository, times(1)).save(eq(profile));

        assertNotNull("Saved profiles is null", savedProfile);
        assertEquals("Wrong profile id", profile.getId(), savedProfile.getId());
        assertEquals("Wrong profile external id", profile.getExternalId(), savedProfile.getExternalId());
        assertEquals("Wrong profile client id", profile.getClientId(), savedProfile.getClientId());
        assertEquals("Wrong profile data", profile.getProfileData(), savedProfile.getProfileData());
        assertEquals("Wrong profile lms id", profile.getLmsId(), savedProfile.getLmsId());
    }

    @Test
    public void saveProfileBasedOnExternalUser() throws Exception {
        ExternalUserDto userDto = createExternalUserDto();

        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setExternalId(String.valueOf(externalId));
        profile.setProfileData(gson.toJson(createExternalUserDto()));
        profile.setLmsId(gooruLms);

        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile result = profileService.saveProfileBasedOnExternalUser(userDto, gooruLms, clientId);

        verify(profileRepository, times(1)).save(any(Profile.class));

        assertNotNull("Saved profiles is null", result);
        assertEquals("Wrong profile id", profile.getId(), result.getId());
        assertEquals("Wrong profile external id", profile.getExternalId(), result.getExternalId());
        assertEquals("Wrong profile client id", profile.getClientId(), result.getClientId());
        assertEquals("Wrong profile data", profile.getProfileData(), result.getProfileData());
        assertEquals("Wrong profile lms id", profile.getLmsId(), result.getLmsId());
    }

    @Test
    public void removeExternalIdFromExternalUserDto() throws Exception {
        ExternalUserDto userDto = createExternalUserDto();

        JsonObject jsonObject = WhiteboxImpl.invokeMethod(profileService, "removeExternalIdFromExternalUserDto", userDto);

        assertEquals(jsonObject.size(), 4);
        assertEquals("Wrong first name", "firstName", jsonObject.get("firstName").getAsString());
        assertEquals("wrong last name", "lastName", jsonObject.get("lastName").getAsString());
        assertEquals("Wrong username", "username", jsonObject.get("username").getAsString());
        assertEquals("Wrong email", "user@email.com", jsonObject.get("email").getAsString());
        assertNull(jsonObject.get("externalId"));
    }

    private ExternalUserDto createExternalUserDto() {
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(externalId);
        userDto.setFirstName("firstName");
        userDto.setLastName("lastName");
        userDto.setUsername("username");
        userDto.setEmail("user@email.com");
        return userDto;
    }

    private Profile createProfile() {
        Profile profile = new Profile();
        profile.setId(profileId);
        profile.setExternalId(externalId);
        profile.setClientId(clientId);
        profile.setLmsId(gooruLms);
        profile.setProfileData(gson.toJson(createExternalUserDto()));
        return profile;
    }
}