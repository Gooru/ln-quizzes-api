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
    Gson gson = new Gson();

    @Test
    public void findById() throws Exception {
        UUID id = UUID.randomUUID();
        when(profileRepository.findById(id)).thenReturn(new Profile());

        Profile result = profileService.findById(id);
        verify(profileRepository, times(1)).findById(eq(id));
    }

    @Test(expected = ContentNotFoundException.class)
    public void findByIdNotFound() throws Exception {
        //Setting return values from db
        UUID id = UUID.randomUUID();
        when(profileRepository.findById(id)).thenReturn(null);
        Profile result = profileService.findById(id);
    }

    @Test
    public void findProfileByIdAllFields() throws Exception {
        //Setting return values from db
        UUID id = UUID.randomUUID();
        UUID externalId = UUID.randomUUID();

        Profile profile = new Profile();
        profile.setId(id);
        profile.setExternalId(externalId.toString());
        profile.setProfileData(
                "{\"firstName\": \"David\"," +
                        "\"lastName\": \"Artavia\"," +
                        "\"username\": \"dartavia\"," +
                        "\"username\": \"dartavia\"," +
                        "\"email\": \"david@quizzes.com\"}");


        when(profileRepository.findById(id)).thenReturn(profile);

        ProfileGetResponseDto result = profileService.findProfileResponseDtoById(id, null);

        verify(profileRepository, times(1)).findById(eq(id));
        assertNotNull("Result is null", result);
        assertEquals("Wrong Id", id.toString(), result.getId());
        assertEquals("Wrong first name", "David", result.getFirstName());
        assertEquals("Wrong last name", "Artavia", result.getLastName());
        assertEquals("Wrong username", "dartavia", result.getUsername());
        assertEquals("Wrong email", "david@quizzes.com", result.getEmail());
        assertEquals("Wrong externalId", externalId.toString(), result.getExternalId());
    }

    @Test
    public void findProfileByIdWithEmailAndExternalId() throws Exception {
        //Setting return values from db
        UUID id = UUID.randomUUID();
        UUID externalId = UUID.randomUUID();

        Profile profile = new Profile();
        profile.setId(id);
        profile.setExternalId(externalId.toString());
        profile.setProfileData(
                "{\"firstName\": \"David\"," +
                        "\"lastName\": \"Artavia\"," +
                        "\"username\": \"dartavia\"," +
                        "\"username\": \"dartavia\"," +
                        "\"email\": \"david@quizzes.com\"}");

        ArrayList<String> fields = new ArrayList<>();
        fields.add("email");
        fields.add("externalId");
        when(profileRepository.findById(id)).thenReturn(profile);

        ProfileGetResponseDto result = profileService.findProfileResponseDtoById(id, fields);

        verify(profileRepository, times(1)).findById(eq(id));
        assertNotNull("Result is null", result);
        assertNull("Id is not null", result.getId());
        assertNull("First name is not null", result.getFirstName());
        assertNull("Last name is not null", result.getLastName());
        assertNull("Username is not null", result.getUsername());
        assertEquals("Wrong email", "david@quizzes.com", result.getEmail());
        assertEquals("Wrong externalId", externalId.toString(), result.getExternalId());
    }

    @Test
    public void returnObjectWithFieldsInList() throws Exception {
        //Setting return values from db
        UUID id = UUID.randomUUID();
        UUID externalId = UUID.randomUUID();

        ProfileGetResponseDto profile = new ProfileGetResponseDto();
        profile.setId(id.toString());
        profile.setExternalId(externalId.toString());
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

        ProfileGetResponseDto result = (ProfileGetResponseDto) WhiteboxImpl.invokeMethod(profileService, "returnObjectWithFieldsInList",
                objectFields, fieldsToReturn, profile);

        assertNotNull("Result is null", result);
        assertNull("Id is not null", result.getId());
        assertNull("First name is not null", result.getFirstName());
        assertNull("Last name is not null", result.getLastName());
        assertNull("Username is not null", result.getUsername());
        assertEquals("Wrong email", "david@quizzes.com", result.getEmail());
        assertEquals("Wrong externalId", externalId.toString(), result.getExternalId());
    }

    @Test
    public void findIdResponseDtoByExternalIdAndLmsId() throws Exception {
        UUID id = UUID.randomUUID();
        Lms lms = Lms.its_learning;
        when(profileRepository
                .findIdByExternalIdAndLmsId("external-id", Lms.its_learning))
                .thenReturn(id);

        IdResponseDto result = profileService.findIdResponseDtoByExternalIdAndLmsId("external-id", lms);
        verify(profileRepository, times(1)).findIdByExternalIdAndLmsId(eq("external-id"), eq(lms));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", id, result.getId());
    }

    @Test
    public void findIdByExternalIdAndLmsId() throws Exception {
        UUID result = profileService.findIdByExternalIdAndLmsId("external-id", Lms.its_learning);
        verify(profileRepository, times(1)).findIdByExternalIdAndLmsId(eq("external-id"), eq(Lms.its_learning));
    }

    @Test
    public void findAssigneeInContext() throws Exception {
        when(profileRepository.findAssigneeInContext(any(UUID.class), any(UUID.class))).thenReturn(new Profile());
        Profile result = profileService.findAssigneeInContext(any(UUID.class), any(UUID.class));
        verify(profileRepository, times(1)).findAssigneeInContext(any(UUID.class), any(UUID.class));
        assertNotNull("Response is null", result);
        assertEquals("Response is not a profile", Profile.class, result.getClass());
    }

    @Test
    public void save() throws Exception {
        Profile profile = new Profile();
        profile.setId(UUID.randomUUID());
        profile.setExternalId("external-id");
        profile.setClientId(UUID.randomUUID());
        profile.setLmsId(Lms.quizzes);
        profile.setProfileData("{\"firstName\":\"name\"}");

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
        UUID clientId = UUID.randomUUID();
        Lms lms = Lms.gooru;

        //Setting ExternalUserDto
        UUID externalId = UUID.randomUUID();
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(externalId.toString());
        userDto.setEmail("david@quizzes.com");
        userDto.setFirstName("David");
        userDto.setLastName("Artavia");
        userDto.setUsername("dartavia");

        Profile profile = new Profile();
        UUID id = UUID.randomUUID();
        profile.setId(id);
        profile.setExternalId(externalId.toString());
        profile.setProfileData(
                "{\"firstName\": \"David\"," +
                        "\"lastName\": \"Artavia\"," +
                        "\"username\": \"dartavia\"," +
                        "\"email\": \"david@quizzes.com\"}");
        profile.setLmsId(lms);

        when(profileRepository.save(any(Profile.class))).thenReturn(profile);

        Profile result = profileService.saveProfileBasedOnExternalUser(userDto, lms, clientId);

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
        ExternalUserDto userDto = new ExternalUserDto();
        userDto.setExternalId(UUID.randomUUID().toString());
        userDto.setFirstName("Keylor");
        userDto.setLastName("Navas");
        userDto.setUsername("knavas");

        JsonObject jsonObject = WhiteboxImpl.invokeMethod(profileService, "removeExternalIdFromExternalUserDto", userDto);

        assertEquals(jsonObject.size(), 3);
        assertEquals("Wrong first name", "Keylor", jsonObject.get("firstName").getAsString());
        assertEquals("wrong last name", "Navas", jsonObject.get("lastName").getAsString());
        assertEquals("Wrong username", "knavas", jsonObject.get("username").getAsString());
        assertNull(jsonObject.get("externalId"));
    }
}