package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.EventContentDto;
import com.quizzes.api.core.services.ConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AnalyticsRestClient.class})
public class AnalyticsRestClientTest {

    @InjectMocks
    private AnalyticsRestClient analyticsRestClient = spy(new AnalyticsRestClient());

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private Gson gsonPretty = new Gson();

    private String url;
    private String apiKey;

    @Before
    public void before() throws Exception {
        url = "http://www.gooru.org";
        apiKey = UUID.randomUUID().toString();
    }

    @Test
    public void play() throws Exception {
        EventContentDto eventContentDto = EventContentDto.builder().build();

        doReturn(new ResponseEntity<>(eventContentDto, HttpStatus.OK)).when(restTemplate)
                .postForObject(any(String.class), any(Collections.class), eq(Void.class));
        doReturn(url).when(configurationService).getContentApiUrl();
        doReturn(apiKey).when(configurationService).getApiKey();

        analyticsRestClient.play(eventContentDto);

        verify(restTemplate, times(1))
                .postForObject(any(String.class), any(Collections.class), eq(Void.class));
        verify(configurationService, times(1)).getContentApiUrl();
        verify(configurationService, times(1)).getApiKey();
    }
}
