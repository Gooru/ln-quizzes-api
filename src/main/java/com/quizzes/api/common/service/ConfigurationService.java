package com.quizzes.api.common.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService {

    @Value("${session.time.minutes}")
    private double sessionMinutes;

    public double getSessionMinutes(){
        return sessionMinutes;
    }

}
