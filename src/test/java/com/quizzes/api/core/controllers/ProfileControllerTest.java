package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.dtos.ProfileGetResponseDto;
import com.quizzes.api.core.model.jooq.enums.Lms;
import com.quizzes.api.core.services.ProfileService;
import com.quizzes.api.core.services.content.CollectionContentService;
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
    public void findIdResponseDtoByExternalIdAndLmsId() throws Exception {
        UUID uuid = UUID.randomUUID();
        IdResponseDto id = new IdResponseDto();
        id.setId(uuid);
        when(profileService.findIdResponseDtoByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(id);

        ResponseEntity<IdResponseDto> result = profileController.getProfileIdByExternalId("externalId", "its_learning");

        verify(profileController, times(1)).getProfileIdByExternalId(any(String.class), any(String.class));
        verify(profileService, times(1)).findIdResponseDtoByExternalIdAndLmsId(any(String.class), any(Lms.class));

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());
        assertEquals("Wrong id", uuid, result.getBody().getId());
    }

    @Test
    public void getProfileIdByExternalIdNull() throws Exception {
        when(profileService.findIdResponseDtoByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(null);

        ResponseEntity<IdResponseDto> result = profileController.getProfileIdByExternalId("externalId", "its_learning");

        verify(profileController, times(1)).getProfileIdByExternalId(any(String.class), any(String.class));
        verify(profileService, times(1)).findIdResponseDtoByExternalIdAndLmsId(any(String.class), any(Lms.class));

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
        profileMock.setExternalId("123");

        when(profileService.findProfileResponseDtoById(id, null)).thenReturn(profileMock);

        ResponseEntity<ProfileGetResponseDto> result =
                profileController.getProfileById(id, null, sessionProfileId, "its_learning");

        verify(profileController, times(1)).getProfileById(id, null, sessionProfileId, "its_learning");
        verify(profileService, times(1)).findProfileResponseDtoById(id, null);

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());

        ProfileGetResponseDto resultBody = result.getBody();
        assertEquals("Wrong id", id.toString(), resultBody.getId());
        assertEquals("Wrong first name", "David", resultBody.getFirstName());
        assertEquals("Wrong last name", "Artavia", resultBody.getLastName());
        assertEquals("Wrong email", "david@quizzes.com", resultBody.getEmail());
        assertEquals("Wrong username", "dartavia", resultBody.getUsername());
        assertEquals("Wrong externalId", "123", resultBody.getExternalId());
    }

    @Test
    public void getProfileByIdReturnEmail() throws Exception {
        UUID id = UUID.randomUUID();
        UUID sessionProfileId = UUID.randomUUID();

        //Setting ProfileDto
        ProfileGetResponseDto profileMock = new ProfileGetResponseDto();
        profileMock.setEmail("david@quizzes.com");

        //Fields
        String fields = "email";

        when(profileService.findProfileResponseDtoById(any(UUID.class), any())).thenReturn(profileMock);

        ResponseEntity<ProfileGetResponseDto> result =
                profileController.getProfileById(id, fields, sessionProfileId, "its_learning");

        verify(profileController, times(1)).getProfileById(id, fields, sessionProfileId, "its_learning");
        verify(profileService, times(1)).findProfileResponseDtoById(any(UUID.class), any());

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());

        ProfileGetResponseDto resultBody = result.getBody();
        assertNull("Id is not null", resultBody.getId());
        assertNull("first name is not null", resultBody.getFirstName());
        assertNull("Last name is not null", resultBody.getLastName());
        assertNull("Username is not null", resultBody.getUsername());
        assertNull("External id is not null", resultBody.getExternalId());
        assertEquals("Wrong email", "david@quizzes.com", resultBody.getEmail());
    }

}