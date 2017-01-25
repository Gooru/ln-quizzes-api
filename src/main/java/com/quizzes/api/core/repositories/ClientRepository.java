package com.quizzes.api.core.repositories;

import com.quizzes.api.core.model.jooq.tables.pojos.Client;

public interface ClientRepository {

    Client findByApiKeyAndApiSecret(String apiKey, String apiSecret);

}
