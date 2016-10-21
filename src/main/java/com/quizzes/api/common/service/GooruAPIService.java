package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.api.AccessDTO;
import com.quizzes.api.common.dto.api.TokenDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GooruAPIService {
    public static final String BASE_URL = "http://www.gooru.org/api/nucleus-auth/v1";
    public static String TOKEN = null;

    public void getAccessToken() {
        final String uri = BASE_URL + "/token";

        String client_key = "c2hlZWJhbkBnb29ydWxlYXJuaW5nLm9yZw==",
                client_id = "ba956a97-ae15-11e5-a302-f8a963065976",
                grant_type = "anonymous";

        AccessDTO accessDTO = new AccessDTO(client_key, client_id, grant_type);

        RestTemplate restTemplate = new RestTemplate();
        TokenDTO result = restTemplate.postForObject(uri, accessDTO, TokenDTO.class);

        TOKEN = result.getAccess_token();
    }
}
