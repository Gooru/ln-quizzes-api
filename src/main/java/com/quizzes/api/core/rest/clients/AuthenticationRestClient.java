package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.content.AccessTokenResponseDto;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.dtos.content.TokenRequestDto;
import com.quizzes.api.core.dtos.content.TokenResponseDto;
import com.quizzes.api.core.exceptions.InvalidSessionException;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationRestClient {
    private static final String ANONYMOUS_GRANT_TYPE = "anonymous";
    private static final String AUTH_TOKEN_URL = "api/nucleus-auth/v2/token";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private GooruHelper gooruHelper;

    public void verifyAccessToken(String token) {
        String endpointUrl = configurationService.getContentApiUrl() + AUTH_TOKEN_URL;

        if (logger.isDebugEnabled()) {
            logger.debug("GET Request to: " + endpointUrl);
            logger.debug("Header: Authorization Token " + token);
        }

        try {
            HttpHeaders headers = gooruHelper.setupHttpHeaders(token);
            HttpEntity entity = new HttpEntity(headers);

            ResponseEntity<String> responseEntity =
                    restTemplate.exchange(endpointUrl, HttpMethod.GET, entity, String.class);
            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gsonPretty.toJson(responseEntity));
            }
        } catch (RestClientException rce) {
            logger.error("Gooru Token '" + token + "' is not valid.", rce);
            throw new ContentProviderException("Gooru Token " + token + " is not valid.", rce);
        } catch (Exception e) {
            logger.error("Gooru Token validation '" + token + "' could not be processed.", e);
            throw new InternalServerException("Gooru Token validation " + token + " could not be processed.", e);
        }
    }

    public String generateAnonymousToken() {
        String endpointUrl = configurationService.getContentApiUrl() + ANONYMOUS_AUTH_API_URL;
        TokenRequestDto tokenRequest = new TokenRequestDto();
        tokenRequest.setClientId(CLIENT_ID);
        tokenRequest.setClientKey(CLIENT_KEY);
        tokenRequest.setGrantType(ANONYMOUS_GRANT_TYPE);

        if (logger.isDebugEnabled()) {
            logger.debug("POST Request to: " + endpointUrl);
            logger.debug("Body: " + gsonPretty.toJson(tokenRequest));
        }

        try {
            TokenResponseDto tokenResponse =
                    restTemplate.postForObject(endpointUrl, tokenRequest, TokenResponseDto.class);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gsonPretty.toJson(tokenResponse));
            }

            return tokenResponse.getToken();
        } catch (RestClientException rce) {
            logger.error("Anonymous token could not be generated.", rce);
            throw new ContentProviderException("Anonymous token could not be generated.", rce);
        } catch (Exception e) {
            logger.error("Anonymous token could not be generated.", e);
            throw new InternalServerException("Anonymous token could not be generated.", e);
        }
    }
}
