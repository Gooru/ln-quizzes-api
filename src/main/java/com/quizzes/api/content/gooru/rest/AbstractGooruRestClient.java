package com.quizzes.api.content.gooru.rest;

import com.quizzes.api.content.gooru.dto.TokenRequestDTO;
import com.quizzes.api.content.gooru.dto.TokenResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public abstract class AbstractGooruRestClient {

    private static final String CLIENT_KEY = "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==";
    private static final String CLIENT_ID = "ba956a97-ae15-11e5-a302-f8a963065976";

    @Value("${content.api.url}")
    private String contentApiUrl;

    public String generateAnonymousToken() {
        String endpointUrl = getContentApiUrl() + "/api/nucleus-auth/v1/token";
        TokenRequestDTO tokenRequest = new TokenRequestDTO(CLIENT_KEY, CLIENT_ID, "anonymous");

        RestTemplate restTemplate = new RestTemplate();
        TokenResponseDTO tokenResponse =
                restTemplate.postForObject(endpointUrl, tokenRequest, TokenResponseDTO.class);



        return tokenResponse.getToken();
    }

    public String getContentApiUrl() {
        return contentApiUrl;
    }

}
