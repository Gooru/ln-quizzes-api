package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AssessmentRestClient.class})
public class AssessmentRestClientTest {

    @InjectMocks
    private AssessmentRestClient assessmentRestClient = PowerMockito.spy(new AssessmentRestClient());

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private AuthenticationRestClient authenticationRestClient;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private GooruHelper gooruHelper;

    private String userToken;

    @Before
    public void before() throws Exception {
        userToken = "user-token";
    }


    @Test
    public void getAssessment() throws Exception {
        AssessmentContentDto assessmentDto = new AssessmentContentDto();
        assessmentDto.setId(UUID.randomUUID().toString());

        String url = "http://www.gooru.org";
        doReturn(url).when(configurationService).getContentApiUrl();
        doReturn(new HttpHeaders()).when(gooruHelper).setHttpHeaders(userToken);

        doReturn(new ResponseEntity<>(assessmentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));

        assessmentRestClient.getAssessment(assessmentDto.getId(), userToken);

        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));
        verify(configurationService, times(1)).getContentApiUrl();
        verify(gooruHelper, times(1)).setHttpHeaders(userToken);
    }

}
