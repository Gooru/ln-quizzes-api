package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.services.ConfigurationService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
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
    RestTemplate restTemplate;

    @Mock
    AuthenticationRestClient authenticationRestClient;

    @Mock
    ConfigurationService configurationService;


    @Test
    public void getAssessment() throws Exception {
        AssessmentContentDto assessmentDto = new AssessmentContentDto();
        assessmentDto.setId(UUID.randomUUID().toString());

        String url = "http://www.gooru.org";
        doReturn(url).when(configurationService).getContentApiUrl();

        doReturn(new ResponseEntity<>(assessmentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));

        assessmentRestClient.getAssessment(assessmentDto.getId(), "user-token");

        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));
        verify(configurationService, times(1)).getContentApiUrl();
    }

}
