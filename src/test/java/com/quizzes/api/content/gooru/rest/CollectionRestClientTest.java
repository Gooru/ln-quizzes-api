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
@PrepareForTest(CollectionRestClient.class)
public class CollectionRestClientTest {

    @InjectMocks
    private CollectionRestClient collectionRestClient = PowerMockito.spy(new CollectionRestClient());

    @Mock
    RestTemplate restTemplate;

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Test
    public void getAssessment() throws Exception {
        String anonymousToken = UUID.randomUUID().toString();
        AssessmentDto assessmentDto = new AssessmentDto();
        assessmentDto.setId(UUID.randomUUID().toString());

        doReturn(anonymousToken).when(collectionRestClient).generateAnonymousToken();

        doReturn(new ResponseEntity<>(assessmentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentDto.class));

        collectionRestClient.getAssessment(assessmentDto.getId());

        verify(restTemplate, times(1))
                .exchange(eq(contentApiUrl + "/api/nucleus/v1/assessments/" + assessmentDto.getId()),
                        eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentDto.class));
    }

}
