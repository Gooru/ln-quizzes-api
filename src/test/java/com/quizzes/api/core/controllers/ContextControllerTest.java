package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.services.ContextService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;
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
    private ContextController controller;

    @Mock
    private ContextService contextService;

    private UUID collectionId;
    private UUID profileId;
    private UUID classId;
    private String token;

    @Before
    public void before() throws Exception {
        collectionId = UUID.randomUUID();
        classId = UUID.randomUUID();
        profileId = UUID.randomUUID();
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
        when(contextService.createContext(any(ContextPostRequestDto.class), eq(profileId), eq(token))).thenReturn(null);

        ResponseEntity<?> result = controller.assignContext(new ContextPostRequestDto(), profileId, token);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collectionId"));
        assertThat(result.getBody().toString(), containsString("Error in contextData"));
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
        List<ContextGetResponseDto> contextGetResponseDtos = new ArrayList<>();
        ContextGetResponseDto contextGetResponseDto = new ContextGetResponseDto();
        contextGetResponseDto.setContextId(UUID.randomUUID());
        contextGetResponseDtos.add(contextGetResponseDto);

        when(contextService.findCreatedContexts(any(UUID.class))).thenReturn(contextGetResponseDtos);

        ResponseEntity<List<ContextGetResponseDto>> response = controller.getCreatedContexts(UUID.randomUUID());

        verify(contextService, times(1)).findCreatedContexts(any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong number of created contexts", 1, response.getBody().size());
        assertEquals("Invalid context id", contextGetResponseDto.getContextId(),
                response.getBody().get(0).getContextId());
    }

    @Test
    public void findCreatedContext() throws Exception {
        ContextGetResponseDto contextGetResponseDto = new ContextGetResponseDto();
        contextGetResponseDto.setContextId(UUID.randomUUID());

        when(contextService.findCreatedContext(any(UUID.class), any(UUID.class))).thenReturn(contextGetResponseDto);

        ResponseEntity<ContextGetResponseDto> response =
                controller.getCreatedContext(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findCreatedContext(any(UUID.class), any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Invalid context id", contextGetResponseDto.getContextId(), response.getBody().getContextId());
    }

    @Test
    public void getAssignedContexts() throws Exception {
        List<ContextGetResponseDto> contextGetResponseDtos = new ArrayList<>();
        ContextGetResponseDto contextGetResponseDto = new ContextGetResponseDto();
        contextGetResponseDto.setContextId(UUID.randomUUID());
        contextGetResponseDtos.add(contextGetResponseDto);

        when(contextService.findAssignedContexts(any(UUID.class))).thenReturn(contextGetResponseDtos);

        ResponseEntity<List<ContextGetResponseDto>> response = controller.getAssignedContexts(UUID.randomUUID());

        verify(contextService, times(1)).findAssignedContexts(any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong number of assigned contexts", 1, response.getBody().size());
        assertEquals("Invalid context id", contextGetResponseDto.getContextId(),
                response.getBody().get(0).getContextId());
    }

    @Test
    public void getAssignedContext() throws Exception {
        ContextGetResponseDto contextGetResponseDto = new ContextGetResponseDto();
        contextGetResponseDto.setContextId(UUID.randomUUID());

        when(contextService.findAssignedContext(any(UUID.class), any(UUID.class))).thenReturn(contextGetResponseDto);

        ResponseEntity<ContextGetResponseDto> response =
                controller.getAssignedContext(UUID.randomUUID(), UUID.randomUUID());

        verify(contextService, times(1)).findAssignedContext(any(UUID.class), any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Invalid context id", contextGetResponseDto.getContextId(), response.getBody().getContextId());
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

}