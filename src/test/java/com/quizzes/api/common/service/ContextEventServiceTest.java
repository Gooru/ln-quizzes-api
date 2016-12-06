package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostRequestResourceDto;
import com.quizzes.api.common.dto.PostResponseResourceDto;
import com.quizzes.api.common.dto.ProfileEventResponseDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.dto.StudentEventsResponseDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.model.entities.StudentEventEntity;
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

    @Mock
    StudentEventEntity studentEventEntity;

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
    public void convertContextProfileToMapWithoutAnswers() throws Exception {
        Map<String, String> eventData = new HashMap<>();
        eventData.put("resourceId", UUID.randomUUID().toString());
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
        map.put("resourceId", "test");
        List<Object> listMock = new ArrayList<>();
        listMock.add("[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        when(jsonParser.parseMap(any(String.class))).thenReturn(map);
        when(jsonParser.parseList(any(String.class))).thenReturn(listMock);

        List<Map<String, Object>> contextProfilesMap =
                WhiteboxImpl.invokeMethod(contextEventService, "convertContextProfileToMap", list);

        Map<String, Object> result = contextProfilesMap.get(0);
        assertEquals("Wrong number of context profiles", 1, contextProfilesMap.size());
        assertTrue("Response does not resourceId", result.containsKey("resourceId"));
        assertTrue("Response does not answer", result.containsKey("answer"));
        assertEquals("Answer is not an empty array", ArrayList.class, result.get("answer").getClass());
    }

    @Test
    public void convertContextProfileToMap() throws Exception {
        Map<String, String> eventData = new HashMap<>();
        eventData.put("resourceId", UUID.randomUUID().toString());
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
        map.put("resourceId", "test");

        when(jsonParser.parseMap(any(String.class))).thenReturn(map);

        List<Map<String, Object>> contextProfilesMap =
                WhiteboxImpl.invokeMethod(contextEventService, "convertContextProfileToMap", list);

        Map<String, Object> result = contextProfilesMap.get(0);
        assertEquals("Wrong number of context profiles", 1, contextProfilesMap.size());
        assertTrue("Response does not contain resourceId", result.containsKey("resourceId"));
        assertTrue("Response does not contain answer", result.containsKey("answer"));
        assertEquals("Answer is not an empty array", JSONArray.class, result.get("answer").getClass());
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

    @Test
    public void getStudentEvents() throws Exception {
        //Map values for findAllStudentEvents
        Map<UUID, List<StudentEventEntity>> studentEventsMap = new HashMap<>();
        List<StudentEventEntity> events = new ArrayList<>();

        //Setting events
        UUID currentResourceId = UUID.randomUUID();

        //Setting entity values
        StudentEventEntity studentEventEntity = Mockito.spy(StudentEventEntity.class);
        when(studentEventEntity.getCurrentResourceId()).thenReturn(currentResourceId);
        when(studentEventEntity.getEventData()).thenReturn("jsonMock");
        events.add(studentEventEntity);

        //Adding students
        UUID student1 = UUID.randomUUID();
        studentEventsMap.put(student1, events);

        UUID student2 = UUID.randomUUID();
        studentEventsMap.put(student2, events);

        //Setting context
        Context contextMock = new Context();
        UUID contextId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        contextMock.setId(contextId);
        contextMock.setCollectionId(collectionId);

        PostResponseResourceDto postResponseResourceDto = new PostResponseResourceDto();
        postResponseResourceDto.setScore(0);
        postResponseResourceDto.setReaction(3);

        when(contextProfileEventService.findAllStudentEventsByContextId(contextId)).thenReturn(studentEventsMap);
        when(contextService.findById(contextId)).thenReturn(contextMock);
        when(gson.fromJson(any(String.class), any())).thenReturn(postResponseResourceDto);

        StudentEventsResponseDto result = contextEventService.getStudentEvents(contextId);

        verify(contextProfileEventService, times(1)).findAllStudentEventsByContextId(contextId);
        verify(contextService, times(1)).findById(contextId);
        verify(gson, times(2)).fromJson(any(String.class), any());

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 2,result.getProfileEvents().size());

        ProfileEventResponseDto profileResult1 = result.getProfileEvents().get(0);
        assertEquals("Wrong event size for student1", 1, profileResult1.getEvents().size());
        assertEquals("Wrong profile ID for student1", student1, profileResult1.getProfileId());
        assertEquals("Wrong current resource", currentResourceId, profileResult1.getCurrentResourceId());
        assertEquals("Wrong score", 3, profileResult1.getEvents().get(0).getReaction());

        ProfileEventResponseDto profileResult2 = result.getProfileEvents().get(1);
        assertEquals("Wrong event size for student2", 1, profileResult2.getEvents().size());
        assertEquals("Wrong profile ID for student2", student2, profileResult2.getProfileId());
        assertEquals("Wrong current resource", currentResourceId, profileResult2.getCurrentResourceId());
        assertEquals("Wrong score", 0, profileResult2.getEvents().get(0).getScore());
    }

    @Test
    public void getStudentEventsWithoutEvents() throws Exception {
        //Map values for findAllStudentEvents
        Map<UUID, List<StudentEventEntity>> studentEventsMap = new HashMap<>();
        List<StudentEventEntity> events = new ArrayList<>();

        //Setting events
        UUID currentResourceId = UUID.randomUUID();

        //Setting entity values
        StudentEventEntity studentEventEntity = Mockito.spy(StudentEventEntity.class);
        when(studentEventEntity.getCurrentResourceId()).thenReturn(currentResourceId);
        when(studentEventEntity.getEventData()).thenReturn(null);
        events.add(studentEventEntity);

        //Adding student
        UUID studentId = UUID.randomUUID();
        studentEventsMap.put(studentId, events);

        //Setting context
        Context contextMock = new Context();
        UUID contextId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        contextMock.setId(contextId);
        contextMock.setCollectionId(collectionId);

        when(contextProfileEventService.findAllStudentEventsByContextId(contextId)).thenReturn(studentEventsMap);
        when(contextService.findById(contextId)).thenReturn(contextMock);

        StudentEventsResponseDto result = contextEventService.getStudentEvents(contextId);

        verify(contextProfileEventService, times(1)).findAllStudentEventsByContextId(contextId);
        verify(contextService, times(1)).findById(contextId);
        verify(gson, times(0)).fromJson(any(String.class), any());

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 1, result.getProfileEvents().size());

        ProfileEventResponseDto profileResult = result.getProfileEvents().get(0);
        assertEquals("Wrong event size", 0, profileResult.getEvents().size());
        assertEquals("Wrong profile ID", studentId, profileResult.getProfileId());
        assertEquals("Wrong current resource", currentResourceId, profileResult.getCurrentResourceId());
    }

}