package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.ContextPutRequestDto;
import com.quizzes.api.core.dtos.MetadataDto;
import com.quizzes.api.core.dtos.ProfileDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidAssigneeException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.AssignedContextEntity;
import com.quizzes.api.core.model.entities.ContextEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
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

    @Test
    public void createContextForAssessment() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setClassId(classId);
        contextPostRequestDto.setIsCollection(false);
        Context contextResult = createContextMock();
        contextResult.setId(contextId);
        List<UUID> memberIds = new ArrayList<>();
        memberIds.add(UUID.randomUUID());

        doNothing().when(contextService, "validateCollectionOwnerInContext", any(UUID.class), any(UUID.class),
                any(Boolean.class));
        doReturn(contextResult).when(contextService, "createContextObject", any(ContextPostRequestDto.class),
                any(UUID.class));
        doReturn(memberIds).when(classMemberService).getClassMemberIds(any(UUID.class), any(String.class));
        doReturn(contextResult).when(contextRepository).save(any(Context.class));
        doReturn(new ContextProfile()).when(contextProfileService).save(any(ContextProfile.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId, token);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(classMemberService, times(1)).getClassMemberIds(any(UUID.class), anyString());
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));

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
        doReturn(new ContextProfile()).when(contextProfileService).save(any(ContextProfile.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId, token);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(classMemberService, times(0)).getClassMemberIds(any(UUID.class), anyString());
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));

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
        List<UUID> memberIds = new ArrayList<>();
        memberIds.add(UUID.randomUUID());

        doNothing().when(contextService, "validateCollectionOwnerInContext", any(UUID.class), any(UUID.class),
                any(Boolean.class));
        doReturn(contextResult).when(contextService, "createContextObject", any(ContextPostRequestDto.class),
                any(UUID.class));
        doReturn(memberIds).when(classMemberService).getClassMemberIds(any(UUID.class), any(String.class));
        doReturn(contextResult).when(contextRepository).save(any(Context.class));
        doReturn(new ContextProfile()).when(contextProfileService).save(any(ContextProfile.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId, token);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(classMemberService, times(1)).getClassMemberIds(any(UUID.class), anyString());
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));

        assertNotNull("Response is null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result);
    }

    @Test
    public void createContextWithoutClassId() throws Exception {
        CollectionDto collectionDto = createCollectionDto();
        Context context = new Context();
        context.setId(contextId);

        doReturn(collectionDto).when(collectionService).getCollectionOrAssessment(collectionId);
        doReturn(new ContextProfile()).when(contextService, "createContextProfileObject", any(UUID.class),
                any(UUID.class));
        doReturn(context).when(contextRepository).save(any(Context.class));
        doReturn(new ContextProfile()).when(contextProfileService).save(any(ContextProfile.class));

        UUID result = contextService.createContextWithoutClassId(collectionId, profileId);

        verifyPrivate(contextService, times(1)).invoke("createContextProfileObject", any(UUID.class), any(UUID.class));
        verify(collectionService, times(1)).getCollectionOrAssessment(collectionId);
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
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
        doReturn(new ContextProfile()).when(contextProfileService).save(any(ContextProfile.class));

        UUID result = contextService.createContext(contextPostRequestDto, profileId, token);

        verifyPrivate(contextService, times(1))
                .invoke("validateCollectionOwnerInContext", any(UUID.class), any(UUID.class), any(Boolean.class));
        verifyPrivate(contextService, times(1))
                .invoke("createContextObject", any(ContextPostRequestDto.class), any(UUID.class));
        verify(classMemberService, times(0)).getClassMemberIds(any(UUID.class), anyString());
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));

        assertNotNull("Response is null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result);
    }

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

    @Ignore
    @Test
    public void removeIdFromProfileDto() throws Exception {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(UUID.randomUUID().toString());
        profileDto.setFirstName("Keylor");
        profileDto.setLastName("Navas");
        profileDto.setUsername("knavas");

        JsonObject jsonObject = WhiteboxImpl.invokeMethod(contextService, "removeIdFromProfileDto", profileDto);

        assertEquals(jsonObject.size(), 3);
        assertEquals("Wrong first name", "Keylor", jsonObject.get("firstName").getAsString());
        assertEquals("wrong last name", "Navas", jsonObject.get("lastName").getAsString());
        assertEquals("Wrong username", "knavas", jsonObject.get("username").getAsString());
        assertNull(jsonObject.get("id"));

    }

    @Ignore
    @Test
    public void mapContextOwnerEntityToContextAssignedDto() throws Exception {
        /*
        ContextOwnerEntity contextOwnerEntity = createContextOwnerEntityMock();
        ContextGetResponseDto contextAssignedDto =
                WhiteboxImpl.invokeMethod(contextService, "mapContextOwnerEntityToContextAssignedDto",
                        contextOwnerEntity);

        assertEquals("Wrong context id", contextId, contextAssignedDto.getId());
        assertEquals("Wrong collection id", collectionId.toString(), contextAssignedDto.getCollection().getId());
        assertEquals("Wrong owner profile id", ownerProfileId, contextAssignedDto.getOwner().getId());
        assertEquals("Wrong createdDate value", createdAt.getTime(), contextAssignedDto.getCreatedDate());
        assertEquals("Wrong metadata value", 1, contextAssignedDto.getContextData().getMetadata().getStartDate());
        assertTrue("Wrong hasStarted value", contextAssignedDto.getHasStarted());
        */
    }

    @Test
    public void findMappedContext() {
        List<UUID> classMemberIds = new ArrayList<>();
        classMemberIds.add(profileId);
        List<ContextEntity> mappedContexts = new ArrayList<>();
        ContextEntity contextEntity = createContextEntityMock();
        mappedContexts.add(contextEntity);

        doReturn(true).when(classMemberService).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        doReturn(mappedContexts).when(contextRepository).findMappedContexts(any(UUID.class), any(UUID.class), anyMap());

        List<ContextEntity> result = contextService.findMappedContext(UUID.randomUUID(), UUID.randomUUID(),
                new HashMap(), UUID.randomUUID(), "token");

        verify(classMemberService, times(1)).containsMemberId(any(UUID.class), any(UUID.class), anyString());
        verify(contextRepository, times(1)).findMappedContexts(any(UUID.class), any(UUID.class), anyMap());
        assertEquals("Invalid number is results", 1, result.size());
        assertEquals("Invalid Context ID for first element", contextEntity.getContextId(),
                result.get(0).getContextId());
    }

    @Test(expected = InvalidAssigneeException.class)
    public void findMappedContextWhenThrowsInvalidAssigneeException() {
        List<UUID> classMemberIds = new ArrayList<>();
        classMemberIds.add(profileId);

        doReturn(false).when(classMemberService).containsMemberId(any(UUID.class), any(UUID.class), anyString());

        contextService.findMappedContext(UUID.randomUUID(), UUID.randomUUID(), new HashMap(),
                UUID.randomUUID(), "token");
    }

    @Ignore
    @Test
    public void update() {
        ContextDataDto contextDataDto = new ContextDataDto();
        ContextPutRequestDto contextDataMock = new ContextPutRequestDto();
        ContextPutRequestDto.PutRequestMetadataDTO metadata = new ContextPutRequestDto.PutRequestMetadataDTO();

        //Setting metadata
        MetadataDto metadataDto = new MetadataDto();
        metadataDto.setDescription("First Partial");
        metadataDto.setTitle("Math 1st Grade");
        //metadataDto.setDueDate(234234);
        //metadataDto.setStartDate(324234);

        contextDataMock.setContextData(metadata);
        contextDataDto.setMetadata(metadataDto);

        //Setting assignees
        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId(UUID.randomUUID().toString());
        ProfileDto profile2 = new ProfileDto();
        profile2.setId(UUID.randomUUID().toString());
        assignees.add(profile1);
        assignees.add(profile2);
        contextDataMock.setAssignees(assignees);

        Context context = createContextMock();

        //doReturn(context).when(contextService).findByIdAndOwnerId(any(UUID.class), any(UUID.class));

        List<String> externalProfileIdsToFind = new ArrayList<>();
        //we are looking for this 2 profiles in the DB
        externalProfileIdsToFind.add(profile1.getId());
        externalProfileIdsToFind.add(profile2.getId());
        List<String> foundExternalProfileIds = new ArrayList<>();
        //this means only 1 out of 2 assignees exist in this context group
        foundExternalProfileIds.add(profile1.getId());


        List<UUID> profileIds = new ArrayList<>();
        //we know that there are 2 profiles created in the context group, these are the assignee ids
        profileIds.add(UUID.randomUUID());
        profileIds.add(UUID.randomUUID());

        when(contextRepository.save(any(Context.class))).thenReturn(context);


        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(UUID.randomUUID().toString());
        profileDto.setFirstName("Keylor");
        profileDto.setLastName("Navas");
        profileDto.setUsername("knavas");

        Context updatedContext = contextService.update(UUID.randomUUID(), UUID.randomUUID(), contextDataMock);
        context.setContextData("{\"contextMap\":{\"classId\":\"classId\"}}");

        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Context is Null", updatedContext);
        assertEquals("Wrong id for context", context.getId(), updatedContext.getId());
        assertEquals("Wrong id for collection", context.getCollectionId(),
                updatedContext.getCollectionId());
        assertEquals("Wrong context data", context.getContextData(), updatedContext.getContextData());
    }

    /*
    @Ignore
    @Test(expected = ContentNotFoundException.class)
    public void updateThrowsContentNotFoundException() {
        doThrow(ContentNotFoundException.class)
                .when(contextService).findByIdAndOwnerId(any(UUID.class), any(UUID.class));
        contextService.update(UUID.randomUUID(), UUID.randomUUID(), new ContextPutRequestDto());
    }

    @Ignore
    @Test(expected = InvalidOwnerException.class)
    public void updateThrowsInvalidOwnerException() {
        doThrow(InvalidOwnerException.class)
                .when(contextService).findByIdAndOwnerId(any(UUID.class), any(UUID.class));
        contextService.update(UUID.randomUUID(), UUID.randomUUID(), new ContextPutRequestDto());
    }
    */



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
