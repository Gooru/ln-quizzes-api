package com.quizzes.api.core.rest.clients;

import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AssessmentRestClient.class})
public class AssessmentRestClientTest {

    @InjectMocks
    private AssessmentRestClient assessmentRestClient = spy(new AssessmentRestClient());

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private GooruHelper gooruHelper;

    private String userToken;
    private UUID assessmentId;

    @Before
    public void before() throws Exception {
        userToken = "user-token";
        assessmentId = UUID.randomUUID();
    }

    @Test
    public void getAssessment() throws Exception {
        AssessmentContentDto assessmentDto = new AssessmentContentDto();
        assessmentDto.setId(assessmentId.toString());

        doReturn("http://nile.gooru.org").when(configurationService).getContentApiUrl();
        doReturn(new HttpHeaders()).when(gooruHelper).setupHttpHeaders(userToken);
        doReturn(new ResponseEntity<>(assessmentDto, HttpStatus.OK)).when(restTemplate)
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));

        AssessmentContentDto result = assessmentRestClient.getAssessment(assessmentId, userToken);

        verify(restTemplate, times(1))
                .exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(AssessmentContentDto.class));
        verify(configurationService, times(1)).getContentApiUrl();
        verify(gooruHelper, times(1)).setupHttpHeaders(userToken);
        assertFalse("Collection is true", result.getIsCollection());
    }

}