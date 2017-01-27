package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
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

import java.util.Arrays;

@Component
public class AssessmentRestClient {

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

    public AssessmentContentDto getAssessment(String assessmentId, String token) {
        String endpointUrl = getContentApiUrl() + ASSESSMENTS_PATH + assessmentId;

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = getHttpHeaders(token);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<AssessmentContentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, AssessmentContentDto.class);
            AssessmentContentDto assessment = responseEntity.getBody();

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

    private HttpHeaders getHttpHeaders(String token) {
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

