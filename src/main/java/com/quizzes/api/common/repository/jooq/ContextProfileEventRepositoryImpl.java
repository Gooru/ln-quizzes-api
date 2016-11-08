package com.quizzes.api.common.repository.jooq;

import com.google.gson.Gson;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.repository.ContextProfileEventRepository;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ContextProfileEventRepositoryImpl implements ContextProfileEventRepository {

    @Autowired
    private DSLContext jooq;

    @Override
    public List<ContextProfileEvent> findAttemptsByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId) {

        Map<String, String> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpend", "1478623337");
        eventData.put("reaction", UUID.randomUUID().toString());
        eventData.put("answer", "[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        new Gson().toJson(eventData), null);

        List<ContextProfileEvent> result = new ArrayList<>();
        result.add(contextProfileEvent);
        return result;
    }
}
