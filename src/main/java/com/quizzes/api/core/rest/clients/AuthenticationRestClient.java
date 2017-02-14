package com.quizzes.api.core.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.core.dtos.content.TokenRequestDto;
import com.quizzes.api.core.dtos.content.TokenResponseDto;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.content.helpers.GooruHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationRestClient {

    private static final String AUTH_API_URL = "/api/nucleus-auth/v2";
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

    public void verifyAccessToken(String token) {
        String endpointUrl = configurationService.getContentApiUrl() + AUTH_API_URL + "/token";

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
        } catch (RestClientException rce) {
            logger.error("Session Token '" + token + "' is not valid in Gooru.", rce);
            throw new ContentProviderException("Session Token " + token + " is not valid in Gooru.", rce);
        } catch (Exception e) {
            logger.error("Session Token validation '" + token + "' could not be processed.", e);
            throw new InternalServerException("Session Token validation " + token + " could not be processed.", e);
        }
    }

    public String generateAnonymousToken() {
        String endpointUrl = configurationService.getContentApiUrl() + AUTH_API_URL + "/signin";
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
