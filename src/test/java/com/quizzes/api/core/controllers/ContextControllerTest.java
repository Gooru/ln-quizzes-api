package com.quizzes.api.core.controllers;

import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.InvalidRequestBodyException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.mappers.EntityMapper;
import com.quizzes.api.core.services.ConfigurationService;
import com.quizzes.api.core.services.ContextService;
import com.quizzes.api.util.QuizzesUtils;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
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

    @Mock
    private ConfigurationService configurationService;

    @Spy
    private EntityMapper entityMapper;

    private UUID contextId;
    private UUID collectionId;
    private UUID classId;
    private UUID profileId;
    private UUID contextProfileId;
    private UUID currentContextProfileId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private UUID anonymousId;
    private String token;

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        classId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        currentContextProfileId = UUID.randomUUID();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
        anonymousId = QuizzesUtils.getAnonymousId();
        token = UUID.randomUUID().toString();
    }

    @Test
    public void createContext() throws Exception {
        ContextPostRequestDto contextRequestDto = new ContextPostRequestDto();
        contextRequestDto.setCollectionId(collectionId);
        contextRequestDto.setClassId(classId);
        contextRequestDto.setContextData(new ContextDataDto());

        doReturn(contextId)
                .when(contextService).createContext(any(UUID.class), any(UUID.class), any(UUID.class),
                any(ContextDataDto.class), anyBoolean(), anyString());

        ResponseEntity<?> result = controller.createContext(contextRequestDto, profileId.toString(), token);

        verify(contextService, times(1)).createContext(any(UUID.class), any(UUID.class), any(UUID.class),
                any(ContextDataDto.class), anyBoolean(), anyString());
        verify(contextService, times(0))
                .createContextWithoutClassId(any(UUID.class), any(UUID.class), anyBoolean(), anyString());
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Response body is wrong", contextId, ((IdResponseDto) result.getBody()).getId());
    }


    @Test
    public void createContextForPreview() throws Exception {
        ContextPostRequestDto contextRequestDto = new ContextPostRequestDto();
        contextRequestDto.setCollectionId(collectionId);
        contextRequestDto.setContextData(new ContextDataDto());

        doReturn(contextId)
                .when(contextService)
                .createContextWithoutClassId(any(UUID.class), any(UUID.class), anyBoolean(), anyString());

        ResponseEntity<?> result = controller.createContext(contextRequestDto, profileId.toString(), token);

        verify(contextService, times(0))
                .createContext(any(UUID.class), any(UUID.class), any(UUID.class), any(ContextDataDto.class),
                        anyBoolean(), anyString());
        verify(contextService, times(1))
                .createContextWithoutClassId(any(UUID.class), any(UUID.class), anyBoolean(), anyString());
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Response body is wrong", contextId, ((IdResponseDto) result.getBody()).getId());
    }

    @Test
    public void createContextForAnonymous() throws Exception {
        ContextPostRequestDto contextRequestDto = new ContextPostRequestDto();
        contextRequestDto.setCollectionId(collectionId);
        contextRequestDto.setContextData(new ContextDataDto());

        doReturn(contextId)
                .when(contextService)
                .createContextWithoutClassId(any(UUID.class), any(UUID.class), anyBoolean(), anyString());

        ResponseEntity<?> result = controller.createContext(contextRequestDto, "anonymous", token);

        verify(contextService, times(0)).createContext(any(UUID.class), any(UUID.class), any(UUID.class),
                any(ContextDataDto.class), anyBoolean(), anyString());
        verify(contextService, times(1))
                .createContextWithoutClassId(any(UUID.class), eq(anonymousId), anyBoolean(), anyString());
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Response body is wrong", contextId, ((IdResponseDto) result.getBody()).getId());
    }

    @Test(expected = InvalidRequestBodyException.class)
    public void createContextWithoutCollectionId() throws Exception {
        ContextPostRequestDto contextRequestDto = new ContextPostRequestDto();
        contextRequestDto.setContextData(new ContextDataDto());

        controller.createContext(contextRequestDto, profileId.toString(), token);
    }

    @Test(expected = InvalidRequestBodyException.class)
    public void createContextWithoutContextData() throws Exception {
        ContextPostRequestDto contextRequestDto = new ContextPostRequestDto();
        contextRequestDto.setCollectionId(collectionId);

        controller.createContext(contextRequestDto, profileId.toString(), token);
    }

    @Test
    public void getCreatedContext() throws Exception {
        ContextEntity contextEntity = createContextEntityMock();

        when(contextService.findCreatedContext(any(UUID.class), any(UUID.class))).thenReturn(contextEntity);

        ResponseEntity<ContextGetResponseDto> response =
                controller.getCreatedContext(UUID.randomUUID(), UUID.randomUUID().toString());

        verify(contextService, times(1)).findCreatedContext(any(UUID.class), any(UUID.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Invalid context id", contextEntity.getContextId(), response.getBody().getContextId());
    }

    @Test
    public void getAssignedContext() throws Exception {
        ContextEntity assignedContextEntity = createContextEntityMock();

        when(contextService.findAssignedContext(any(UUID.class), any(UUID.class), any(String.class)))
                .thenReturn(assignedContextEntity);

        ResponseEntity<ContextGetResponseDto> response =
                controller.getAssignedContext(UUID.randomUUID(), UUID.randomUUID().toString(), "");

        verify(contextService, times(1)).findAssignedContext(any(UUID.class), any(UUID.class), any(String.class));
        assertNotNull("Response is null", response);
        assertNotNull("Body is null", response.getBody());
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Invalid context id", assignedContextEntity.getContextId(), response.getBody().getContextId());
    }

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