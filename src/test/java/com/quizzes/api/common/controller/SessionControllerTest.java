package com.quizzes.api.common.controller;

import com.quizzes.api.common.dto.ExternalUserDto;
import com.quizzes.api.common.dto.SessionPostRequestDto;
import com.quizzes.api.common.dto.SessionTokenDto;
import com.quizzes.api.common.service.SessionService;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SessionController.class)
public class SessionControllerTest {

    @InjectMocks
    private SessionController sessionController = Mockito.spy(SessionController.class);

    @Mock
    private SessionService sessionService;

    @Test
    public void authorize() throws Exception {
        //Setting sessionDto
        SessionPostRequestDto session = new SessionPostRequestDto();
        session.setClientApiKey(UUID.randomUUID().toString());
        session.setClientApiSecret(UUID.randomUUID().toString());
        session.setUser(new ExternalUserDto());

        //Setting sessionTokenDto
        UUID sessionToken = UUID.randomUUID();
        SessionTokenDto token = new SessionTokenDto();
        token.setSessionToken(sessionToken);

        when(sessionService.generateToken(eq(session))).thenReturn(token);

        ResponseEntity<SessionTokenDto> result = sessionController.authorize(session);

        verify(sessionService, times(1)).generateToken(eq(session));

        assertNotNull("Result is null", result);
        assertEquals("Http status is not 200", HttpStatus.OK, result.getStatusCode());
        assertEquals("Wrong session token", token.getSessionToken(), result.getBody().getSessionToken());
    }

}