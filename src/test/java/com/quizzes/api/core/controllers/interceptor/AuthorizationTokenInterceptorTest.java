package com.quizzes.api.core.controllers.interceptor;

import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
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
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
public class AuthorizationTokenInterceptorTest {

    @InjectMocks
    private AuthorizationTokenInterceptor sessionInterceptor;

    @Mock
    private AuthenticationRestClient authenticationRestClient;

    private String authorization;
    private String token;
    private MockHttpServletRequest request;


    @Before
    public void beforeEachTest() {
        token = "MTQ4NTUzMzM2MDA1Nzphbm9ueW1vdXM6YmE5NTZhOTctYWUxNS0xMWU1LWEzMDItZjhhOTYzMDY1OTc2";
        authorization = "Token " + token;
        request = new MockHttpServletRequest();
    }

    @Test
    public void preHandle() throws Exception {
        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());
        assertTrue("Result is false", result);
    }

    @Test
    public void preHandleAuthorization() throws Exception {
        request.addHeader("Authorization", authorization);

        AccessTokenResponseDto accessTokenResponseDto = new AccessTokenResponseDto();
        accessTokenResponseDto.setClientId(UUID.randomUUID().toString());
        accessTokenResponseDto.setUserId(UUID.randomUUID().toString());

        when(authenticationRestClient.verifyAccessToken(any(String.class))).thenReturn(accessTokenResponseDto);

        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());

        verify(authenticationRestClient, times(1)).verifyAccessToken(any(String.class));

        assertTrue("Result is false", result);
    }

    @Test(expected = InvalidSessionException.class)
    public void preHandleAuthorizationInvalidSession() throws Exception {
        request.addHeader("Authorization", token);

        boolean result = sessionInterceptor.preHandle(request, new MockHttpServletResponse(), new Object());
    }

    @Test
    public void getToken() throws Exception {
        String result =
                WhiteboxImpl.invokeMethod(sessionInterceptor, "getToken", authorization);

        assertEquals("Wrong session value", token, result);
    }

    @Test(expected = InvalidSessionException.class)
    public void getTokenExceptionWhenNull() throws Exception {
        String result =
                WhiteboxImpl.invokeMethod(sessionInterceptor, "getToken", "");
    }

    @Test(expected = InvalidSessionException.class)
    public void getTokenExceptionWhenWrongData() throws Exception {
        String result =
                WhiteboxImpl.invokeMethod(sessionInterceptor, "getToken", UUID.randomUUID().toString());
    }

}