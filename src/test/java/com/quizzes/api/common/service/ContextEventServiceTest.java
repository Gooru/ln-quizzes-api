package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.model.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import org.jooq.tools.json.JSONArray;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextEventService.class)
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
        when(profileService.findAssigneeInContext(any(UUID.class), any(UUID.class))).thenReturn(new Profile());
        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);
        when(resourceService.findFirstBySequenceByContextId(any(UUID.class))).thenReturn(resource);
        when(contextProfileEventService.findEventsByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(0)).findFirstBySequenceByContextId(any(UUID.class));
        verify(profileService, times(1)).findAssigneeInContext(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findEventsByContextProfileId(any(UUID.class));

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
        when(profileService.findAssigneeInContext(any(UUID.class), any(UUID.class))).thenReturn(new Profile());
        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(null);
        when(resourceService.findFirstBySequenceByContextId(any(UUID.class))).thenReturn(resource);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(contextProfile);

        when(contextProfileEventService.findEventsByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(profileService, times(1)).findAssigneeInContext(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(1)).findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(1)).findFirstBySequenceByContextId(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findEventsByContextProfileId(any(UUID.class));

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

        when(profileService.findAssigneeInContext(any(UUID.class), any(UUID.class))).thenReturn(new Profile());
        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        when(contextProfileEventService.findEventsByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(profileService, times(1)).findAssigneeInContext(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(1)).findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(0)).findFirstBySequenceByContextId(any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findEventsByContextProfileId(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Id is Null", result.getId());
        assertNotNull("Resource id is Null", result.getCurrentResourceId());
        assertNotNull("Collection id is Null", result.getCollection().getId());
        assertNull("Events doc is not null", result.getEvents());
        assertEquals("Wrong size", 1, result.getEventsResponse().size());
        assertNotNull("Answer list is Null", result.getEventsResponse().get(0));
    }

    @Test(expected = ContentNotFoundException.class)
    public void startContextEventContextException() throws Exception {
        when(contextService.findById(any(UUID.class))).thenReturn(null);
        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test(expected = ContentNotFoundException.class)
    public void startContextEventProfileException() throws Exception {
        when(contextService.findById(any(UUID.class))).thenReturn(new Context());
        when(profileService.findAssigneeInContext(any(UUID.class), any(UUID.class))).thenReturn(null);
        StartContextEventResponseDto result = contextEventService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    public void convertContextProfileToMapWithoutAnswers() throws Exception {
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

        List<Map<String, Object>> contextProfilesMap =
                WhiteboxImpl.invokeMethod(contextEventService, "convertContextProfileToMap", list);

        Map<String, Object> result = contextProfilesMap.get(0);
        assertEquals("Wrong number of context profiles", 1, contextProfilesMap.size());
        assertFalse("Response includes id", result.containsKey("id"));
        assertTrue("Response does not resourceId", result.containsKey("resourceId"));
        assertTrue("Response does not answer", result.containsKey("answer"));
        assertEquals("Answer is not an empty array", ArrayList.class, result.get("answer").getClass());
    }

    @Test
    public void convertContextProfileToMap() throws Exception {
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
        map.put("test", "test");

        when(jsonParser.parseMap(any(String.class))).thenReturn(map);

        List<Map<String, Object>> contextProfilesMap =
                WhiteboxImpl.invokeMethod(contextEventService, "convertContextProfileToMap", list);

        Map<String, Object> result = contextProfilesMap.get(0);
        assertEquals("Wrong number of context profiles", 1, contextProfilesMap.size());
        assertFalse("Response includes id", result.containsKey("id"));
        assertTrue("Response does not resourceId", result.containsKey("resourceId"));
        assertTrue("Response does not answer", result.containsKey("answer"));
        assertEquals("Answer is not an empty array", JSONArray.class, result.get("answer").getClass());
    }
    
    @Test
    public void finishContextEvent() throws Exception {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(false);

        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        contextEventService.finishContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileService, times(1)).findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
    }

    @Test
    public void finishContextEventDoNothing() throws Exception {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(true);

        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        contextEventService.finishContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileService, times(1)).findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
    }

    @Test(expected = ContentNotFoundException.class)
    public void finishContextEventException() throws Exception {
        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(null);
        contextEventService.finishContextEvent(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test(expected = ContentNotFoundException.class)
    public void validateProfileInContext() throws Exception {
        UUID profileId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        when(profileService.findAssigneeInContext(any(UUID.class), any(UUID.class))).thenReturn(null);
        WhiteboxImpl.invokeMethod(contextEventService, "validateProfileInContext", profileId, contextId);
    }

    @Test(expected = ContentNotFoundException.class)
    public void validateContextException() throws Exception {
        UUID contextId = UUID.randomUUID();
        when(contextService.findById(any(UUID.class))).thenReturn(null);
        Context result = WhiteboxImpl.invokeMethod(contextEventService, "validateContext", contextId);
    }

    @Test
    public void validateContext() throws Exception {
        UUID contextId = UUID.randomUUID();
        when(contextService.findById(any(UUID.class))).thenReturn(new Context());
        Context result = WhiteboxImpl.invokeMethod(contextEventService, "validateContext", contextId);
        assertNotNull("Response is Null", result);
    }

}