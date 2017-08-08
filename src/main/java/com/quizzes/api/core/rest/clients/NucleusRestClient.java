package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

public class NucleusRestClient {

    private final Logger logger;

    protected static final String NUCLEUS_API_URL = "/api/nucleus/v1";

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected Gson gsonPretty;

    @Autowired
    protected GooruHelper gooruHelper;

    @Autowired
    protected ConfigurationService configurationService;

    public NucleusRestClient(Logger logger) {
        this.logger = logger;
    }

    protected HttpEntity setupHttpHeaders(String authToken) {
        HttpHeaders headers = gooruHelper.setupHttpHeaders(authToken);
        return new HttpEntity(headers);
    }

    protected void logRequest(String endpoint) {
        logger.debug("GET Request to: " + endpoint);
    }

    protected void logResponse(String endpoint, String json) {
        logger.debug("Response from: " + endpoint);
        logger.debug("Body: " + json);
    }
}
