package com.quizzes.api.core.controllers.interceptor;

import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.model.entities.SessionProfileEntity;
import com.quizzes.api.core.services.SessionService;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
public class SessionInterceptorTest {

    @InjectMocks
    private SessionInterceptor sessionInterceptor;

    @Mock
    private SessionService sessionService;

    private UUID sessionId;
    private String authorization;
    private MockHttpServletRequest request;


    @Before
    public void beforeEachTest() {
        sessionId = UUID.randomUUID();
        authorization = "Token " + sessionId;
        request = new MockHttpServletRequest();
    }

    @Ignore
    @Test
    public void preHandle() throws Exception {
        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        //verify(sessionService, times(0)).isSessionAlive(any(), any(), any());

       // assertTrue("Result is false", result);
    }


    @Ignore
    @Test
    public void preHandleAuthorization() throws Exception {
        request.addHeader("Authorization", authorization);

        SessionProfileEntity sessionProfileEntity = Mockito.spy(SessionProfileEntity.class);
        when(sessionProfileEntity.getSessionId()).thenReturn(sessionId);
        when(sessionProfileEntity.getLastAccessAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(sessionProfileEntity.getCurrentTimestamp()).thenReturn(new Timestamp(System.currentTimeMillis()));

        //when(sessionService.isSessionAlive(any(), any(), any())).thenReturn(true);

        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        //verify(sessionService, times(1)).isSessionAlive(any(), any(), any());

        assertTrue("Result is false", result);
    }

    @Ignore
    @Test(expected = InvalidSessionException.class)
    public void preHandleAuthorizationInvalidSession() throws Exception {
        request.addHeader("Authorization", authorization);

        SessionProfileEntity sessionProfileEntity = Mockito.spy(SessionProfileEntity.class);
        when(sessionProfileEntity.getSessionId()).thenReturn(sessionId);
        when(sessionProfileEntity.getLastAccessAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(sessionProfileEntity.getCurrentTimestamp()).thenReturn(new Timestamp(System.currentTimeMillis()));

        //when(sessionService.isSessionAlive(any(), any(), any())).thenReturn(false);

        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    }

    @Test
    public void validateTokenFormat() throws Exception {
        String result =
                WhiteboxImpl.invokeMethod(sessionInterceptor, "validateTokenFormat", authorization);

        assertEquals("Wrong session value", sessionId.toString(), result);
    }

    @Test(expected = InvalidSessionException.class)
    public void validateTokenFormatExceptionWhenNull() throws Exception {
        String result =
                WhiteboxImpl.invokeMethod(sessionInterceptor, "validateTokenFormat", "");
    }

    @Test(expected = InvalidSessionException.class)
    public void validateTokenFormatExceptionWhenWrongData() throws Exception {
        String result =
                WhiteboxImpl.invokeMethod(sessionInterceptor, "validateTokenFormat", sessionId.toString());
    }

}