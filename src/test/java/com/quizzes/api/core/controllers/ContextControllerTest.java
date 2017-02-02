package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.*;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.services.ContextService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextControllerTest {

    @InjectMocks
    private ContextController controller = new ContextController();

    @Mock
    private ContextService contextService;

    private UUID contextId;
    private UUID collectionId;
    private String profileId;
    private UUID classId;
    private UUID unitId;
    private UUID memberId;
    private UUID ownerProfileId;
    private UUID contextProfileId;
    private Timestamp createdAt;
    private String token;

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        unitId = UUID.randomUUID();
        classId = UUID.randomUUID();
        ownerProfileId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        createdAt = Timestamp.from(Instant.now());
        profileId = UUID.randomUUID().toString();
        token = UUID.randomUUID().toString();
    }

    @Test
    public void assignContext() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        assignment.setCollectionId(collectionId);
        assignment.setClassId(classId);

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        when(contextService.createContext(assignment, profileId, token)).thenReturn(idResponseDto);

        ResponseEntity<?> result = controller.assignContext(assignment, profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        Object resultBody = result.getBody();
        assertSame(resultBody.getClass(), IdResponseDto.class);
        assertEquals("Response body is wrong", ((IdResponseDto) resultBody).getId(), idResponseDto.getId());
    }

    @Test
    public void assignContextEmptyAssignment() throws Exception {
//        IdResponseDto idResponseDto = new IdResponseDto();
//        UUID contextId = UUID.randomUUID();
//        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token))).thenReturn(null);

        ResponseEntity<?> result = controller.assignContext(new ContextPostRequestDto(), profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collectionId"));
        assertThat(result.getBody().toString(), containsString("Error in contextData"));
    }

    @Test
    public void assignContextOwnerValidation() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token)))
                .thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        assignment.setCollectionId(UUID.randomUUID());

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        ResponseEntity<?> result = controller.assignContext(assignment, null, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), not(containsString("Error in contextData")));
        assertThat(result.getBody().toString(), containsString("Error in profileId"));
        assertThat(result.getBody().toString(), containsString("profileId is required"));

        result = controller.assignContext(assignment, profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertNotNull("Response body is null", result.getBody().toString());
    }

    @Test
    public void assignContextCollectionValidation() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token)))
                .thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        //Testing no collection
        ResponseEntity<?> result = controller.assignContext(assignment, profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in contextData")));
        assertThat(result.getBody().toString(), containsString("Error in collectionId"));
        assertThat(result.getBody().toString(), containsString("A Collection ID is required"));

        assignment.setCollectionId(UUID.randomUUID());

        result = controller.assignContext(assignment, profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertNotNull("Response body is null", result.getBody().toString());
    }

    @Test
    public void assignContextContextDataValidation() throws Exception {
        IdResponseDto idResponseDto = new IdResponseDto();
        UUID contextId = UUID.randomUUID();
        idResponseDto.setId(contextId);
        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token)))
                .thenReturn(idResponseDto);

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        assignment.setCollectionId(UUID.randomUUID());

        //Testing no context
        ResponseEntity<?> result = controller.assignContext(assignment, profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("A ContextData is required"));

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        result = controller.assignContext(assignment, profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertNotNull("Response body is null", result.getBody().toString());
    }

    @Test
    public void findCreatedContexts() throws Exception {

        List<Context> contextsCreatedByOwner = new ArrayList<>();

        Context context = new Context();
        context.setId(UUID.randomUUID());
        context.setCollectionId(UUID.randomUUID());
        context.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");

        contextsCreatedByOwner.add(context);

        List<ContextGetResponseDto> contextGetResponseDtos = new ArrayList<>();
        ContextGetResponseDto contextGetResponseDto = new ContextGetResponseDto();
        contextGetResponseDto.setId(UUID.randomUUID());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(UUID.randomUUID().toString());
        contextGetResponseDto.setCollection(collectionDto);
        contextGetResponseDto.setId(UUID.randomUUID());

        //Setting assignees
        List<IdResponseDto> assignees = new ArrayList<>();
        IdResponseDto asignee1 = new IdResponseDto();
        asignee1.setId(UUID.randomUUID());
        assignees.add(asignee1);
        IdResponseDto asignee2 = new IdResponseDto();
        asignee2.setId(UUID.randomUUID());
        assignees.add(asignee2);
        contextGetResponseDto.setAssignees(assignees);

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

        contextGetResponseDto.setContextData(contextDataDto);
        contextGetResponseDtos.add(contextGetResponseDto);

        when(contextService.findCreatedContexts(any(UUID.class))).thenReturn(contextGetResponseDtos);

        Map<String, String> filter = new HashMap<>();
        filter.put("classId", UUID.randomUUID().toString());
        ResponseEntity<List<ContextGetResponseDto>> response =
                controller.getCreatedContexts("nothing", UUID.randomUUID(), filter);

        assertNotNull("Response is Null", response);
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong list size for assignments", 1, response.getBody().size());

        ContextGetResponseDto result = response.getBody().get(0);
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
        List<ContextGetResponseDto> contexts = new ArrayList<>();
        ContextGetResponseDto contextAssigned = new ContextGetResponseDto();

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

        when(contextService.getAssignedContexts(any(UUID.class),any(Boolean.class), any(Long.class), any(Long.class))).thenReturn(contexts);

        ResponseEntity<List<ContextGetResponseDto>> response = controller.getAssignedContexts("its_learning", UUID.randomUUID(), null, null, null);

        verify(contextService, times(1)).getAssignedContexts(any(UUID.class), any(Boolean.class), any(Long.class), any(Long.class));

        List<ContextGetResponseDto> list = response.getBody();
        assertNotNull("Response is Null", response);
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong list size for assignments", 1, list.size());

        ContextGetResponseDto result = list.get(0);
        assertNotNull("Body is null", result);
        assertNotNull("Context id is null", result.getId());

        assertNotNull("Collection id is null", result.getCollection().getId());

        IdResponseDto ownerResult = result.getOwner();
        assertNotNull("Owner id is null", ownerResult);

        ContextDataDto contextResult = result.getContextData();
        assertEquals("Wrong size inside context result", 1, contextResult.getContextMap().size());
        assertEquals("Wrong due date", 234234, contextResult.getMetadata().getDueDate());
        assertEquals("Wrong start date", 324234, contextResult.getMetadata().getStartDate());
        assertEquals("Wrong title", "Math 1st Grade", contextResult.getMetadata().getTitle());
        assertEquals("Wrong description", "First Partial", contextResult.getMetadata().getDescription());
    }

    @Test
    public void updateContext() throws Exception {
        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(UUID.randomUUID());
        contextResult.setContextData("{\"context\":\"value\"}");
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextService.update(any(UUID.class), any(UUID.class), any(ContextPutRequestDto.class))).thenReturn(contextResult);

        ResponseEntity<IdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());

        verify(contextService, times(1)).update(any(UUID.class), any(UUID.class), any(ContextPutRequestDto.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Invalid status code", contextResult.getId(), result.getBody().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateContextException() throws Exception {
        when(contextService.update(any(UUID.class), any(UUID.class), any(ContextPutRequestDto.class))).thenReturn(null);
        ResponseEntity<IdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());
    }

}