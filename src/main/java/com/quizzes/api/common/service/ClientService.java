package com.quizzes.api.common.service;

import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InvalidCredentialsException;
import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.model.jooq.tables.pojos.Session;
import com.quizzes.api.common.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public Client findByApiKeyAndApiSecret(String apiKey, String apiSecret) {
        Client client = clientRepository.findByApiKeyAndApiSecret(apiKey, apiSecret);
        if (client == null) {
            throw new InvalidCredentialsException("Invalid client credentials.");
        }
        return client;
    }
}
