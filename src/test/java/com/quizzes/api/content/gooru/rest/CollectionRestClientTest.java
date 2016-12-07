package com.quizzes.api.content.gooru.rest;

import com.quizzes.api.content.gooru.dto.AssessmentDto;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CollectionRestClient.class})
public class CollectionRestClientTest {

    @InjectMocks
    private CollectionRestClient collectionRestClient = PowerMockito.spy(new CollectionRestClient());

    @Mock
    RestTemplate restTemplate;

    @Mock
    AuthenticationRestClient authenticationRestClient;

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Test
    public void getAssessment() throws Exception {
        String anonymousToken = UUID.randomUUID().toString();
        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setId(UUID.randomUUID().toString());

        doReturn(anonymousToken).when(authenticationRestClient).generateAnonymousToken();

        doReturn(new ResponseEntity<>(assessmentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentDto.class));

        collectionRestClient.getAssessment(assessmentDto.getId());

        verify(restTemplate, times(1))
                .exchange(eq(contentApiUrl + "/api/nucleus/v1/assessments/" + assessmentDto.getId()),
                        eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentDto.class));
    }

    @Test
    public void copyAssessment() throws Exception {
        String anonymousToken = UUID.randomUUID().toString();
        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setId(UUID.randomUUID().toString());

        doReturn(new URI(UUID.randomUUID().toString())).when(restTemplate)
                .postForLocation(any(String.class), any(HttpEntity.class), any(UUID.class));

        collectionRestClient.copyAssessment(assessmentDto.getId(), anonymousToken);

        verify(restTemplate, times(1))
                .postForLocation(eq(contentApiUrl + "/api/nucleus/v1/copier/assessments/{assessmentId}"),
                        any(HttpEntity.class), any(UUID.class));
    }
}
