package com.quizzes.api.content.gooru.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quizzes.api.common.exception.ContentProviderException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.content.gooru.dto.TokenRequestDto;
import com.quizzes.api.content.gooru.dto.TokenResponseDto;
import com.quizzes.api.content.gooru.dto.TokenUserRequestDto;
import com.quizzes.api.content.gooru.dto.UserDataTokenDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractGooruRestClient {

    private static final String CLIENT_KEY = "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==";
    private static final String CLIENT_ID = "ba956a97-ae15-11e5-a302-f8a963065976";
    private static final String RETURN_URL = "http://www.gooru.org";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    public String generateUserToken(UserDataTokenDto user) {
        String endpointUrl = getContentApiUrl() + "/api/nucleus-auth/v1/authorize";

        TokenUserRequestDto tokenUserRequest = new TokenUserRequestDto();
        tokenUserRequest.setClientKey(CLIENT_KEY);
        tokenUserRequest.setGrantType("google");
        tokenUserRequest.setClientId(CLIENT_ID);
        tokenUserRequest.setReturnUrl(RETURN_URL);
        tokenUserRequest.setUser(user);

        if (logger.isDebugEnabled()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            logger.debug("POST Request to: " + endpointUrl);
            logger.debug("Body: " + gson.toJson(tokenUserRequest));
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
        String endpointUrl = getContentApiUrl() + "/api/nucleus-auth/v1/token";
        TokenRequestDto tokenRequest = new TokenRequestDto();
        tokenRequest.setClientId(CLIENT_ID);
        tokenRequest.setClientKey(CLIENT_KEY);
        tokenRequest.setGrantType("anonymous");

        if (logger.isDebugEnabled()) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            logger.debug("POST Request to: " + endpointUrl);
            logger.debug("Body: " + gson.toJson(tokenRequest));
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
