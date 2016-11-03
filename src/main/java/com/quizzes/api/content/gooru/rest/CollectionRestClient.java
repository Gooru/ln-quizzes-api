package com.quizzes.api.content.gooru.rest;

import com.quizzes.api.content.gooru.dto.AssessmentDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

public class CollectionRestClient extends AbstractGooruRestClient {


    public AssessmentDTO getAssessment(String assessmentId) {
        String endpointUrl = getContentApiUrl() + "/api/nucleus/v1/assessments/" + assessmentId;
        String token = generateAnonymousToken();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + token);

        HttpEntity entity = new HttpEntity(headers);
        ResponseEntity<AssessmentDTO> responseEntity =
                restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, AssessmentDTO.class);

        AssessmentDTO assessment = responseEntity.getBody();

        return assessment;
    }

}
