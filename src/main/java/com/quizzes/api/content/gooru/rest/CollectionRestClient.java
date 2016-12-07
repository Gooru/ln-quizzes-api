package com.quizzes.api.content.gooru.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.common.exception.ContentProviderException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

@Component
public class CollectionRestClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String API_URL = "/api/nucleus/v1/";
    private static final String ASSESSMENTS_PATH = API_URL.concat("assessments/");
    private static final String ASSESSMENTS_COPIER_PATH = API_URL.concat("copier/assessments/{assessmentId}");

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    public AssessmentDto getAssessment(String assessmentId) {
        String endpointUrl = getContentApiUrl() + ASSESSMENTS_PATH + assessmentId;
        String token = authenticationRestClient.generateAnonymousToken();

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = getHttpHeaders(token);
            HttpEntity entity = new HttpEntity(headers);


            ResponseEntity<AssessmentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, AssessmentDto.class);
            AssessmentDto assessment = responseEntity.getBody();

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gsonPretty.toJson(assessment));
            }

            return assessment;
        } catch (RestClientException rce) {
            logger.error("Gooru Assessment '" + assessmentId + "' could not be retrieved.", rce);
            throw new ContentProviderException("Assessment " + assessmentId + " could not be retrieved.", rce);
        } catch (Exception e) {
            logger.error("Gooru Assessment '" + assessmentId + "' could not be processed.", e);
            throw new InternalServerException("Assessment " + assessmentId + " could not be processed.", e);
        }
    }

    public String copyAssessment(String assessmentId, String token) {
        String endpointUrl = getContentApiUrl() + ASSESSMENTS_COPIER_PATH;

        if (logger.isDebugEnabled()) {
            logger.debug("POST Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = getHttpHeaders(token);
            HttpEntity<JsonObject> entity = new HttpEntity<>(new JsonObject(), headers);

            URI location = restTemplate.postForLocation(endpointUrl, entity, assessmentId);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Copy Assessment Location: " + location);
            }

            return location.toString();
        } catch (RestClientException rce) {
            logger.error("Gooru Assessment '" + assessmentId + "' could not be copied.", rce);
            throw new ContentProviderException("Assessment " + assessmentId + " could not be copied.", rce);
        } catch (Exception e) {
            logger.error("Gooru Assessment copy '" + assessmentId + "' could not be processed.", e);
            throw new InternalServerException("Assessment copy " + assessmentId + " could not be processed.", e);
        }
    }

    private HttpHeaders getHttpHeaders(String token){
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + token);

        if (logger.isDebugEnabled()) {
            logger.debug("Headers: " + gsonPretty.toJson(headers));
        }

        return headers;
    }

    public String getContentApiUrl() {
        return contentApiUrl;
    }
}
