package com.quizzes.api.content.gooru.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quizzes.api.common.exception.ContentProviderException;
import com.quizzes.api.common.exception.InternalServerException;
import com.quizzes.api.content.gooru.dto.TokenRequestDto;
import com.quizzes.api.content.gooru.dto.TokenResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractGooruRestClient {

    private static final String CLIENT_KEY = "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==";
    private static final String CLIENT_ID = "ba956a97-ae15-11e5-a302-f8a963065976";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${content.api.url}")
    private String contentApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private Gson gsonPretty;

    public String generateAnonymousToken() {
        String endpointUrl = getContentApiUrl() + "/api/nucleus-auth/v1/token";
        TokenRequestDto tokenRequest = new TokenRequestDto(CLIENT_KEY, CLIENT_ID, "anonymous");

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
