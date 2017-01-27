package com.quizzes.api.core.services;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class ConfigurationService {

    @Value("${session.time.minutes}")
    private double sessionMinutes;

    @Value("${content.api.url}")
    private String contentApiUrl;

    public double getSessionMinutes() {
        return sessionMinutes;
    }

    public String getContentApiUrl() {
        return contentApiUrl;
    }

}
