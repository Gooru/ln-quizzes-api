package com.quizzes.api.core.services;

import com.quizzes.api.core.services.content.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    @Autowired
    ConfigurationService configurationService;

}
