package com.quizzes.api.common.interceptor;

import com.quizzes.api.common.model.entities.SessionProfileEntity;
import com.quizzes.api.common.service.SessionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class SessionInterceptorTest {

    @InjectMocks
    private SessionInterceptor sessionInterceptor;

    @Mock
    private SessionService sessionService;

    private UUID sessionId;
    private MockHttpServletRequest request;


    @Before
    public void beforeEachTest() {
        sessionId = UUID.randomUUID();
        request = new MockHttpServletRequest();
    }

    @Test
    public void preHandle() throws Exception {
        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        verify(sessionService, times(0)).findSessionProfileEntityBySessionId(any());
        verify(sessionService, times(0)).isSessionAlive(any(), any(), any());

        assertTrue("Result is false", result);
    }

    @Test
    public void preHandleThrowException() throws Exception {
        request.addHeader("session-token", sessionId);

        SessionProfileEntity sessionProfileEntity = Mockito.spy(SessionProfileEntity.class);
        when(sessionProfileEntity.getSessionId()).thenReturn(sessionId);
        when(sessionProfileEntity.getLastAccessAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(sessionProfileEntity.getCurrentTimestamp()).thenReturn(new Timestamp(System.currentTimeMillis()));

        when(sessionService.findSessionProfileEntityBySessionId(sessionId)).thenReturn(sessionProfileEntity);
        when(sessionService.isSessionAlive(any(), any(), any())).thenReturn(true);

        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        verify(sessionService, times(1)).findSessionProfileEntityBySessionId(sessionId);
        verify(sessionService, times(1)).isSessionAlive(any(), any(), any());

        assertTrue("Result is false", result);
    }

}