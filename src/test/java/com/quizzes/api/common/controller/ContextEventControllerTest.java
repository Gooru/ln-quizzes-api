package com.quizzes.api.common.controller;

import com.google.gson.JsonArray;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.service.ContextEventService;
import com.quizzes.api.common.service.ContextProfileService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextEventControllerTest {

    @InjectMocks
    private ContextEventController controller = new ContextEventController();

    @Mock
    private ContextEventService contextEventService;

    @Mock
    private ContextProfileService contextProfileService;

    @Test
    public void startContextEvent() throws Exception {
        UUID id = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        CollectionDto collection = new CollectionDto();
        collection.setId(String.valueOf(collectionId));

        StartContextEventResponseDto startContext = new StartContextEventResponseDto();
        startContext.setId(id);
        startContext.setCurrentResourceId(resourceId);
        startContext.setCollection(collection);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("answer", new JsonArray());
        list.add(map);

        startContext.setEventsResponse(list);

        when(contextEventService.startContextEvent(any(UUID.class), any(UUID.class))).thenReturn(startContext);

        ResponseEntity<StartContextEventResponseDto> result = controller.startContextEvent(UUID.randomUUID(), "quizzes", UUID.randomUUID());

        verify(contextEventService, times(1)).startContextEvent(any(UUID.class), any(UUID.class));

        StartContextEventResponseDto resultBody = result.getBody();
        assertSame(resultBody.getClass(), StartContextEventResponseDto.class);
        assertEquals("Wrong resource id is null", resourceId, resultBody.getCurrentResourceId());
        assertEquals("Wrong id", id, resultBody.getId());
        assertEquals("Wrong collection id", collection.getId(), resultBody.getCollection().getId());
        assertEquals("Wrong collection id", 1, resultBody.getEventsResponse().size());
        assertTrue("Answer key not found", resultBody.getEventsResponse().get(0).containsKey("answer"));
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void finishContextEvent() throws Exception {
        ResponseEntity<?> result = controller.finishContextEvent(UUID.randomUUID(), "its_learning", UUID.randomUUID());
        verify(contextEventService, times(1)).finishContextEvent(any(UUID.class), any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
    }

}