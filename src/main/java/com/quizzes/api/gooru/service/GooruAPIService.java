package com.quizzes.api.gooru.service;

import com.quizzes.api.common.dto.api.AccessDTO;
import com.quizzes.api.common.dto.api.TokenDTO;
import com.quizzes.api.common.exception.ExceptionMessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

public class GooruAPIService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private String token;

    @Value("${gooru.api.base-url}")
    private String baseURL;

    @Value("${gooru.api.client-key}")
    private String clientKey;

    @Value("${gooru.api.client-id}")
    private String clientId;

    @Value("${gooru.api.grant-type}")
    private String grantType;

    public String getAccessToken() {
        if (token == null) {
            try {
                return generateToken();
            } catch (Exception e) {
                logger.error("Error: It was not possible to connect to Gooru API. Error: " + e);
                throw new IllegalArgumentException(ExceptionMessageTemplate.ERROR_CONNECTING_API);
            }
        } else {
            //TODO: We need to verify if token is valid
            return token;
        }
    }

    public String generateToken() {
        String uri = generateGooruURL("/token");
        AccessDTO accessDTO = getAccessKey();

        RestTemplate restTemplate = new RestTemplate();
        TokenDTO result = restTemplate.postForObject(uri, accessDTO, TokenDTO.class);

        return result.getToken();
    }

    public String generateGooruURL(String path) {
        return baseURL + (path.startsWith("/") ? "" : "/") + path;
    }

    public AccessDTO getAccessKey() {
        return new AccessDTO(clientKey, clientId, grantType);
    }
}
