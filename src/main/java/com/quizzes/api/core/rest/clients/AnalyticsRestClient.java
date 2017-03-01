package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.EventDto;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.services.ConfigurationService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

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
    private Gson gsonPretty;

    public void play(EventDto event) {
        String endpointUrl = configurationService.getContentApiUrl() +
                EVENTS_PATH + "?apiKey=" + configurationService.getApiKey();

        if (log.isDebugEnabled()) {
            log.debug("POST Request to: " + endpointUrl);
        }

        try {
            log.debug("Body: " + gsonPretty.toJson(Collections.singletonList(event)));
            restTemplate.postForObject(endpointUrl, Collections.singletonList(event), Void.class);

        } catch (HttpClientErrorException hcee) {
            log.error("There was problem saving the event: " + event.getEventName(), hcee);
            throw new ContentProviderException("There was problem saving the event: " + event.getEventName(), hcee);
        } catch (Exception e) {
            log.error("There was problem saving the event: " + event.getEventName(), e);
            throw new InternalServerException("There was problem saving the event: " + event.getEventName(), e);
        }
    }
}
