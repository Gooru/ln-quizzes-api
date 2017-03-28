package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.analytics.EventCommon;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Component
@Slf4j
public class AnalyticsRestClient {

    private static final String NUCLEUS_INSIGHTS_API_URL = "/api/nucleus-insights/v2";
    private static final String EVENTS_PATH = NUCLEUS_INSIGHTS_API_URL.concat("/event");

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GooruHelper gooruHelper;

    @Autowired
    private Gson gsonPretty;

    public void notifyEvent(EventCommon event, String token) {
        String endpointUrl = configurationService.getContentApiUrl() +
                EVENTS_PATH + "?apiKey=" + configurationService.getApiKey();
        List<EventContentCommonDto> eventBody = Collections.singletonList(event);

        if (log.isDebugEnabled()) {
            log.debug("POST Request to: " + endpointUrl);
            log.debug("Body: " + gsonPretty.toJson(eventBody));
        }

        try {
            HttpHeaders headers = gooruHelper.setupAnalyticsHttpHeaders(token);
            restTemplate.postForObject(endpointUrl, new HttpEntity<>(eventBody, headers), Void.class);
        } catch (Exception e) {
            log.error("Event " + event.getEventName() + " could not be sent to Analytics API.", e);
        }
    }

}
