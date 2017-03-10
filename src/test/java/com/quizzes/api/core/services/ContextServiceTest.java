package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.MetadataDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.services.content.ClassMemberService;
import com.quizzes.api.core.services.content.CollectionService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyMap;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextService.class)
public class ContextServiceTest {

    @InjectMocks
    private ContextService contextService = spy(new ContextService());

    @Mock
    private ContextRepository contextRepository;

    @Mock
    private ContextProfileService contextProfileService;

    @Mock
    private ClassMemberService classMemberService;

    @Mock
    private CollectionService collectionService;

    @Mock
    private Gson gson = new Gson();

    private UUID contextId;
    private UUID collectionId;
    private UUID profileId;
    private UUID classId;
    private UUID unitId;
    private UUID memberId;
    private String token;
    private UUID contextProfileId;
    private UUID currentContextProfileId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        unitId = UUID.randomUUID();
        classId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        currentContextProfileId = UUID.randomUUID();
        token = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    /*
    @Test
    public void createContextForAssessment() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setClassId(classId);
        contextPostRequestDto.setIsCollection(false);
        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        doNothing().when(contextService, "validateCollectionOwnerInContext", any(UUID.class), any(UUID.class),
                any(Boolean.class));
        doReturn(contextResult).when(contextService, "createContextObject", any(ContextPostRequestDto.class),
                any(UUID.class));
        doReturn(contextResult).when(contextRepository).save(any(Context.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result);
    }

    @Test
    public void createContextWithoutClassIdForAssessment() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setIsCollection(false);
        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        doNothing().when(contextService, "validateCollectionOwnerInContext", any(UUID.class), any(UUID.class),
                any(Boolean.class));
        doReturn(contextResult).when(contextService, "createContextObject", any(ContextPostRequestDto.class),
                any(UUID.class));
        doReturn(contextResult).when(contextRepository).save(any(Context.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(classMemberService, times(0)).getClassMemberIds(any(UUID.class), anyString());
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result);
    }

    @Test
    public void createContextForCollection() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setClassId(classId);
        contextPostRequestDto.setIsCollection(true);
        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        doNothing().when(contextService, "validateCollectionOwnerInContext", any(UUID.class), any(UUID.class),
                any(Boolean.class));
        doReturn(contextResult).when(contextService, "createContextObject", any(ContextPostRequestDto.class),
                any(UUID.class));
        doReturn(contextResult).when(contextRepository).save(any(Context.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result);
    }

    @Test
    public void createContextWithoutClassId() throws Exception {
        CollectionDto collectionDto = createCollectionDto();
        Context context = new Context();
        context.setId(contextId);

        doReturn(collectionDto).when(collectionService).getCollectionOrAssessment(collectionId);
        doReturn(context).when(contextRepository).save(any(Context.class));

        UUID result = contextService.createContextWithoutClassId(collectionId, profileId);

        verify(collectionService, times(1)).getCollectionOrAssessment(collectionId);
        verify(contextRepository, times(1)).save(any(Context.class));
        assertEquals("Wrong id for context", contextId, result);
    }

    @Test
    public void createContextWithoutClassIdForCollection() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setIsCollection(true);
        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        doNothing().when(contextService, "validateCollectionOwnerInContext", any(UUID.class), any(UUID.class),
                any(Boolean.class));
        doReturn(contextResult).when(contextService, "createContextObject", any(ContextPostRequestDto.class),
                any(UUID.class));
        doReturn(contextResult).when(contextRepository).save(any(Context.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(classMemberService, times(0)).getClassMemberIds(any(UUID.class), anyString());
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result);
    }
    */

