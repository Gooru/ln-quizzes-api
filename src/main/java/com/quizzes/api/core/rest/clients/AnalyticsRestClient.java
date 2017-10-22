package com.quizzes.api.core.rest.clients;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.AsyncRestTemplate;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.analytics.EventCommon;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AnalyticsRestClient {

    @Autowired
    private AsyncRestTemplate asyncRestTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GooruHelper gooruHelper;

    @Autowired
    private Gson gsonPretty;

    public void notifyEvent(EventCommon event, String token) {
        String baseUrl = configurationService.getAnalyticsEventApiUrl();
        Objects.requireNonNull(baseUrl);

        String endpointUrl = baseUrl + "?apiKey=" + configurationService.getApiKey();
        List<EventCommon> eventBody = Collections.singletonList(event);

        if (log.isDebugEnabled()) {
            log.debug("POST Request to: " + endpointUrl);
            log.debug("Body: " + gsonPretty.toJson(eventBody));
        }

        try {
            HttpHeaders headers = gooruHelper.setupAnalyticsHttpHeaders(token);
            asyncRestTemplate.postForEntity(endpointUrl, new HttpEntity<>(eventBody, headers), Void.class);
        } catch (Exception e) {
            log.error("Event " + event.getEventName() + " could not be sent to Analytics API.", e);
        }
    }

}
