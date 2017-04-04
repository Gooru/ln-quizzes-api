package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.analytics.EventCollection;
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
import org.springframework.web.client.AsyncRestTemplate;

import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    private AsyncRestTemplate asyncRestTemplate;

    @Mock
    private ConfigurationService configurationService;

    @Mock
    private GooruHelper gooruHelper;

    @Mock
    private Gson gsonPretty = new Gson();

    private String url;
    private String apiKey;
    private String token;

    @Before
    public void before() throws Exception {
        url = "http://www.gooru.org";
        apiKey = UUID.randomUUID().toString();
        token = UUID.randomUUID().toString();
    }

    @Test
    public void notifyEvent() throws Exception {
        EventCollection eventCollectionContentDto = EventCollection.builder().build();

        doReturn(null).when(asyncRestTemplate).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        doReturn(url).when(configurationService).getContentApiUrl();
        doReturn(apiKey).when(configurationService).getApiKey();
        doReturn(new HttpHeaders()).when(gooruHelper).setupAnalyticsHttpHeaders(token);

        analyticsRestClient.notifyEvent(eventCollectionContentDto, token);

        verify(asyncRestTemplate, times(1)).postForEntity(anyString(), any(HttpEntity.class), eq(Void.class));
        verify(configurationService, times(1)).getContentApiUrl();
        verify(gooruHelper, times(1)).setupAnalyticsHttpHeaders(token);
        verify(configurationService, times(1)).getApiKey();
    }
}
