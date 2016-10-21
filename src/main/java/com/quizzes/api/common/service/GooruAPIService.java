package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.api.AccessDTO;
import com.quizzes.api.common.dto.api.TokenDTO;
import com.quizzes.api.common.exception.ExceptionMessageTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GooruAPIService {
    public static final String BASE_URL = "http://www.gooru.org/api/nucleus-auth/v1";
    public static String TOKEN = null;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void getAccessToken() {
        try {
            String uri = generateGooruURL("/token");
            AccessDTO accessDTO = getAccessKey();

            RestTemplate restTemplate = new RestTemplate();
            TokenDTO result = restTemplate.postForObject(uri, accessDTO, TokenDTO.class);

            TOKEN = result.getAccess_token();
        } catch (Exception e) {
            logger.error("Error: It was not possible to connect to Gooru API");
            throw new IllegalArgumentException(ExceptionMessageTemplate.ERROR_CONNECTING_API);
        }
    }

    String generateGooruURL(String path) {
        if ('/' != path.charAt(0)) {
            logger.error("Error: There was a problem trying to connect to the API," +
                    " the path is missing the character '/' in " + BASE_URL + " _here_ " + path);
            throw new IllegalArgumentException(ExceptionMessageTemplate.ERROR_CONNECTING_API);
        }
        return BASE_URL + path;
    }

    AccessDTO getAccessKey() {
        String client_key = "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==",
                client_id = "ba956a97-ae15-11e5-a302-f8a963065976",
                grant_type = "anonymous";

        return new AccessDTO(client_key, client_id, grant_type);
    }
}
