package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.services.content.ConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AssessmentRestClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private Gson gsonPretty;

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    public AssessmentContentDto getAssessment(String assessmentId, String token) {
        String endpointUrl = configurationService.getAssessmentByIdPath(assessmentId);

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = configurationService.setHttpHeaders(token);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<AssessmentContentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, AssessmentContentDto.class);
            AssessmentContentDto assessment = responseEntity.getBody();

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gsonPretty.toJson(assessment));
            }

            return assessment;
        } catch (HttpClientErrorException hcee) {
            logger.error("Gooru Assessment '" + assessmentId + "' could not be retrieved.", hcee);
            if(hcee.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new ContentNotFoundException("Assessment " + assessmentId + " could not be found.");
            }
            throw new ContentProviderException("Assessment " + assessmentId + " could not be retrieved.", hcee);
        } catch (Exception e) {
            logger.error("Gooru Assessment '" + assessmentId + "' could not be processed.", e);
            throw new InternalServerException("Assessment " + assessmentId + " could not be processed.", e);
        }
    }
}

