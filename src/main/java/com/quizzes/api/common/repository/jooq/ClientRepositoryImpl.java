package com.quizzes.api.common.repository.jooq;

import com.quizzes.api.common.model.jooq.tables.pojos.Client;
import com.quizzes.api.common.repository.ClientRepository;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.UUID;

import static com.quizzes.api.common.model.jooq.tables.Client.CLIENT;

@Repository
public class ClientRepositoryImpl implements ClientRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public Client findByApiKeyAndApiSecret(String apiKey, String apiSecret) {
        return jooq.select()
                .from(CLIENT)
                .where(CLIENT.API_KEY.eq(UUID.fromString(apiKey)))
                .and(DSL.condition("CLIENT.API_SECRET = DECODE(MD5(?), 'HEX')", apiSecret))
                .fetchOneInto(Client.class);
    }

}

