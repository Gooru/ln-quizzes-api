package com.quizzes.api.content.gooru.rest;

import com.google.gson.Gson;
import com.quizzes.api.common.exception.ContentProviderException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class CollectionRestClient extends AbstractGooruRestClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    public AssessmentDto getAssessment(String assessmentId) {
        String endpointUrl = getContentApiUrl() + "/api/nucleus/v1/assessments/" + assessmentId;
        String token = generateAnonymousToken();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Token " + token);
            HttpEntity entity = new HttpEntity(headers);

            if (logger.isDebugEnabled()) {
                logger.debug("GET Request to: " + endpointUrl);
                logger.debug("Headers: " + gsonPretty.toJson(headers));
            }

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

}
