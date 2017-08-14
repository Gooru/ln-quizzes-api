package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.CollectionMetadataDto;
import com.quizzes.api.core.dtos.MetadataDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.enums.PlayerEventSource;
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
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextService.class)
public class ContextServiceTest {

    @InjectMocks
    private ContextService contextService = spy(new ContextService());

    @Mock
    private ContextRepository contextRepository;

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
    private UUID courseId;
    private UUID unitId;
    private UUID lessonId;
    private PlayerEventSource eventSource;
    private String token;
    private UUID contextProfileId;
    private UUID currentContextProfileId;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        classId = UUID.randomUUID();
        courseId = UUID.randomUUID();
        unitId = UUID.randomUUID();
        lessonId = UUID.randomUUID();
        eventSource = PlayerEventSource.CourseMap;
        profileId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        currentContextProfileId = UUID.randomUUID();
        token = UUID.randomUUID().toString();
        createdAt = new Timestamp(System.currentTimeMillis());
        updatedAt = new Timestamp(System.currentTimeMillis());
    }

    @Ignore
    @Test
    public void createContextByOwnerForExistingContext() throws Exception {
        ContextDataDto contextDataDto = new ContextDataDto();
        contextDataDto.setContextMap(new HashMap<>());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        collectionDto.setOwnerId(profileId);
        ContextEntity contextEntity = createContextEntityMock();

        doReturn(collectionDto).when(collectionService)
                .getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        doReturn(contextEntity).when(contextRepository).findByContextMapKey(anyString());

        UUID result = contextService.createContext(profileId, collectionId, false, classId, contextDataDto, token);

        verify(collectionService, times(1)).getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        verify(contextRepository, times(1)).findByContextMapKey(anyString());

        assertEquals("Wrong Context ID", contextId, result);
    }

    @Ignore
    @Test
    public void createContextByOwnerForNewContext() throws Exception {
        ContextDataDto contextDataDto = new ContextDataDto();
        contextDataDto.setContextMap(new HashMap<>());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        collectionDto.setOwnerId(profileId);
        Context context = new Context();
        context.setId(contextId);

        doReturn(collectionDto).when(collectionService)
                .getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        doReturn(null).when(contextRepository).findByContextMapKey(anyString());
        doReturn(context).when(contextRepository).save(any(Context.class));

        UUID result = contextService.createContext(profileId, collectionId, false, classId, contextDataDto, token);

        verify(collectionService, times(1)).getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        verify(contextRepository, times(1)).findByContextMapKey(anyString());
        verify(contextRepository, times(1)).save(any(Context.class));

        assertEquals("Wrong Context ID", contextId, result);
    }

    @Ignore
    @Test
    public void createContextByAssigneeForExistingContext() throws Exception {
        ContextDataDto contextDataDto = new ContextDataDto();
        contextDataDto.setContextMap(new HashMap<>());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        collectionDto.setOwnerId(UUID.randomUUID());
        ContextEntity contextEntity = createContextEntityMock();

        doReturn(collectionDto).when(collectionService)
                .getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        doReturn(true).when(classMemberService).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        doReturn(true).when(classMemberService).containsOwnerId(any(UUID.class), any(UUID.class), anyString());
        doReturn(contextEntity).when(contextRepository).findByContextMapKey(anyString());

        UUID result = contextService.createContext(profileId, collectionId, false, classId, contextDataDto, token);

        verify(collectionService, times(1)).getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        verify(classMemberService, times(1)).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        verify(classMemberService, times(1)).containsOwnerId(any(UUID.class), any(UUID.class), anyString());
        verify(contextRepository, times(1)).findByContextMapKey(anyString());

        assertEquals("Wrong Context ID", contextId, result);
    }

    @Ignore
    @Test
    public void createContextByAssigneeForNewContext() throws Exception {
        ContextDataDto contextDataDto = new ContextDataDto();
        contextDataDto.setContextMap(new HashMap<>());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        collectionDto.setOwnerId(UUID.randomUUID());
        Context context = new Context();
        context.setId(contextId);

        doReturn(collectionDto).when(collectionService)
                .getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        doReturn(true).when(classMemberService).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        doReturn(true).when(classMemberService).containsOwnerId(any(UUID.class), any(UUID.class), anyString());
        doReturn(null).when(contextRepository).findByContextMapKey(anyString());
        doReturn(context).when(contextRepository).save(any(Context.class));

        UUID result = contextService.createContext(profileId, collectionId, false, classId, contextDataDto, token);

        verify(collectionService, times(1)).getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        verify(classMemberService, times(1)).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        verify(classMemberService, times(1)).containsOwnerId(any(UUID.class), any(UUID.class), anyString());
        verify(contextRepository, times(1)).findByContextMapKey(anyString());
        verify(contextRepository, times(1)).save(any(Context.class));

        assertEquals("Wrong Context ID", contextId, result);
    }

    @Ignore
    @Test(expected = InvalidAssigneeException.class)
    public void createContextByInvalidAssignee() throws Exception {
        ContextDataDto contextDataDto = new ContextDataDto();
        contextDataDto.setContextMap(new HashMap<>());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        collectionDto.setOwnerId(UUID.randomUUID());

        doReturn(collectionDto).when(collectionService)
                .getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        doReturn(false).when(classMemberService).containsMemberId(any(UUID.class), any(UUID.class), anyString());

        contextService.createContext(profileId, collectionId, false, classId, contextDataDto, token);
    }

    @Ignore
    @Test(expected = InvalidOwnerException.class)
    public void createContextByInvalidClassOwner() throws Exception {
        ContextDataDto contextDataDto = new ContextDataDto();
        contextDataDto.setContextMap(new HashMap<>());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        collectionDto.setOwnerId(UUID.randomUUID());

        doReturn(collectionDto).when(collectionService)
                .getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        doReturn(true).when(classMemberService).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        doReturn(false).when(classMemberService).containsOwnerId(any(UUID.class), any(UUID.class), anyString());

        contextService.createContext(profileId, collectionId, false, classId, contextDataDto, token);
    }

    @Test
    public void createContextForSuggestedContent() throws Exception {
        ContextDataDto contextDataDto = new ContextDataDto();
        contextDataDto.setContextMap(new HashMap<>());
        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(collectionId.toString());
        collectionDto.setIsCollection(false);
        collectionDto.setOwnerId(profileId);
        CollectionMetadataDto collectionMetadataDto = new CollectionMetadataDto();
        collectionMetadataDto.setSubFormat("pre-test");
        collectionDto.setMetadata(collectionMetadataDto);
        Context context = createContextMock();
        doReturn(collectionDto).when(collectionService)
                .getCollectionOrAssessment(any(UUID.class), anyBoolean(), anyString());
        doReturn(null).when(contextRepository).findByContextMapKey(anyString());
        doReturn(context).when(contextRepository).save(any(Context.class));
        doReturn(createClassMemberContent()).when(classMemberService).getClassMemberContent(any(UUID.class), anyString());

        UUID result = contextService.createContext(profileId, collectionId, false, classId, contextDataDto, token);

        verify(contextRepository, times(1)).findByContextMapKey(anyString());
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(classMemberService, never()).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        verify(classMemberService, never()).containsOwnerId(any(UUID.class), any(UUID.class), anyString());

        assertEquals("Wrong Context ID", contextId, result);
    }

    @Test
    public void generateContextMapKey() throws Exception {
        Map<String, String> contextMap = new HashMap();
        contextMap.put("courseId", courseId.toString());
        contextMap.put("unitId", unitId.toString());
        contextMap.put("lessonId", lessonId.toString());
        contextMap.put("eventSource", eventSource.getLiteral());

        String mapKeyTemplate = "profileId:%s/collectionId:%s/classId:%s/courseId:%s/unitId:%s/lessonId:%s/eventSource:%s";
        String mapKey = String.format(mapKeyTemplate, profileId, collectionId, classId, courseId, unitId, lessonId,
                eventSource.getLiteral());
        String encodedMapKey = Base64.getEncoder().encodeToString(mapKey.getBytes(StandardCharsets.UTF_8));
        String result = WhiteboxImpl.invokeMethod(contextService, "generateContextMapKey",
                profileId, collectionId, classId, contextMap);

        assertEquals("Wrong ContextMapKey value", encodedMapKey, result);
    }

    @Test
    public void generateContextMapKeyWithEmptyMap() throws Exception {
        Map<String, String> contextMap = new HashMap();

        String mapKeyTemplate = "profileId:%s/collectionId:%s/classId:%s";
        String mapKey = String.format(mapKeyTemplate, profileId, collectionId, classId);
        String encodedMapKey = Base64.getEncoder().encodeToString(mapKey.getBytes(StandardCharsets.UTF_8));
        String result = WhiteboxImpl.invokeMethod(contextService, "generateContextMapKey",
                profileId, collectionId, classId, contextMap);

        assertEquals("Wrong ContextMapKey value", encodedMapKey, result);
    }

    @Test
    public void generateContextMapKeyWithoutClass() throws Exception {
        Map<String, String> contextMap = new HashMap();

        String mapKeyTemplate = "profileId:%s/collectionId:%s";
        String mapKey = String.format(mapKeyTemplate, profileId, collectionId, null);
        String encodedMapKey = Base64.getEncoder().encodeToString(mapKey.getBytes(StandardCharsets.UTF_8));
        String result = WhiteboxImpl.invokeMethod(contextService, "generateContextMapKey",
                profileId, collectionId, null, contextMap);

        assertEquals("Wrong ContextMapKey value", encodedMapKey, result);
    }

    @Test
    public void findById() {
        ContextEntity contextResult = createContextEntityMock();
        when(contextRepository.findById(any(UUID.class))).thenReturn(contextResult);

        ContextEntity result = contextService.findById(UUID.randomUUID());

        verify(contextRepository, times(1)).findById(any(UUID.class));
        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextId, result.getContextId());
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
        ContextEntity assignedContextEntity = createContextEntityMock();
        when(contextRepository.findById(any(UUID.class))).thenReturn(assignedContextEntity);
        doReturn(true).when(classMemberService).containsMemberId(any(UUID.class), any(UUID.class), anyString());

        ContextEntity result = contextService.findAssignedContext(contextId, profileId, token);

        verify(contextRepository, times(1)).findById(any(UUID.class));
        assertEquals("Wrong context id", assignedContextEntity.getContextId(), result.getContextId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findAssignedContextWhenThrowsContentNotFoundException() throws Exception {
        when(contextRepository.findById(any(UUID.class))).thenReturn(null);
        contextService.findAssignedContext(contextId, profileId, token);
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

    private ClassMemberContentDto createClassMemberContent() {
        ClassMemberContentDto classMemberContent = new ClassMemberContentDto();
        classMemberContent.setMemberIds(new ArrayList<>());
        classMemberContent.setOwnerIds(new ArrayList<>());
        classMemberContent.getOwnerIds().add(profileId);
        return classMemberContent;
    }


}
