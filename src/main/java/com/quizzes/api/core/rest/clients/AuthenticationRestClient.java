package com.quizzes.api.core.rest.clients;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.TokenRequestDto;
import com.quizzes.api.core.dtos.content.TokenResponseDto;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;

@Component
public class AuthenticationRestClient {

    private static final String ANONYMOUS_GRANT_TYPE = "anonymous";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GooruHelper gooruHelper;

    @Autowired
    private Gson gson;

    public boolean verifyAccessToken(String token) {
        String endpointUrl = configurationService.getTokenVerificationUrl();
        Objects.requireNonNull(endpointUrl);

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(token);
            HttpEntity entity = new HttpEntity(headers);
            restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, Void.class);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Status: OK");
            }
        } catch (RestClientResponseException rce) {
            logger.error("Gooru Session Token " + token + " could not be validated.", rce);
            if (HttpStatus.UNAUTHORIZED.value() == rce.getRawStatusCode()) {
                throw new InvalidSessionException("Invalid Gooru Session Token " + token, rce);
            }
            throw new ContentProviderException("Gooru Session Token " + token + " could not be validated.", rce);
        } catch (Exception e) {
            logger.error("Gooru Session Token " + token + " validation process failed.", e);
            throw new InternalServerException("Gooru Session Token " + token + " validation process failed.", e);
        }
        return true;
    }

    public String generateAnonymousToken() {
        String endpointUrl = configurationService.getSigninUrl();
        Objects.requireNonNull(endpointUrl);

        TokenRequestDto tokenRequest = new TokenRequestDto();
        tokenRequest.setClientId(configurationService.getClientId());
        tokenRequest.setClientKey(configurationService.getClientKey());
        tokenRequest.setGrantType(ANONYMOUS_GRANT_TYPE);

        if (logger.isDebugEnabled()) {
            logger.debug("POST Request to: " + endpointUrl);
            logger.debug("Body: " + gson.toJson(tokenRequest));
        }

        try {
            TokenResponseDto tokenResponse =
                restTemplate.postForObject(endpointUrl, tokenRequest, TokenResponseDto.class);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gson.toJson(tokenResponse));
            }

            return tokenResponse.getToken();
        } catch (RestClientException rce) {
            logger.error("Anonymous Token generation failed in Gooru.", rce);
            throw new ContentProviderException("Anonymous Token generation failed in Gooru.", rce);
        } catch (Exception e) {
            logger.error("Anonymous Token generation could not be processed.", e);
            throw new InternalServerException("Anonymous Token generation could not be processed.", e);
        }
    }

}
