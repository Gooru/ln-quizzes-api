package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.IdResponseDto;
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

}