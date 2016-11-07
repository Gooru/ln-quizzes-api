package com.quizzes.api.common.repository.jooq;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.response.AttemptDTO;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class ContextProfileEventRepositoryImpl {

    @Autowired
    private DSLContext jooq;

    List<ContextProfileEvent> findAttemptsByContextProfileIdAndResourceId(UUID contextProfileId, UUID resourceId) {
        Map<String, Object> answerJson = new HashMap<>();
        answerJson.put("value", "2");
        answerJson.put("value", "1,3");
        answerJson.put("value", "3");

        Map<String, String> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpend", UUID.randomUUID().toString());
        eventData.put("reaction", UUID.randomUUID().toString());
        eventData.put("answer", new Gson().toJson(answerJson));

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        new Gson().toJson(answerJson), null);

        List<ContextProfileEvent> result = new ArrayList<>();
        result.add(contextProfileEvent);
        return result;
    }
}
