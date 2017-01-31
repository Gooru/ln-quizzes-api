package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.core.dtos.content.CollectionContentDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Component
public class CollectionRestClient {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final String API_URL = "/api/nucleus/v1/";
    private static final String COLLECTIONS_PATH = API_URL.concat("collections/");
    private static final String COLLECTIONS_COPIER_PATH = API_URL.concat("copier/collections/{collectionId}");

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    @Autowired
    GooruHelper gooruHelper;

    @Autowired
    ConfigurationService configurationService;

    public CollectionContentDto getCollection(String collectionId, String token) {
        String endpointUrl = configurationService.getContentApiUrl() + COLLECTIONS_PATH + collectionId;

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(token);
            HttpEntity entity = new HttpEntity(headers);
            ResponseEntity<CollectionContentDto> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, CollectionContentDto.class);
            CollectionContentDto collection = responseEntity.getBody();

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gsonPretty.toJson(collection));
            }

            return collection;
        } catch (HttpClientErrorException hcee) {
            logger.error("Gooru Collection '" + collectionId + "' could not be retrieved.", hcee);
            if(hcee.getStatusCode().equals(HttpStatus.NOT_FOUND)){
                throw new ContentNotFoundException("Collection " + collectionId + " could not be found.");
            }
            throw new ContentProviderException("Collection " + collectionId + " could not be retrieved.", hcee);
        } catch (Exception e) {
            logger.error("Gooru Collection '" + collectionId + "' could not be processed.", e);
            throw new InternalServerException("Collection " + collectionId + " could not be processed.", e);
        }
    }

    public String copyAssessment(String collectionId, String token) {
        String endpointUrl = configurationService.getContentApiUrl() + COLLECTIONS_COPIER_PATH;

        if (logger.isDebugEnabled()) {
            logger.debug("POST Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(token);
            HttpEntity<JsonObject> entity = new HttpEntity<>(new JsonObject(), headers);

            URI location = restTemplate.postForLocation(endpointUrl, entity, collectionId);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Copy collection Location: " + location);
            }

            return location.toString();
        } catch (RestClientException rce) {
            logger.error("Gooru Collection '" + collectionId + "' could not be copied.", rce);
            throw new ContentProviderException("Collection " + collectionId + " could not be copied.", rce);
        } catch (Exception e) {
            logger.error("Gooru Collection copy '" + collectionId + "' could not be processed.", e);
            throw new InternalServerException("Collection copy " + collectionId + " could not be processed.", e);
        }
    }

}
