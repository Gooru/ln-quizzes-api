package com.quizzes.api.common.service;

import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl profileService = Mockito.spy(ProfileServiceImpl.class);

    @Mock
    private ProfileRepository profileRepository;

    @Test
    public void findById() throws Exception {
        UUID id = UUID.randomUUID();
        doReturn(null).when(profileService).findById(id);

        Profile result = profileService.findById(id);
        verify(profileService, times(1)).findById(eq(id));
        assertNull("Response is not null", result);
    }

    @Test
    public void findByExternalIdAndLmsId() throws Exception {
        UUID id = UUID.randomUUID();
        Profile profile = new Profile(id, "external-id", Lms.its_learning, "{\"firstName\":\"name\"}", null);
        Lms lms = Lms.its_learning;
        when(profileRepository
                .findByExternalIdAndLmsId("external-id", Lms.its_learning))
                .thenReturn(profile);

        Profile result = profileService.findByExternalIdAndLmsId("external-id", lms);
        verify(profileRepository, times(1)).findByExternalIdAndLmsId(eq("external-id"), eq(lms));
        assertNotNull("Response is null", result);
        assertEquals("Wrong id", profile.getId(), result.getId());
        assertEquals("Wrong profile data", "{\"firstName\":\"name\"}", result.getProfileData());
        assertEquals("Wrong lms id", lms, result.getLmsId());
        assertEquals("Wrong external id", "external-id", result.getExternalId());
    }

    @Test
    public void save() throws Exception {
        Profile mockProfile = new Profile(null, "external-id", Lms.its_learning, "{\"firstName\":\"name\"}", null);

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