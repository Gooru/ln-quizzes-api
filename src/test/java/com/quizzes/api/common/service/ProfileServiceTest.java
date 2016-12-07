package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import com.quizzes.api.common.service.content.CollectionContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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
    public void findIdByExternalIdAndLmsId() throws Exception {
        UUID id = UUID.randomUUID();
        Lms lms = Lms.its_learning;
        when(profileRepository
                .findIdByExternalIdAndLmsId("external-id", Lms.its_learning))
                .thenReturn(id);

        IdResponseDto result = profileService.findIdByExternalIdAndLmsId("external-id", lms);
        verify(profileRepository, times(1)).findIdByExternalIdAndLmsId(eq("external-id"), eq(lms));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", id, result.getId());
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
        Profile mockProfile = new Profile(null, "external-id", Lms.its_learning, "{\"firstName\":\"name\"}", null,
                null);
        Profile profile = mockProfile;
        UUID id = UUID.randomUUID();
        profile.setId(id);
        when(profileRepository.save(mockProfile)).thenReturn(profile);

        Profile result = profileService.save(profile);

        verify(profileRepository, times(1)).save(eq(mockProfile));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", id, result.getId());
        assertEquals("Wrong profile data", profile.getProfileData(), result.getProfileData());
        assertEquals("Wrong lms id", profile.getLmsId(), result.getLmsId());
        assertEquals("Wrong external id", profile.getExternalId(), result.getExternalId());
    }
}