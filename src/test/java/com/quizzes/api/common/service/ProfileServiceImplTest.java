package com.quizzes.api.common.service;

import com.quizzes.api.common.model.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)

public class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl profileService = Mockito.spy(ProfileServiceImpl.class);

    @Mock
    private ProfileRepository profileRepository;

    @Test
    public void findById() throws Exception {
        Profile mockProfile = new Profile("externalId");
        doReturn(mockProfile).when(profileService).findById(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));

        Profile result = profileService.findById(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        verify(profileService, times(1)).findById(Mockito.eq(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db")));
        assertNotNull(result);
        assertNotNull(result.getExternalId(), "externalId");
    }

}