package com.quizzes.api.common.service;

import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    public Client findByApiKeyAndApiSecret(String apiKey, String apiSecret) {
        return clientRepository.findByApiKeyAndApiSecret(apiKey, apiSecret);
    }
}
