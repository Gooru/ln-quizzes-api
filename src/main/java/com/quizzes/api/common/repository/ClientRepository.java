package com.quizzes.api.common.repository;

import com.quizzes.api.common.model.jooq.tables.pojos.Client;

public interface ClientRepository {

    Client findByApiKeyAndApiSecret(String apiKey, String apiSecret);

}
