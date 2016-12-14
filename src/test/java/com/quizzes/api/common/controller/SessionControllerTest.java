package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionController.class)
public class SessionControllerTest {

    @InjectMocks
    private SessionController sessionController = Mockito.spy(SessionController.class);

    @Test
    public void getAuthorization() throws Exception {
        ResponseEntity<SessionTokenDto> result = sessionController.getAuthorization(new SessionPostRequestDto());

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());
        assertNotNull("Id is null", result.getBody().getSessionToken());
    }

}