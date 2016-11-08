package com.quizzes.api.content.gooru.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quizzes.api.common.exception.ContentProviderException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.content.gooru.dto.AssessmentDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public AssessmentDto getAssessment(String assessmentId) {
        String endpointUrl = getContentApiUrl() + "/api/nucleus/v1/assessments/" + assessmentId;
        String token = generateAnonymousToken();

        try {
            RestTemplate restTemplate = RestTemplateBuilder.buildRestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.set("Authorization", "Token " + token);
            HttpEntity entity = new HttpEntity(headers);

            if (logger.isDebugEnabled()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                logger.debug("GET Request to: " + endpointUrl);
                logger.debug("Headers: " + gson.toJson(headers));
            }

            ResponseEntity<AssessmentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, AssessmentDto.class);
            AssessmentDto assessment = responseEntity.getBody();

            if (logger.isDebugEnabled()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gson.toJson(assessment));
            }

            return assessment;
        } catch (RestClientException rce) {
            logger.error("Anonymous token could not be generated.", rce);
            throw new ContentProviderException("Anonymous token could not be generated.", rce);
        } catch (Exception e) {
            logger.error("Anonymous token could not be generated.", e);
            throw new InternalServerException("Anonymous token could not be generated.", e);
        }
    }

}
