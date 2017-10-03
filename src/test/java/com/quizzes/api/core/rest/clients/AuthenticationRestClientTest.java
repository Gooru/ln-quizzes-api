package com.quizzes.api.core.rest.clients;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticationRestClientTest {

    @InjectMocks
    private AuthenticationRestClient authenticationRestClient = Mockito.spy(AuthenticationRestClient.class);

    @Mock
    RestTemplate restTemplate;

    @Mock
    ConfigurationService configurationService;

    @Mock
    GooruHelper gooruHelper;

    @Test
    public void verifyUserToken() throws Exception {
        doReturn("http://nile-dev.gooru.org/api/nucleus-token-server/v1/token").when(configurationService)
            .getTokenVerificationUrl();

        HttpHeaders headers = new HttpHeaders();
        doReturn(headers).when(gooruHelper).setupHttpHeaders(any(String.class));

        doReturn(new ResponseEntity<>(Void.class, HttpStatus.OK)).when(restTemplate)
            .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(Void.class));

        boolean result = authenticationRestClient.verifyAccessToken("ANY_TOKEN");

        verify(configurationService, times(1)).getTokenVerificationUrl();
        verify(gooruHelper, times(1)).setupHttpHeaders(any(String.class));
        verify(restTemplate, times(1))
            .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(Void.class));

        assertTrue("Wrong return value", result);
    }

}
