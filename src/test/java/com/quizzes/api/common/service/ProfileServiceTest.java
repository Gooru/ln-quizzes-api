package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ProfileDto;
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
    public void findProfileDataById() throws Exception {
        //Setting return values from db
        UUID id = UUID.randomUUID();
        Profile profile = new Profile();
        profile.setId(id);
        profile.setProfileData(
                "{\"firstName\": \"David\"," +
                "\"lastName\": \"Artavia\"," +
                "\"username\": \"dartavia\"," +
                "\"email\": \"david@quizzes.com\"}");


        when(profileRepository.findById(id)).thenReturn(profile);

        ProfileDto result = profileService.findById(id);

        verify(profileRepository, times(1)).findById(eq(id));
        assertNotNull("Result is null", result);
        assertEquals("Wrong Id", id.toString(), result.getId());
        assertEquals("Wrong first name", "David", result.getFirstName());
        assertEquals("Wrong last name", "Artavia", result.getLastName());
        assertEquals("Wrong username", "dartavia", result.getUsername());
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
    public void findIdByExternalIdAndClientId() throws Exception {
        UUID clientId = UUID.randomUUID();
        UUID result = profileService.findIdByExternalIdAndClientId("external-id", clientId);
        verify(profileRepository, times(1)).findIdByExternalIdAndClientId(eq("external-id"), eq(clientId));
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