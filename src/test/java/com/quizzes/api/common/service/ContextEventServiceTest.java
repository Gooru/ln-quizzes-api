package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostRequestResourceDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.utils.JsonUtil;
import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.boot.json.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ContextEventService.class, Gson.class})
public class ContextEventServiceTest {

    @InjectMocks
    private ContextEventService contextEventService = Mockito.spy(ContextEventService.class);

    @Mock
    ContextProfileService contextProfileService;

    @Mock
    JsonParser jsonParser;

    @Mock
    ContextProfileEventService contextProfileEventService;

    @Mock
    ContextService contextService;

    @Mock
    ProfileService profileService;

    @Mock
    ContextRepository contextRepository;

    @Mock
    ResourceService resourceService;

    @Mock
    JsonUtil jsonUtil;

    @Mock
    Gson gson;

    @Test
    public void startContextEvent() throws Exception {
        UUID collectionId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);

        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        UUID contextProfileId = UUID.randomUUID();
        UUID currentResourceId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(currentResourceId);

        Map<String, String> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpend", "1478623337");
        eventData.put("reaction", UUID.randomUUID().toString());
        eventData.put("answer", "[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        new Gson().toJson(eventData), null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        Map<String, Object> map = new HashMap<>();
        map.put("answer", "test");
        List<Object> listMock = new ArrayList<>();
        listMock.add("[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        when(jsonParser.parseMap(any(String.class))).thenReturn(map);
        when(jsonParser.parseList(any(String.class))).thenReturn(listMock);

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);
        when(resourceService.findFirstBySequenceByContextId(any(UUID.class))).thenReturn(resource);
        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(0)).findFirstBySequenceByContextId(any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findByContextProfileId(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Id is Null", result.getId());
        assertNotNull("Resource id is Null", result.getCurrentResourceId());
        assertNotNull("Collection id is Null", result.getCollection().getId());
        assertNull("Events doc is not empty", result.getEvents());
        assertEquals("Wrong size", 1, result.getEventsResponse().size());
        assertNotNull("Answer list is Null", result.getEventsResponse().get(0));
    }

    @Test
    public void startContextEventWhenContextProfileNull() throws Exception {
        UUID collectionId = UUID.randomUUID();

        UUID contextId = UUID.randomUUID();
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);

        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        UUID contextProfileId = UUID.randomUUID();
        UUID currentResourceId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(currentResourceId);
        List<ContextProfileEvent> list = new ArrayList<>();

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(null);
        when(resourceService.findFirstBySequenceByContextId(any(UUID.class))).thenReturn(resource);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(contextProfile);

        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(1)).findFirstBySequenceByContextId(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findByContextProfileId(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Id is Null", result.getId());
        assertNotNull("Resource id is Null", result.getCurrentResourceId());
        assertNotNull("Collection id is Null", result.getCollection().getId());
        assertNull("Events doc is not null", result.getEvents());
        assertEquals("Wrong size response events", 0, result.getEventsResponse().size());
        assertEquals("Answer list wrong size", 0, result.getEventsResponse().size());
    }

    @Test
    public void startContextEventWhenContextProfileNotNull() throws Exception {
        UUID collectionId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);

        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        UUID contextProfileId = UUID.randomUUID();
        UUID currentResourceId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(currentResourceId);

        Map<String, String> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpend", "1478623337");
        eventData.put("reaction", UUID.randomUUID().toString());
        eventData.put("answer", "[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        new Gson().toJson(eventData), null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        Map<String, Object> map = new HashMap<>();
        map.put("answer", "test");
        List<Object> listMock = new ArrayList<>();
        listMock.add("[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        when(jsonParser.parseMap(any(String.class))).thenReturn(map);
        when(jsonParser.parseList(any(String.class))).thenReturn(listMock);

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(0)).findFirstBySequenceByContextId(any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findByContextProfileId(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Id is Null", result.getId());
        assertNotNull("Resource id is Null", result.getCurrentResourceId());
        assertNotNull("Collection id is Null", result.getCollection().getId());
        assertNull("Events doc is not null", result.getEvents());
        assertEquals("Wrong size", 1, result.getEventsResponse().size());
        assertNotNull("Answer list is Null", result.getEventsResponse().get(0));
    }

    @Test
    public void convertContextProfileToMap() throws Exception {
        //Setting ContextProfileEvent list
        long timeSpent = 123;
        int reaction = 3;
        int score = 0;
        UUID resourceId = UUID.randomUUID();
        String eventData = "{\n" +
                "      \"score\": "+ score + ",\n" +
                "      \"answer\": [\n" +
                "        {\n" +
                "          \"value\": \"A\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"reaction\": 3,\n" +
                "      \"c\": " + timeSpent + ",\n" +
                "      \"resourceId\": \"" + resourceId + "\"\n" +
                "    }";

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), eventData, null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        //Setting return value
        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("answer", "[{\"value\":\"A\"]");
        mapResponse.put("reaction", reaction);
        mapResponse.put("resourceId", resourceId);
        mapResponse.put("timeSpent", timeSpent);
        mapResponse.put("score", score);

        when(jsonParser.parseMap(any(String.class))).thenReturn(mapResponse);

        List<Map<String, Object>> contextProfilesMap =
                WhiteboxImpl.invokeMethod(contextEventService, "convertContextProfileToMap", list);

        verify(jsonParser, times(1)).parseMap(any(String.class));

        Map<String, Object> result = contextProfilesMap.get(0);
        assertEquals("Wrong number of context profiles", 1, contextProfilesMap.size());
        assertEquals("Wrong resourceId", resourceId, result.get("resourceId"));
        assertEquals("Wrong reaction", reaction, result.get("reaction"));
        assertEquals("Wrong timeSpent", timeSpent, result.get("timeSpent"));
        assertEquals("Wrong score", score, result.get("score"));
        assertEquals("Wrong answer", "[{\"value\":\"A\"]", result.get("answer"));
    }

    @Test
    public void onResourceEvent() throws Exception {
        Resource resource = new Resource();
        ContextProfile contextProfile = new ContextProfile();
        OnResourceEventPostRequestDto body = new OnResourceEventPostRequestDto();
        UUID resourceId = UUID.randomUUID();
        ;
        PostRequestResourceDto resourceDto = new PostRequestResourceDto();
        resourceDto.setResourceId(resourceId);
        body.setPreviousResource(resourceDto);

        ContextProfileEvent event = new ContextProfileEvent();

        when(jsonUtil.removePropertyFromObject(any(Object.class), any(String.class))).thenReturn(new JsonObject());

        Map<String, Object> mapAnswers = new HashMap<>();
        mapAnswers.put("correctAnswer", "[{\"value\":\"test\"}]");

        JSONObject object = new JSONObject();
        object.put("correctAnswer", "[{\"value\":\"test\"}]");

        JsonElement jsonAnswers = new Gson().toJsonTree(mapAnswers.get("correctAnswer"));
        JsonArray correctAnswers = new JsonArray();
        correctAnswers.add(jsonAnswers);

        when(gson.toJsonTree(any(Map.class))).thenReturn(correctAnswers);

        when(contextProfileService.
                findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);
        when(resourceService.findById(any(UUID.class))).thenReturn(resource);
        when(contextProfileEventService.
                findByContextProfileIdAndResourceId(any(UUID.class), any(UUID.class))).thenReturn(event);

        contextEventService.onResourceEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), body);

        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(2)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(jsonParser, times(1)).parseMap(any(String.class));
        verify(contextProfileEventService, times(1)).findByContextProfileIdAndResourceId(any(UUID.class), any(UUID.class));
        verify(contextProfileEventService, times(1)).save(any(ContextProfileEvent.class));
    }

    @Test
    public void finishContextEvent() throws Exception {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(false);

        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        contextEventService.finishContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
    }

    @Test
    public void finishContextEventDoNothing() throws Exception {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(true);

        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        contextEventService.finishContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
    }

}