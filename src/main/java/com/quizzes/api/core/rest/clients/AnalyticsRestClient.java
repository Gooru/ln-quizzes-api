package com.quizzes.api.core.rest.clients;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
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

    @Autowired
    private Gson gson;

    private static final Logger XMISSION_ERROR_LOGGER = LoggerFactory.getLogger("xmission.errors");

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
            ListenableFuture<ResponseEntity<Void>> future =
                asyncRestTemplate.postForEntity(endpointUrl, new HttpEntity<>(eventBody, headers), Void.class);
            future.addCallback(result -> {
                log.debug("Successfully sent event to analytics.");
            }, ex -> {
                log.warn("Sending event to analytics failed", ex);
                XMISSION_ERROR_LOGGER.warn(gson.toJson(eventBody));
            });
        } catch (Exception e) {
            log.error("Event " + event.getEventName() + " could not be sent to Analytics API.", e);
        }
    }

}
