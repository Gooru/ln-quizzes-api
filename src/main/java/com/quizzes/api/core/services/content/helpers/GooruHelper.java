package com.quizzes.api.core.services.content.helpers;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class GooruHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private Gson gsonPretty;

    public HttpHeaders setHttpHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.set("Authorization", "Token " + token);

        if (logger.isDebugEnabled()) {
            logger.debug("Headers: " + gsonPretty.toJson(headers));
        }

        return headers;
    }

}
