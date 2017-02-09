package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.content.AccessTokenResponseDto;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


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
        doReturn("http://nile-dev.gooru.org").when(configurationService).getContentApiUrl();

        HttpHeaders headers = new HttpHeaders();
        doReturn(headers).when(gooruHelper).setupHttpHeaders(any(String.class));

        AccessTokenResponseDto response = new AccessTokenResponseDto();

        doReturn(new ResponseEntity<>(response, HttpStatus.OK)).when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AccessTokenResponseDto.class));

        authenticationRestClient.verifyUserToken("ANY_TOKEN");

        verify(configurationService, times(1)).getContentApiUrl();

        verify(gooruHelper, times(1)).setupHttpHeaders(any(String.class));

        verify(restTemplate, times(1)).exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AccessTokenResponseDto.class));
    }

}