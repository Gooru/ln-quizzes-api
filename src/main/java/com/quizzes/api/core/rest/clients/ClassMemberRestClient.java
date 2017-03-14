package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.exceptions.InvalidClassMemberException;
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
public class ClassMemberRestClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String CLASS_MEMBERS_URL = "/api/nucleus/v1/classes/%s/members";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    @Autowired
    private GooruHelper gooruHelper;

    @Autowired
    private ConfigurationService configurationService;

    public ClassMemberContentDto getClassMembers(UUID classId, String token) {
        String endpointUrl = configurationService.getContentApiUrl() + String.format(CLASS_MEMBERS_URL, classId);

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(token);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<ClassMemberContentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, ClassMemberContentDto.class);
            ClassMemberContentDto classMemberContentDto = responseEntity.getBody();

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gsonPretty.toJson(classMemberContentDto));
            }

            return classMemberContentDto;
        } catch (HttpClientErrorException hcee) {
            logger.error("Gooru class member for class ID: '" + classId + "' could not be retrieved.", hcee);
            if (hcee.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new ContentNotFoundException("Class member for class ID:" + classId + " could not be found.");
            }
            if (hcee.getStatusCode().equals(HttpStatus.FORBIDDEN)) {
                throw new InvalidClassMemberException("User with token " + token +
                        " does not have permissions to access Class ID " + classId + " members.");
            }
            throw new ContentProviderException("Class member for class ID: " + classId + " could not be retrieved.",
                    hcee);
        } catch (Exception e) {
            logger.error("Gooru class member '" + classId + "' could not be processed.", e);
            throw new InternalServerException("Class member for class ID: " + classId + " could not be processed.", e);
        }
    }

}