    /*
    @Test
    public void validateCollectionOwnerInContextForAssessment() throws Exception {
        CollectionDto assessmentDto = createAssessmentDto();
        doReturn(assessmentDto).when(collectionService).getAssessment(any(UUID.class));
        WhiteboxImpl.invokeMethod(contextService, "validateCollectionOwnerInContext", profileId, collectionId, false);
        verify(collectionService, times(1)).getAssessment(any(UUID.class));
    }

    @Test
    public void validateCollectionOwnerInContextForCollection() throws Exception {
        CollectionDto collectionDto = createCollectionDto();
        doReturn(collectionDto).when(collectionService).getCollection(any(UUID.class));
        WhiteboxImpl.invokeMethod(contextService, "validateCollectionOwnerInContext", profileId, collectionId, true);
        verify(collectionService, times(1)).getCollection(any(UUID.class));
    }

    @Test(expected = InvalidOwnerException.class)
    public void validateCollectionOwnerInContextThrowsException() throws Exception {
        CollectionDto collectionDto = createCollectionDto();
        doReturn(collectionDto).when(collectionService).getCollection(any(UUID.class));
        WhiteboxImpl.invokeMethod(contextService, "validateCollectionOwnerInContext", UUID.randomUUID(), collectionId,
                true);
    }
    */

