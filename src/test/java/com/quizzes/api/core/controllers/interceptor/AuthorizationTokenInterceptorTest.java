package com.quizzes.api.core.controllers.interceptor;

import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.services.TokenService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
public class AuthorizationTokenInterceptorTest {

    @InjectMocks
    private AuthorizationTokenInterceptor sessionInterceptor;

    @Mock
    private TokenService tokenService;

    private String authorization;
    private String token;
    private String tokenAnonymous;
    private String authorizationAnonymous;
    private MockHttpServletRequest request;


    @Before
    public void beforeEachTest() {
        tokenAnonymous = "MjoxNDg3MDAxNzc5MDg5OmFub255bW91czo6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2";
        token = "MjoxNDg2NzY5NTgwNzcxOjk4NDJkNDQ5LWYyNDQtNDhmZC1iYWU2LThhNWM5MTNjMjM1ZDo6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2";
        authorizationAnonymous = "Token " + tokenAnonymous;
        authorization = "Token " + token;
        authorizationAnonymous = "Token " + tokenAnonymous;
        request = new MockHttpServletRequest();
    }

    @Test
    public void preHandleAuthorizationForAnonymous() throws Exception {
        preHandleAuthorization(authorizationAnonymous);
    }

    @Test
    public void preHandleAuthorization() throws Exception {
        preHandleAuthorization(authorization);
    }

    private void preHandleAuthorization(String token) {
        request.addHeader("Authorization", token);
        when(tokenService.validate(any(String.class))).thenReturn(true);

        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        verify(tokenService, times(1)).validate(any(String.class));
        assertTrue("Result is false", result);
    }

    @Test(expected = InvalidRequestException.class)
    public void preHandleAuthorizationInvalidSession() throws Exception {
        request.addHeader("Authorization", token);
        sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    }

    @Test
    public void getToken() throws Exception {
        String result = WhiteboxImpl.invokeMethod(sessionInterceptor, "getToken", authorization);
        assertEquals("Wrong session value", token, result);
    }

    @Test(expected = InvalidRequestException.class)
    public void getTokenExceptionWhenNull() throws Exception {
        WhiteboxImpl.invokeMethod(sessionInterceptor, "getToken", "");
    }

    @Test(expected = InvalidRequestException.class)
    public void getTokenExceptionWhenWrongData() throws Exception {
        WhiteboxImpl.invokeMethod(sessionInterceptor, "getToken", UUID.randomUUID().toString());
    }

}