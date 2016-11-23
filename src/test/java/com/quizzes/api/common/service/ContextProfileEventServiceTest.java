package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextProfileEventRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextProfileEventServiceTest {

    @InjectMocks
    private ContextProfileEventService contextProfileEventService = Mockito.spy(ContextProfileEventService.class);

    @Mock
    ContextProfileEventRepository contextProfileEventRepository;

    @Test
    public void findEventsByContextProfileId() throws Exception {
        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpent", System.currentTimeMillis());
        eventData.put("reaction", 5);
        eventData.put("answer", "[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        new Gson().toJson(eventData), null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        when(contextProfileEventRepository.findEventsByContextProfileId(any(UUID.class))).thenReturn(list);

        List<ContextProfileEvent> result = contextProfileEventService.findEventsByContextProfileId(UUID.randomUUID());

        verify(contextProfileEventRepository, times(1)).findEventsByContextProfileId(any(UUID.class));
        assertNotNull("Response is Null", result);
    }

}