    @Test
    public void findById() {
        Context contextResult = new Context();
        contextResult.setId(contextId);
        contextResult.setCollectionId(collectionId);
        contextResult.setContextData("{\"context\":\"value\"}");
        contextResult.setIsDeleted(false);
        when(contextRepository.findById(any(UUID.class))).thenReturn(contextResult);

        Context result = contextService.findById(UUID.randomUUID());

        verify(contextRepository, times(1)).findById(any(UUID.class));
        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextId, result.getId());
        assertEquals("Wrong id for collection", collectionId, result.getCollectionId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findByIdThrowException() {
        when(contextRepository.findById(contextId)).thenReturn(null);
        contextService.findById(contextId);
    }

    @Test
    public void findCreatedContexts() throws Exception {
        List<ContextEntity> contextEntities = new ArrayList<>();
        contextEntities.add(createContextEntityMock());

        when(contextRepository.findCreatedContextsByProfileId(any(UUID.class))).thenReturn(contextEntities);

        List<ContextEntity> result = contextService.findCreatedContexts(profileId);

        verify(contextRepository, times(1)).findCreatedContextsByProfileId(any(UUID.class));

        assertEquals("Wrong size", 1, result.size());
        assertEquals("Wrong context id", contextEntities.get(0).getContextId(), result.get(0).getContextId());
    }

    @Test
    public void findCreatedContext() throws Exception {
        ContextEntity contextEntity = createContextEntityMock();

        when(contextRepository.findCreatedContextByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(contextEntity);

        ContextEntity result = contextService.findCreatedContext(contextId, profileId);

        verify(contextRepository, times(1))
                .findCreatedContextByContextIdAndProfileId(any(UUID.class), any(UUID.class));

        assertEquals("Wrong context id", contextEntity.getContextId(), result.getContextId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findCreatedContextWhenThrowsContentNotFoundException() throws Exception {
        when(contextRepository.findCreatedContextByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);
        contextService.findCreatedContext(contextId, profileId);
    }

    @Test
    public void findAssignedContexts() throws Exception {
        List<AssignedContextEntity> assignedContextEntities = new ArrayList<>();
        assignedContextEntities.add(createAssignedContextEntityMock());

        when(contextRepository.findAssignedContextsByProfileId(any(UUID.class))).thenReturn(assignedContextEntities);

        List<AssignedContextEntity> result = contextService.findAssignedContexts(profileId);

        verify(contextRepository, times(1)).findAssignedContextsByProfileId(any(UUID.class));

        assertEquals("Wrong size", 1, result.size());
        assertEquals("Wrong context id", assignedContextEntities.get(0).getContextId(), result.get(0).getContextId());
    }

    @Test
    public void findAssignedContext() throws Exception {
        AssignedContextEntity assignedContextEntity = createAssignedContextEntityMock();

        when(contextRepository.findAssignedContextByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(assignedContextEntity);

        AssignedContextEntity result = contextService.findAssignedContext(contextId, profileId);

        verify(contextRepository, times(1))
                .findAssignedContextByContextIdAndProfileId(any(UUID.class), any(UUID.class));

        assertEquals("Wrong context id", assignedContextEntity.getContextId(), result.getContextId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findAssignedContextWhenThrowsContentNotFoundException() throws Exception {
        when(contextRepository.findAssignedContextByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);
        contextService.findAssignedContext(contextId, profileId);
    }

    private Context createContextMock() {
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);
        context.setContextData(gson.toJson(createContextDataDto()));
        context.setCreatedAt(createdAt);
        context.setProfileId(profileId);
        return context;
    }

    private MetadataDto createMetadataDto() {
        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setTitle("title");
        metadataDto.setDescription("description");
        return metadataDto;
    }

    private ContextDataDto createContextDataDto() {
        ContextDataDto contextDataDto = new ContextDataDto();

        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("unitId", unitId.toString());

        contextDataDto.setContextMap(contextMap);
        contextDataDto.setMetadata(createMetadataDto());

        return contextDataDto;
    }

    private ClassMemberContentDto createClassMember() {
        ClassMemberContentDto classMember = new ClassMemberContentDto();
        classMember.setMemberIds(new ArrayList<>(Arrays.asList(memberId)));
        return classMember;
    }

    /*
    private ContextOwnerEntity createContextOwnerEntityMock() {
        ContextOwnerEntity contextOwnerEntity = mock(ContextOwnerEntity.class);
        String contextData =
                "{" +
                        "  'contextMap': {" +
                        "    'classId': 'class-id-1'" +
                        "  }," +
                        "  'metadata': {" +
                        "    'title': 'metadata title'," +
                        "    'description': 'metadata description'," +
                        "    'startDate': 1," +
                        "    'dueDate': 2" +
                        "  }" +
                        "}";
        when(contextOwnerEntity.getContextId()).thenReturn(contextId);
        when(contextOwnerEntity.getCollectionId()).thenReturn(collectionId);
        when(contextOwnerEntity.getContextData()).thenReturn(contextData);
        when(contextOwnerEntity.getCreatedAt()).thenReturn(createdAt);
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(ownerProfileId);
        when(contextOwnerEntity.getContextProfileId()).thenReturn(contextProfileId);
        return contextOwnerEntity;
    }
    */

    private ContextPostRequestDto createContextPostRequestDto() {
        ContextPostRequestDto contextPostRequestDto = new ContextPostRequestDto();
        contextPostRequestDto.setCollectionId(collectionId);
        contextPostRequestDto.setContextData(createContextDataDto());
        return contextPostRequestDto;
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
        Mockito.when(contextEntity.getContextId()).thenReturn(contextId);
        Mockito.when(contextEntity.getCollectionId()).thenReturn(collectionId);
        Mockito.when(contextEntity.getClassId()).thenReturn(classId);
        Mockito.when(contextEntity.getProfileId()).thenReturn(profileId);
        Mockito.when(contextEntity.getContextData()).thenReturn(contextData);
        Mockito.when(contextEntity.getCreatedAt()).thenReturn(createdAt);
        Mockito.when(contextEntity.getUpdatedAt()).thenReturn(updatedAt);
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
        Mockito.when(contextEntity.getContextProfileId()).thenReturn(contextProfileId);
        Mockito.when(contextEntity.getCurrentContextProfileId()).thenReturn(currentContextProfileId);
        Mockito.when(contextEntity.getContextId()).thenReturn(contextId);
        Mockito.when(contextEntity.getCollectionId()).thenReturn(collectionId);
        Mockito.when(contextEntity.getClassId()).thenReturn(classId);
        Mockito.when(contextEntity.getProfileId()).thenReturn(profileId);
        Mockito.when(contextEntity.getContextData()).thenReturn(contextData);
        Mockito.when(contextEntity.getCreatedAt()).thenReturn(createdAt);
        Mockito.when(contextEntity.getUpdatedAt()).thenReturn(updatedAt);
        return contextEntity;
    }

    private CollectionDto createCollectionDto() {
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setIsCollection(true);
        collectionDto.setId(collectionId.toString());
        collectionDto.setOwnerId(profileId);
        return collectionDto;
    }

    private CollectionDto createAssessmentDto() {
        CollectionDto assessmentDto = createCollectionDto();
        assessmentDto.setIsCollection(false);
        return assessmentDto;
    }

}
