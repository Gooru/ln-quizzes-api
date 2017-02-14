package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
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
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
public class AssessmentRestClient {

    private static final String NUCLEUS_API_URL = "/api/nucleus/v1";
    private static final String ASSESSMENTS_PATH = NUCLEUS_API_URL.concat("/assessments/%s");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GooruHelper gooruHelper;

    @Autowired
    private Gson gson;

    public AssessmentContentDto getAssessment(UUID assessmentId, String token) {
        String endpointUrl = configurationService.getContentApiUrl() + String.format(ASSESSMENTS_PATH, assessmentId);

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(token);
            HttpEntity requestEntity = new HttpEntity(headers);
            ResponseEntity<AssessmentContentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, requestEntity, AssessmentContentDto.class);
            AssessmentContentDto assessment = responseEntity.getBody();
            assessment.setIsCollection(false);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gson.toJson(assessment));
            }

            return assessment;
        } catch (HttpClientErrorException hcee) {
            if (hcee.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                logger.error("Assessment " + assessmentId + " not found in Gooru.", hcee);
                throw new ContentNotFoundException("Assessment " + assessmentId + " not found in Gooru.");
            } else {
                logger.error("Assessment " + assessmentId + " could not be retrieved.", hcee);
                throw new ContentProviderException("Assessment " + assessmentId + " could not be retrieved.", hcee);
            }
        } catch (Exception e) {
            logger.error("Getting Assessment " + assessmentId + " process failed.", e);
            throw new InternalServerException("Getting Assessment " + assessmentId + " process failed.", e);
        }
    }

}

