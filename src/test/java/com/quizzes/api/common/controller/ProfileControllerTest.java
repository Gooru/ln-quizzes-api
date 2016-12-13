package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ProfileGetResponseDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.service.ProfileService;
import com.quizzes.api.common.service.content.CollectionContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CollectionContentService.class)
public class ProfileControllerTest {

    @InjectMocks
    private ProfileController profileController = Mockito.spy(ProfileController.class);

    @Mock
    ProfileService profileService;

    @Test
    public void getProfileIdByExternalId() throws Exception {
        UUID uuid = UUID.randomUUID();
        IdResponseDto id = new IdResponseDto();
        id.setId(uuid);
        when(profileService.findIdByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(id);

        ResponseEntity<IdResponseDto> result = profileController.getProfileIdByExternalId("externalId", "its_learning");

        verify(profileController, times(1)).getProfileIdByExternalId(any(String.class), any(String.class));
        verify(profileService, times(1)).findIdByExternalIdAndLmsId(any(String.class), any(Lms.class));

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());
        assertEquals("Wrong id", uuid, result.getBody().getId());
    }

    @Test
    public void getProfileIdByExternalIdNull() throws Exception {
        when(profileService.findIdByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(null);

        ResponseEntity<IdResponseDto> result = profileController.getProfileIdByExternalId("externalId", "its_learning");

        verify(profileController, times(1)).getProfileIdByExternalId(any(String.class), any(String.class));
        verify(profileService, times(1)).findIdByExternalIdAndLmsId(any(String.class), any(Lms.class));

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());
        assertNull("Id is not null", result.getBody());
    }

    @Test
    public void getProfileById() throws Exception {
        UUID id = UUID.randomUUID();
        UUID sessionProfileId = UUID.randomUUID();

        //Setting ProfileDto
        ProfileGetResponseDto profileMock = new ProfileGetResponseDto();
        profileMock.setId(id.toString());
        profileMock.setEmail("david@quizzes.com");
        profileMock.setFirstName("David");
        profileMock.setLastName("Artavia");
        profileMock.setUsername("dartavia");

        when(profileService.findById(id)).thenReturn(profileMock);

        ResponseEntity<ProfileGetResponseDto> result = profileController.getProfileById(id, sessionProfileId, "its_learning");

        verify(profileController, times(1)).getProfileById(id, sessionProfileId, "its_learning");
        verify(profileService, times(1)).findById(id);

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());

        ProfileDto resultBody = result.getBody();
        assertEquals("Wrong id", id.toString(), resultBody.getId());
        assertEquals("Wrong first name", "David", resultBody.getFirstName());
        assertEquals("Wrong last name", "Artavia", resultBody.getLastName());
        assertEquals("Wrong email", "david@quizzes.com", resultBody.getEmail());
        assertEquals("Wrong username", "dartavia", resultBody.getUsername());
    }

    @Test
    public void getProfileDataByIdShouldReturnNull() throws Exception {
        UUID id = UUID.randomUUID();
        UUID sessionProfileId = UUID.randomUUID();

        when(profileService.findById(id)).thenReturn(null);

        ResponseEntity<ProfileGetResponseDto> result = profileController.getProfileById(id, sessionProfileId, "quizzes");

        verify(profileController, times(1)).getProfileById(id, sessionProfileId, "quizzes");
        verify(profileService, times(1)).findById(id);

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());
        assertNull("Id is not null", result.getBody());
    }

}