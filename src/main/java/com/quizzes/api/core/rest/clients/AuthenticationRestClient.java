package com.quizzes.api.content.rest.clients;

import com.google.gson.Gson;
import com.quizzes.api.core.exceptions.ContentProviderException;
import com.quizzes.api.core.exceptions.InternalServerException;
import com.quizzes.api.content.dtos.TokenRequestDto;
import com.quizzes.api.content.dtos.TokenResponseDto;
import com.quizzes.api.content.dtos.UserDataTokenDto;
import com.quizzes.api.content.dtos.UserTokenRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class AuthenticationRestClient {
    private static final String CLIENT_KEY = "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==";
    private static final String CLIENT_ID = "ba956a97-ae15-11e5-a302-f8a963065976";
    private static final String RETURN_URL = "http://www.gooru.org";
    private static final String USER_GRANT_TYPE = "google";
    private static final String ANONYMOUS_GRANT_TYPE = "anonymous";
    private static final String AUTH_API_URL = "/api/nucleus-auth/v1/";
    private static final String ANONYMOUS_AUTH_API_URL = AUTH_API_URL.concat("token");
    private static final String USER_AUTH_API_URL = AUTH_API_URL.concat("authorize");

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    public String generateUserToken(UserDataTokenDto user) {
        String endpointUrl = getContentApiUrl() + USER_AUTH_API_URL;

        UserTokenRequestDto tokenUserRequest = new UserTokenRequestDto();
        tokenUserRequest.setClientKey(CLIENT_KEY);
        tokenUserRequest.setGrantType(USER_GRANT_TYPE);
        tokenUserRequest.setClientId(CLIENT_ID);
        tokenUserRequest.setReturnUrl(RETURN_URL);
        tokenUserRequest.setUser(user);

        if (logger.isDebugEnabled()) {
            logger.debug("POST Request to: " + endpointUrl);
            logger.debug("Body: " + gsonPretty.toJson(tokenUserRequest));
        }

        try {
            TokenResponseDto tokenResponse =
                    restTemplate.postForObject(endpointUrl, tokenUserRequest, TokenResponseDto.class);

            if (logger.isDebugEnabled()) {
                logger.debug("Response from: " + endpointUrl);
                logger.debug("Body: " + gsonPretty.toJson(tokenResponse));
            }

            return tokenResponse.getToken();
        } catch (RestClientException rce) {
            logger.error("User token could not be generated.", rce);
            throw new ContentProviderException("User token could not be generated.", rce);
        } catch (Exception e) {
            logger.error("User token could not be generated.", e);
            throw new InternalServerException("User token could not be generated.", e);
        }
    }

    public String generateAnonymousToken() {
        String endpointUrl = getContentApiUrl() + ANONYMOUS_AUTH_API_URL;
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

    public String getContentApiUrl() {
        return contentApiUrl;
    }
}
