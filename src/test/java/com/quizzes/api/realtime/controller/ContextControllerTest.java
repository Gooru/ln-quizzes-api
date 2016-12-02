package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.controller.ContextController;
import com.quizzes.api.common.dto.CommonContextGetResponseDto;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.ContextPostRequestDto;
import com.quizzes.api.common.dto.MetadataDto;
import com.quizzes.api.common.dto.controller.CollectionDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
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

    @Test
    public void assignContext() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), any(Lms.class))).thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        ProfileDto owner = new ProfileDto();
        owner.setId("1");
        owner.setFirstName("firstName");
        owner.setLastName("lastname");
        owner.setUsername("username");
        owner.setEmail("first@name.com");
        assignment.setOwner(owner);

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        assignee.setEmail("first1@name.com");
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
        assertSame(resultBody.getClass(), IdResponseDto.class);
        assertEquals("Response body is wrong", ((IdResponseDto) resultBody).getId(), idResponseDto.getId());
    }

    @Test
    public void assignContextEmptyAssignment() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), any(Lms.class))).thenReturn(idResponseDto);

        ResponseEntity<?> result = controller.assignContext(new ContextPostRequestDto(), Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in externalCollectionId"));
        assertThat(result.getBody().toString(), containsString("Error in owner"));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("Error in assignees"));
    }

    @Test
    public void assignContextStudentValidation() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), any(Lms.class))).thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();
        ProfileDto owner = new ProfileDto();
        owner.setId("1");
        owner.setFirstName("firstName");
        owner.setLastName("lastname");
        owner.setUsername("username");
        owner.setEmail("first@name.com");
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
        assertThat(result.getBody().toString(), containsString("Email is required"));

        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        assignee.setEmail("first@name.com");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertNotNull("Response body is null", result.getBody().toString());
    }

    @Test
    public void assignContextTeacherValidation() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), any(Lms.class))).thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        assignee.setEmail("first@name.com");
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
        assertThat(result.getBody().toString(), containsString("Email is required"));

        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");
        owner.setEmail("first@name.com");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertNotNull("Response body is null", result.getBody().toString());
    }

    @Test
    public void assignContextCollectionValidation() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), any(Lms.class))).thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        assignee.setEmail("first@name.com");
        List<ProfileDto> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        ProfileDto owner = new ProfileDto();
        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");
        owner.setEmail("first@name.com");
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
        assertThat(result.getBody().toString(), containsString("Error in externalCollectionId"));
        assertThat(result.getBody().toString(), containsString("{Errors=[Error in externalCollectionId: An External Collection ID is required]}"));

        //testing empty collection
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("{Errors=[Error in externalCollectionId: An External Collection ID is required]}"));
        assertThat(result.getBody().toString(), containsString("ID is required"));

        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertNotNull("Response body is null", result.getBody().toString());
    }

    @Test
    public void assignContextContextValidation() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), any(Lms.class))).thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        ProfileDto assignee = new ProfileDto();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        assignee.setEmail("first@name.com");
        List<ProfileDto> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        ProfileDto owner = new ProfileDto();
        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");
        owner.setEmail("first@name.com");
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
        assertNotNull("Response body is null", result.getBody().toString());
    }

    @Test
    public void getContext() throws Exception {
        ContextGetResponseDto response = new ContextGetResponseDto();

        List<IdResponseDto> assignees = new ArrayList<>();

        //Setting assignees
        UUID assigneeId = UUID.randomUUID();
        IdResponseDto assignee = new IdResponseDto();
        assignee.setId(assigneeId);
        assignees.add(assignee);
        response.setAssignees(assignees);

        //Setting contextData
        ContextDataDto contextDataDto = new ContextDataDto();
        Map<String, String> contextData = new HashMap<>();
        contextData.put("class", "234");
        contextDataDto.setContextMap(contextData);

        MetadataDto metadata = new MetadataDto();
        metadata.setDescription("First Partial");
        metadata.setTitle("Math 1st Grade");
        metadata.setDueDate(234234);
        metadata.setStartDate(324234);
        contextDataDto.setMetadata(metadata);
        response.setContextData(contextDataDto);

        //Setting id and collection
        UUID id = UUID.randomUUID();
        response.setId(id);

        //Setting collection
        UUID collectionId = UUID.randomUUID();
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(String.valueOf(collectionId));
        response.setCollection(collectionDto);

        //Setting owner
        IdResponseDto owner = new IdResponseDto();
        UUID ownerId = UUID.randomUUID();
        owner.setId(ownerId);
        response.setOwner(owner);

        when(contextService.getContext(any(UUID.class))).thenReturn(response);

        ResponseEntity<ContextGetResponseDto> result = controller.getContext(UUID.randomUUID(), "its_learning", UUID.randomUUID());

        verify(contextService, times(1)).getContext(any(UUID.class));
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code", HttpStatus.OK, result.getStatusCode());

        ContextGetResponseDto resultDto = result.getBody();

        assertNotNull("Context id is null", resultDto.getId());
        assertEquals("Wrong collection id", collectionId.toString(), resultDto.getCollection().getId());
        assertNotNull("ContextData is null", resultDto.getContextData());
        assertNotNull("Metadata is null", resultDto.getContextData().getMetadata());
        assertEquals("Wrong owner id", ownerId, resultDto.getOwner().getId());
        assertEquals("Wrong assignees size", 1, resultDto.getAssignees().size());
        assertEquals("Wrong first assignee id", assigneeId, resultDto.getAssignees().get(0).getId());
    }

    @Test
    public void getContextNotFound() throws Exception {
        when(contextService.getContext(any(UUID.class))).thenReturn(null);

        ResponseEntity<ContextGetResponseDto> result = controller.getContext(UUID.randomUUID(), "its_learning", UUID.randomUUID());

        verify(contextService, times(1)).getContext(any(UUID.class));
        assertNotNull("Response Entity is null", result);
        assertNull("Response body is not null", result.getBody());
        assertEquals("Invalid status code", HttpStatus.NOT_FOUND, result.getStatusCode());
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

        //Setting assignees
        List<IdResponseDto> assignees = new ArrayList<>();
        IdResponseDto asignee1 = new IdResponseDto();
        asignee1.setId(UUID.randomUUID());
        assignees.add(asignee1);
        IdResponseDto asignee2 = new IdResponseDto();
        asignee2.setId(UUID.randomUUID());
        assignees.add(asignee2);
        createdContextGetResponseDto.setAssignees(assignees);

        //Setting contextData
        ContextDataDto contextDataDto = new ContextDataDto();
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", "9e8f32bd-04fd-42c2-97f9-36addd23d850");
        contextDataDto.setContextMap(contextMap);

        MetadataDto metadata = new MetadataDto();
        metadata.setDescription("First Partial");
        metadata.setTitle("Math 1st Grade");
        metadata.setDueDate(234234);
        metadata.setStartDate(324234);
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

        List<IdResponseDto> profiles = result.getAssignees();
        assertEquals("Wrong list size for assignees", 2, profiles.size());
        assertNotNull("Profile1 id is null", profiles.get(0).getId());

        ContextDataDto contextResult = result.getContextData();
        assertEquals("Wrong size inside context map", 1, contextResult.getContextMap().size());
        assertEquals("Key title with invalid value in metadata", "Math 1st Grade", contextResult.getMetadata().getTitle());
        assertEquals("Key description with invalid value in metadata", "First Partial", contextResult.getMetadata().getDescription());
    }

    @Test
    public void getAssignedContexts() throws Exception {
        List<ContextAssignedGetResponseDto> contexts = new ArrayList<>();
        ContextAssignedGetResponseDto contextAssigned = new ContextAssignedGetResponseDto();

        //Setting collection
        contextAssigned.setId(UUID.randomUUID());
        contextAssigned.setCollection(new CollectionDto(UUID.randomUUID().toString()));

        //Setting contextData
        ContextDataDto contextDataDto = new ContextDataDto();
        Map<String, String> contextData = new HashMap<>();
        contextData.put("class", "234");
        contextDataDto.setContextMap(contextData);

        MetadataDto metadata = new MetadataDto();
        metadata.setDescription("First Partial");
        metadata.setTitle("Math 1st Grade");
        metadata.setDueDate(234234);
        metadata.setStartDate(324234);
        contextDataDto.setMetadata(metadata);
        contextAssigned.setContextData(contextDataDto);

        //Setting owner
        IdResponseDto ownerData = new IdResponseDto();
        ownerData.setId(UUID.randomUUID());
        contextAssigned.setOwner(ownerData);
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

        IdResponseDto ownerResult = result.getOwner();
        assertNotNull("Owner id is null", ownerResult);

        ContextDataDto contextResult = result.getContextData();
        assertEquals("Wrong size inside context result", 1, contextResult.getContextMap().size());
        assertEquals("Wrong due date", 234234,contextResult.getMetadata().getDueDate());
        assertEquals("Wrong start date", 324234,contextResult.getMetadata().getStartDate());
        assertEquals("Wrong title", "Math 1st Grade",contextResult.getMetadata().getTitle());
        assertEquals("Wrong description", "First Partial",contextResult.getMetadata().getDescription());
    }

    @Test
    public void updateContext() throws Exception {
        Context contextResult = new Context(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "{\"context\":\"value\"}", null);
        when(contextService.update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class))).thenReturn(contextResult);

        ResponseEntity<IdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());

        verify(contextService, times(1)).update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Invalid status code", contextResult.getId(), result.getBody().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateContextException() throws Exception {
        when(contextService.update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class))).thenReturn(null);
        ResponseEntity<IdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());
    }

}