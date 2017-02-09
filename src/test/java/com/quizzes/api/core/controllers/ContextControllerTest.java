package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.mappers.EntityMapper;
import com.quizzes.api.core.services.ContextService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContextControllerTest {

    @InjectMocks
    private ContextController controller;

    @Mock
    private ContextService contextService;

    @Spy
    private EntityMapper entityMapper;

    private UUID contextId;
    private UUID collectionId;
    private UUID classId;
    private UUID profileId;
    private UUID contextProfileId;
    private UUID currentContextProfileId;
    private String token;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        classId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        currentContextProfileId = UUID.randomUUID();
        token = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @Test
    public void createContext() throws Exception {
        ContextPostRequestDto assignment = new ContextPostRequestDto();
        assignment.setCollectionId(collectionId);
        assignment.setClassId(classId);
        assignment.setContextData(new ContextDataDto());

        when(contextService.createContext(assignment, profileId, token)).thenReturn(contextId);

        ResponseEntity<?> result = controller.createContext(assignment, profileId, token);
        assertNotNull("Response is null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Response body is wrong", contextId, ((IdResponseDto) result.getBody()).getId());
    }

    @Test
    public void assignContextEmptyAssignment() throws Exception {
        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token))).thenReturn(null);

        ResponseEntity<?> result = controller.createContext(new ContextPostRequestDto(), profileId, token);
        assertNotNull("Response is null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collectionId"));
        assertThat(result.getBody().toString(), containsString("Error in contextData"));
    }

    @Test
    public void assignContextCollectionValidation() throws Exception {
        ContextPostRequestDto assignment = new ContextPostRequestDto();
        assignment.setContextData(new ContextDataDto());

        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token)))
                .thenReturn(contextId);

        //Testing no collection
        ResponseEntity<?> result = controller.createContext(assignment, profileId, token);
        assertNotNull("Response is null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in contextData")));
        assertThat(result.getBody().toString(), containsString("Error in collectionId"));
        assertThat(result.getBody().toString(), containsString("A Collection ID is required"));

        assignment.setCollectionId(UUID.randomUUID());

        result = controller.createContext(assignment, profileId, token);
        assertNotNull("Response is null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNotNull("Response body is null", result.getBody());
    }

    @Test
    public void assignContextContextDataValidation() throws Exception {
        ContextPostRequestDto assignment = new ContextPostRequestDto();
        assignment.setCollectionId(UUID.randomUUID());

        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token)))
                .thenReturn(contextId);

        //Testing no context
        ResponseEntity<?> result = controller.createContext(assignment, profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("A ContextData is required"));

        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        result = controller.createContext(assignment, profileId, token);
        assertNotNull("Response is null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNotNull("Response body is null", result.getBody());
    }

    @Test
    public void getCreatedContexts() throws Exception {
        List<ContextEntity> contextEntities = new ArrayList<>();
        ContextEntity contextEntity = createContextEntityMock();
        contextEntities.add(contextEntity);

        when(contextService.findCreatedContexts(any(UUID.class))).thenReturn(contextEntities);

        ResponseEntity<List<ContextGetResponseDto>> response = controller.getCreatedContexts(UUID.randomUUID());

        verify(contextService, times(1)).findCreatedContexts(any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong number of created contexts", 1, response.getBody().size());
        assertEquals("Invalid context id", contextEntity.getContextId(), response.getBody().get(0).getContextId());
    }

    @Test
    public void getCreatedContext() throws Exception {
        ContextEntity contextEntity = createContextEntityMock();

        when(contextService.findCreatedContext(any(UUID.class), any(UUID.class))).thenReturn(contextEntity);

        ResponseEntity<ContextGetResponseDto> response =
                controller.getCreatedContext(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findCreatedContext(any(UUID.class), any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Invalid context id", contextEntity.getContextId(), response.getBody().getContextId());
    }

    @Test
    public void getAssignedContexts() throws Exception {
        List<AssignedContextEntity> assignedContextEntities = new ArrayList<>();
        AssignedContextEntity assignedContextEntity = createAssignedContextEntityMock();
        assignedContextEntities.add(assignedContextEntity);

        when(contextService.findAssignedContexts(any(UUID.class))).thenReturn(assignedContextEntities);

        ResponseEntity<List<ContextGetResponseDto>> response = controller.getAssignedContexts(UUID.randomUUID());

        verify(contextService, times(1)).findAssignedContexts(any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong number of assigned contexts", 1, response.getBody().size());
        assertEquals("Invalid context id", assignedContextEntity.getContextId(),
                response.getBody().get(0).getContextId());
        assertTrue("HasStarted is false", response.getBody().get(0).getHasStarted());
    }

    @Test
    public void getAssignedContext() throws Exception {
        AssignedContextEntity assignedContextEntity = createAssignedContextEntityMock();

        when(contextService.findAssignedContext(any(UUID.class), any(UUID.class))).thenReturn(assignedContextEntity);

        ResponseEntity<ContextGetResponseDto> response =
                controller.getAssignedContext(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findAssignedContext(any(UUID.class), any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Invalid context id", assignedContextEntity.getContextId(), response.getBody().getContextId());
        assertTrue("HasStarted is false", response.getBody().getHasStarted());
    }

    // TODO We need to clarify how will be integrated the Update for Contexts in Nile
    /*
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
    */

    private ContextEntity createContextEntityMock() {
        ContextEntity contextEntity = mock(ContextEntity.class);
        String contextData = "{" +
                "  'contextMap': {" +
                "    'courseId': 'course-id-1'" +
                "  }," +
                "  'metadata': {" +
                "    'title': 'metadata title'," +
                "    'description': 'metadata description'" +
                "  }" +
                "}";
        when(contextEntity.getContextId()).thenReturn(contextId);
        when(contextEntity.getCollectionId()).thenReturn(collectionId);
        when(contextEntity.getClassId()).thenReturn(classId);
        when(contextEntity.getProfileId()).thenReturn(profileId);
        when(contextEntity.getContextData()).thenReturn(contextData);
        when(contextEntity.getCreatedAt()).thenReturn(createdAt);
        when(contextEntity.getUpdatedAt()).thenReturn(updatedAt);
        return contextEntity;
    }

    private AssignedContextEntity createAssignedContextEntityMock() {
        AssignedContextEntity contextEntity = mock(AssignedContextEntity.class);
        String contextData = "{" +
                "  'contextMap': {" +
                "    'courseId': 'course-id-1'" +
                "  }," +
                "  'metadata': {" +
                "    'title': 'metadata title'," +
                "    'description': 'metadata description'" +
                "  }" +
                "}";
        when(contextEntity.getContextProfileId()).thenReturn(contextProfileId);
        when(contextEntity.getCurrentContextProfileId()).thenReturn(currentContextProfileId);
        when(contextEntity.getContextId()).thenReturn(contextId);
        when(contextEntity.getCollectionId()).thenReturn(collectionId);
        when(contextEntity.getClassId()).thenReturn(classId);
        when(contextEntity.getProfileId()).thenReturn(profileId);
        when(contextEntity.getContextData()).thenReturn(contextData);
        when(contextEntity.getCreatedAt()).thenReturn(createdAt);
        when(contextEntity.getUpdatedAt()).thenReturn(updatedAt);
        return contextEntity;
    }

}