package com.quizzes.api.core.services;

import com.quizzes.api.core.exceptions.InvalidCredentialsException;
import com.quizzes.api.core.model.jooq.tables.pojos.Client;
import com.quizzes.api.core.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
