package com.quizzes.api.core.services.content;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ConfigurationService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String API_URL = "/api/nucleus/v1/";
    private static final String ASSESSMENTS_PATH = API_URL.concat("assessments/");
    private static final String ASSESSMENTS_COPIER_PATH = API_URL.concat("copier/assessments/{assessmentId}");

    @Autowired
    private Gson gsonPretty;

    @Value("${session.time.minutes}")
    private double sessionMinutes;

    @Value("${content.api.url}")
    private String contentApiUrl;

    public double getSessionMinutes() {
        return sessionMinutes;
    }

    public String getContentApiUrl() {
        return contentApiUrl;
    }

    public String getAssessmentByIdPath(String assessmentId) {
        return contentApiUrl + ASSESSMENTS_PATH + assessmentId;
    }

    public HttpHeaders setHttpHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + token);

        if (logger.isDebugEnabled()) {
            logger.debug("Headers: " + gsonPretty.toJson(headers));
        }

        return headers;
    }
}
