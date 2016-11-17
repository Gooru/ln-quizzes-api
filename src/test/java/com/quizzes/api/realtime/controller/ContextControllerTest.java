package com.quizzes.api.realtime.controller;

import com.google.gson.JsonArray;
import com.quizzes.api.common.controller.ContextController;
import com.quizzes.api.common.dto.CommonContextGetResponseDto;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextIdResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.controller.AssignmentDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.dto.controller.UuidDto;
import com.quizzes.api.common.dto.controller.request.OnResourceEventRequestDto;
import com.quizzes.api.common.dto.controller.request.ResourceDto;
import com.quizzes.api.common.dto.controller.response.AnswerDto;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.GroupProfileService;
import com.quizzes.api.common.service.GroupService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.json.JsonParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextControllerTest {

    @InjectMocks
    private ContextController controller = new ContextController();

    @Mock
    private ContextService contextService;

    @Mock
    private GroupService groupService;

    @Mock
    private GroupProfileService groupProfileService;

    @Mock
    private JsonParser jsonParser;

    @Test
    public void assignContext() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDto.class), any(Lms.class))).thenReturn(context);

        AssignmentDto assignment = new AssignmentDto();

        ProfileDto owner = new ProfileDto();
        owner.setId("1");
        owner.setFirstName("firstName");
        owner.setLastName("lastname");
        owner.setUsername("username");
        assignment.setOwner(owner);

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDto> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);
        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        Object resultBody = result.getBody();
        assertSame(resultBody.getClass(), ContextIdResponseDto.class);
        assertEquals("Response body is wrong", ((ContextIdResponseDto) resultBody).getId(), context.getId());
    }

    @Ignore
    @Test
    public void assignContextEmptyAssignment() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDto.class), any(Lms.class))).thenReturn(context);

        ResponseEntity<?> result = controller.assignContext(new AssignmentDto(), Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("Error in owner"));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("Error in assignees"));
    }

    @Ignore
    @Test
    public void assignContextStudentValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDto.class), any(Lms.class))).thenReturn(context);

        AssignmentDto assignment = new AssignmentDto();
        ProfileDto owner = new ProfileDto();
        owner.setId("1");
        owner.setFirstName("firstName");
        owner.setLastName("lastname");
        owner.setUsername("username");
        assignment.setOwner(owner);
        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        //Testing no assignees
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), not(containsString("Error in owner")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in assignees"));
        assertThat(result.getBody().toString(), containsString("At least one assignee is required"));

        ProfileDto assignee = new ProfileDto();
        List<ProfileDto> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        //testing empty assignee
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in assignees"));
        assertThat(result.getBody().toString(), containsString("ID is required"));
        assertThat(result.getBody().toString(), containsString("Firstname is required"));
        assertThat(result.getBody().toString(), containsString("Lastname is required"));
        assertThat(result.getBody().toString(), containsString("Username is required"));

        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Ignore
    @Test
    public void assignContextTeacherValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDto.class), any(Lms.class))).thenReturn(context);

        AssignmentDto assignment = new AssignmentDto();

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDto> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        //Testing no owner
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), not(containsString("Error in owners")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in owner"));
        assertThat(result.getBody().toString(), containsString("A Owner is required"));

        ProfileDto owner = new ProfileDto();
        assignment.setOwner(owner);

        //testing empty owner
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in owner"));
        assertThat(result.getBody().toString(), containsString("ID is required"));
        assertThat(result.getBody().toString(), containsString("Firstname is required"));
        assertThat(result.getBody().toString(), containsString("Lastname is required"));
        assertThat(result.getBody().toString(), containsString("Username is required"));

        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Ignore
    @Test
    public void assignContextCollectionValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDto.class), any(Lms.class))).thenReturn(context);

        AssignmentDto assignment = new AssignmentDto();

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDto> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        ProfileDto owner = new ProfileDto();
        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");
        assignment.setOwner(owner);

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        //Testing no collection
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in assignees")));
        assertThat(result.getBody().toString(), not(containsString("Error in owners")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("A Collection is required"));

        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        //testing empty collection
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("ID is required"));

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Ignore
    @Test
    public void assignContextContextValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDto.class), any(Lms.class))).thenReturn(context);

        AssignmentDto assignment = new AssignmentDto();

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDto> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        ProfileDto owner = new ProfileDto();
        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");
        assignment.setOwner(owner);

        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        //Testing no context
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in assignees")));
        assertThat(result.getBody().toString(), not(containsString("Error in owners")));
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("A ContextData is required"));

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

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

        startContext.setAttempt(list);

        when(contextService.startContextEvent(any(UUID.class), any(UUID.class))).thenReturn(startContext);

        ResponseEntity<StartContextEventResponseDto> result = controller.startContextEvent(UUID.randomUUID(), "quizzes", UUID.randomUUID());

        verify(contextService, times(1)).startContextEvent(any(UUID.class), any(UUID.class));

        StartContextEventResponseDto resultBody = result.getBody();
        assertSame(resultBody.getClass(), StartContextEventResponseDto.class);
        assertEquals("Wrong resource id is null", resourceId, resultBody.getCurrentResourceId());
        assertEquals("Wrong id", id, resultBody.getId());
        assertEquals("Wrong collection id", collection.getId(), resultBody.getCollection().getId());
        assertEquals("Wrong collection id", 1, resultBody.getAttempt().size());
        assertTrue("Answer key not found", resultBody.getAttempt().get(0).containsKey("answer"));
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void registerResource() throws Exception {
        AnswerDto answerDto = new AnswerDto("1");
        List<AnswerDto> answerDtoList = new ArrayList<>();
        answerDtoList.add(answerDto);
        ResourceDto resource = new ResourceDto(UUID.randomUUID(), 120, 3, answerDtoList);
        OnResourceEventRequestDto requestBody = new OnResourceEventRequestDto(resource);

        ResponseEntity<?> result = controller.onResourceEvent("1", "1", requestBody, "quizzes", UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Body is not null", null, result.getBody());
    }

    @Test
    public void finishContextEvent() throws Exception {
        ResponseEntity<?> result = controller.finishContextEvent(UUID.randomUUID(), "its_learning", UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
    }

    @Test
    public void getContext() throws Exception {
        ContextGetResponseDto response = new ContextGetResponseDto();
        UUID id = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID assigneeId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();

        List<Map<String, Object>> assignees = new ArrayList<>();

        Map<String, Object> assignee = new HashMap<>();
        assignee.put("id", assigneeId);
        assignees.add(assignee);

        Map<String, Object> owner = new HashMap<>();
        owner.put("id", ownerId);

        Map<String, Object> contextData = new HashMap<>();
        contextData.put("metadata", new HashMap<>());
        contextData.put("contextData", new HashMap<>());

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(String.valueOf(collectionId));

        response.setId(id);
        response.setContextDataResponse(contextData);
        response.setCollection(collectionDto);
        response.setOwnerResponse(owner);
        response.setAssigneesResponse(assignees);

        when(contextService.getContext(any(UUID.class))).thenReturn(response);

        ResponseEntity<ContextGetResponseDto> result = controller.getContext(UUID.randomUUID(), "its_learning", UUID.randomUUID());

        verify(contextService, times(1)).getContext(any(UUID.class));
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code", HttpStatus.OK, result.getStatusCode());

        ContextGetResponseDto resultDto = result.getBody();

        assertNotNull("Context id is null", resultDto.getId());
        assertEquals("Wrong collection id", collectionId.toString(), resultDto.getCollection().getId());
        assertEquals("Wrong contextData", contextData, resultDto.getContextDataResponse());
        assertEquals("Wrong owner data", owner, resultDto.getOwnerResponse());
        assertEquals("Wrong assignees size", 1, resultDto.getAssigneesResponse().size());
    }

    @Test
    public void findCreatedContexts() throws Exception {

        List<Context> contextsCreatedByOwner = new ArrayList<>();

        Context context = new Context();
        context.setId(UUID.randomUUID());
        context.setCollectionId(UUID.randomUUID());
        context.setGroupId(UUID.randomUUID());
        context.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");

        contextsCreatedByOwner.add(context);

        when(contextService.findContextByOwnerId(any(UUID.class))).thenReturn(contextsCreatedByOwner);

        List<CreatedContextGetResponseDto> createdContextGetResponseDtos = new ArrayList<>();
        CreatedContextGetResponseDto createdContextGetResponseDto = new CreatedContextGetResponseDto();
        createdContextGetResponseDto.setId(UUID.randomUUID());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(UUID.randomUUID().toString());
        createdContextGetResponseDto.setCollection(collectionDto);
        createdContextGetResponseDto.setId(UUID.randomUUID());
        List<UuidDto> assignees = new ArrayList<>();
        UuidDto asignee1 = new UuidDto();
        asignee1.setId(UUID.randomUUID());
        assignees.add(asignee1);
        UuidDto asignee2 = new UuidDto();
        asignee2.setId(UUID.randomUUID());
        assignees.add(asignee2);
        createdContextGetResponseDto.setAssignees(assignees);
        CommonContextGetResponseDto.ContextDataDto contextDataDto = new CommonContextGetResponseDto.ContextDataDto();
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", "9e8f32bd-04fd-42c2-97f9-36addd23d850");
        contextDataDto.setContextMap(contextMap);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("description", "First Partial");
        metadata.put("title", "Math 1st Grade");
        contextDataDto.setMetadata(metadata);
        createdContextGetResponseDto.setContextData(contextDataDto);
        createdContextGetResponseDtos.add(createdContextGetResponseDto);

        when(contextService.findCreatedContexts(any(UUID.class))).thenReturn(createdContextGetResponseDtos);

        Map<String, String> filter = new HashMap<>();
        filter.put("classId", UUID.randomUUID().toString());
        ResponseEntity<List<CreatedContextGetResponseDto>> response = controller
                .findCreatedContexts(Lms.its_learning.getLiteral(), UUID.randomUUID(), filter);

        assertNotNull("Response is Null", response);
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong list size for assignments", 1, response.getBody().size());

        CreatedContextGetResponseDto result = response.getBody().get(0);
        assertNotNull("Body is null", result);
        assertNotNull("Context id is null", result.getId());

        assertNotNull("Collection id is null", result.getCollection().getId());

        List<UuidDto> profiles = result.getAssignees();
        assertEquals("Wrong list size for assignees", 2, profiles.size());
        assertNotNull("Profile1 id is null", profiles.get(0).getId());

        CommonContextGetResponseDto.ContextDataDto contextResult = result.getContextData();
        assertEquals("Wrong size inside context map", 1, contextResult.getContextMap().size());
        assertEquals("Wrong size inside metadata", 2, contextResult.getMetadata().size());
        assertEquals("Key title with invalid value in metadata", "Math 1st Grade", contextResult.getMetadata().get("title"));
        assertEquals("Key description with invalid value in metadata", "First Partial", contextResult.getMetadata().get("description"));
    }

    @Test
    public void getAssignedContexts() throws Exception {
        List<ContextAssignedGetResponseDto> contexts = new ArrayList<>();
        ContextAssignedGetResponseDto contextAssigned = new ContextAssignedGetResponseDto();
        contextAssigned.setId(UUID.randomUUID());
        contextAssigned.setCollection(new CollectionDto(UUID.randomUUID().toString()));

        Map<String, Object> contextDataMap = new HashMap<>();
        contextDataMap.put("contextMap", new HashMap<>());
        contextDataMap.put("metaData", new HashMap<>());
        contextAssigned.setContextResponse(contextDataMap);

        Map<String, Object> ownerData = new HashMap<>();
        ownerData.put("id", UUID.randomUUID().toString());
        ownerData.put("firstName", "name");
        ownerData.put("lastName", "last");
        ownerData.put("username", "username");
        contextAssigned.setOwnerResponse(ownerData);
        contexts.add(contextAssigned);

        when(contextService.getAssignedContexts(any(UUID.class))).thenReturn(contexts);

        ResponseEntity<List<ContextAssignedGetResponseDto>> response = controller.getAssignedContexts("its_learning", UUID.randomUUID());

        verify(contextService, times(1)).getAssignedContexts(any(UUID.class));

        List<ContextAssignedGetResponseDto> list = response.getBody();
        assertNotNull("Response is Null", response);
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong list size for assignments", 1, list.size());

        ContextAssignedGetResponseDto result = list.get(0);
        assertNotNull("Body is null", result);
        assertNotNull("Context id is null", result.getId());

        assertNotNull("Collection id is null", result.getCollection().getId());

        Map<String, Object> ownerResult = result.getOwnerResponse();
        assertNotNull("Owner id is null", ownerResult.get("id"));
        assertNotNull("First name id is null", ownerResult.get("firstName"));
        assertNotNull("Last name id is null", ownerResult.get("lastName"));
        assertNotNull("Username id is null", ownerResult.get("username"));

        Map<String, Object> contextResult = result.getContextResponse();
        assertEquals("Wrong size inside context result", 2, contextResult.size());
        assertTrue("Missing metaData key in context result", contextResult.containsKey("metaData"));
        assertTrue("Missing contextMap key in context result", contextResult.containsKey("contextMap"));
    }

    @Test
    public void updateContext() throws Exception {
        Context contextResult = new Context(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "{\"context\":\"value\"}", null);
        when(contextService.update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class))).thenReturn(contextResult);

        ResponseEntity<ContextIdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());

        verify(contextService, times(1)).update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Invalid status code", contextResult.getId(), result.getBody().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateContextException() throws Exception {
        when(contextService.update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class))).thenReturn(null);
        ResponseEntity<ContextIdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());
    }

}