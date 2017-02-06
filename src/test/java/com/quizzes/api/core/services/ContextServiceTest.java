package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.core.dtos.ClassMemberContentDto;
import com.quizzes.api.core.dtos.ContextGetResponseDto;
import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.ContextPutRequestDto;
import com.quizzes.api.core.dtos.EventSummaryDataDto;
import com.quizzes.api.core.dtos.IdResponseDto;
import com.quizzes.api.core.dtos.MetadataDto;
import com.quizzes.api.core.dtos.ProfileDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.dtos.content.CollectionContentDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.ContextAssigneeEntity;
import com.quizzes.api.core.model.entities.ContextOwnerEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.core.repositories.ContextRepository;
import com.quizzes.api.core.rest.clients.AssessmentRestClient;
import com.quizzes.api.core.rest.clients.ClassMemberRestClient;
import com.quizzes.api.core.rest.clients.CollectionRestClient;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
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
    private ContextProfileEventService contextProfileEventService;

    @Mock
    private ContextProfileService contextProfileService;

    @Mock
    private ClassMemberRestClient classMemberRestClient;

    @Mock
    private CollectionRestClient collectionRestClient;

    @Mock
    private AssessmentRestClient assessmentRestClient;

    @Mock
    private Gson gson = new Gson();

    private UUID contextId;
    private UUID collectionId;
    private UUID profileId;
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
        profileId = UUID.randomUUID();
        memberId = UUID.randomUUID();
        token = UUID.randomUUID().toString();
    }

    @Test
    public void createContextForAssessment() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setClassId(classId);
        contextPostRequestDto.setIsCollection(false);

        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        ClassMemberContentDto classMember = createClassMember();

        doNothing().when(contextService, "validateCollectionOwner", collectionId, false, profileId, token);
        doReturn(createContextMock()).when(contextService, "createContextObject", contextPostRequestDto, profileId);
        doNothing().when(contextService, "createContextProfiles", classMember.getMemberIds(), contextId);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(classMemberRestClient.getClassMembers(classId.toString(), token)).thenReturn(classMember);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, profileId, token);

        verify(contextRepository, times(1)).save(any(Context.class));
        verify(classMemberRestClient, times(1)).getClassMembers(classId.toString(), token);
        verifyPrivate(contextService, times(1)).invoke("createContextObject", contextPostRequestDto, profileId);
        verifyPrivate(contextService, times(1)).invoke("createContextProfiles", classMember.getMemberIds(), contextId);
        verifyPrivate(contextService, times(1)).invoke("validateCollectionOwner",
                collectionId, false, profileId, token);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
    }

    @Test
    public void createContextWithoutClassIdForAssessment() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setIsCollection(false);

        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        ClassMemberContentDto classMember = createClassMember();

        doReturn(createContextMock()).when(contextService, "createContextObject", contextPostRequestDto, profileId);
        doNothing().when(contextService, "validateCollectionOwner", collectionId, false, profileId, token);
        doNothing().when(contextService, "createContextProfiles", any(), any());
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(classMemberRestClient.getClassMembers(any(), any())).thenReturn(classMember);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, profileId, token);

        verify(contextRepository, times(1)).save(any(Context.class));
        verify(classMemberRestClient, times(0)).getClassMembers(any(), any());
        verifyPrivate(contextService, times(1)).invoke("createContextObject", contextPostRequestDto, profileId);
        verifyPrivate(contextService, times(0)).invoke("createContextProfiles", any(), any());
        verifyPrivate(contextService, times(1)).invoke("validateCollectionOwner",
                collectionId, false, profileId, token);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
    }

    @Test
    public void createContextForCollection() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setClassId(classId);
        contextPostRequestDto.setIsCollection(true);

        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        ClassMemberContentDto classMember = createClassMember();

        doNothing().when(contextService, "validateCollectionOwner", collectionId, true, profileId, token);
        doReturn(createContextMock()).when(contextService, "createContextObject", contextPostRequestDto, profileId);
        doNothing().when(contextService, "createContextProfiles", classMember.getMemberIds(), contextId);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(classMemberRestClient.getClassMembers(classId.toString(), token)).thenReturn(classMember);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, profileId, token);

        verify(contextRepository, times(1)).save(any(Context.class));
        verify(classMemberRestClient, times(1)).getClassMembers(classId.toString(), token);
        verifyPrivate(contextService, times(1)).invoke("createContextObject", contextPostRequestDto, profileId);
        verifyPrivate(contextService, times(1)).invoke("createContextProfiles", classMember.getMemberIds(), contextId);
        verifyPrivate(contextService, times(1)).invoke("validateCollectionOwner",
                collectionId, true, profileId, token);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
    }

    @Test
    public void createContextWithoutClassIdForCollection() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setIsCollection(true);

        Context contextResult = createContextMock();
        contextResult.setId(contextId);

        ClassMemberContentDto classMember = createClassMember();

        doReturn(createContextMock()).when(contextService, "createContextObject", contextPostRequestDto, profileId);
        doNothing().when(contextService, "validateCollectionOwner", collectionId, true, profileId, token);
        doNothing().when(contextService, "createContextProfiles", any(), any());
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(classMemberRestClient.getClassMembers(any(), any())).thenReturn(classMember);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, profileId, token);

        verify(contextRepository, times(1)).save(any(Context.class));
        verify(classMemberRestClient, times(0)).getClassMembers(any(), any());
        verifyPrivate(contextService, times(1)).invoke("createContextObject", contextPostRequestDto, profileId);
        verifyPrivate(contextService, times(0)).invoke("createContextProfiles", any(), any());
        verifyPrivate(contextService, times(1)).invoke("validateCollectionOwner",
                collectionId, true, profileId, token);

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
    }

    @Test
    public void getCollectionOwnerIdForCollection() throws Exception {
        CollectionContentDto collectionContentDto = new CollectionContentDto();
        collectionContentDto.setId(collectionId.toString());
        collectionContentDto.setOwnerId(profileId);

        when(collectionRestClient.getCollection(collectionId.toString(), token)).thenReturn(collectionContentDto);

        UUID result = WhiteboxImpl.invokeMethod(contextService, "getCollectionOwnerId", collectionId.toString(),
                true, token);

        verify(collectionRestClient, times(1)).getCollection(collectionId.toString(), token);
        assertEquals("Wrong ownerId", profileId, result);
    }

    @Test
    public void getCollectionOwnerIdForAssessment() throws Exception {
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();
        assessmentContentDto.setId(collectionId.toString());
        assessmentContentDto.setOwnerId(profileId);

        when(assessmentRestClient.getAssessment(collectionId.toString(), token)).thenReturn(assessmentContentDto);

        UUID result = WhiteboxImpl.invokeMethod(contextService, "getCollectionOwnerId", collectionId.toString(),
                false, token);

        verify(assessmentRestClient, times(1)).getAssessment(collectionId.toString(), token);
        assertEquals("Wrong ownerId", profileId, result);
    }

    @Test
    public void validateCollectionOwner() throws Exception {
        CollectionContentDto collectionContentDto = new CollectionContentDto();
        collectionContentDto.setId(collectionId.toString());
        collectionContentDto.setOwnerId(profileId);

        doReturn(profileId).when(contextService, "getCollectionOwnerId", collectionId.toString(), true, token);

        WhiteboxImpl.invokeMethod(contextService, "validateCollectionOwner", collectionId, true, profileId, token);

        verifyPrivate(contextService, times(1)).invoke("getCollectionOwnerId", collectionId.toString(), true, token);
    }

    @Test(expected = InvalidOwnerException.class)
    public void validateCollectionOwnerThrowsException() throws Exception {
        UUID anotherOwnerID = UUID.randomUUID();
        CollectionContentDto collectionContentDto = new CollectionContentDto();
        collectionContentDto.setId(collectionId.toString());
        collectionContentDto.setOwnerId(anotherOwnerID);

        doReturn(profileId).when(contextService, "getCollectionOwnerId", collectionId.toString(), false, token);

        WhiteboxImpl.invokeMethod(contextService, "validateCollectionOwner", collectionId, false,
                anotherOwnerID, token);

        verifyPrivate(contextService, times(1)).invoke("getCollectionOwnerId", collectionId.toString(), false, token);
    }

    @Test
    public void createContextProfile() throws Exception {
        ContextProfile result =
                WhiteboxImpl.invokeMethod(contextService, "createContextProfile", contextId, profileId);

        assertNull("contextId is not null", result.getId());
        assertNull("Current resource is not null", result.getCurrentResourceId());
        assertEquals("Wrong profileId", profileId, result.getProfileId());
        assertEquals("Wrong classId", contextId, result.getContextId());
        assertFalse("isComplete is true", result.getIsComplete());
        assertNotNull("Summary Data is null", result.getEventSummaryData());

        EventSummaryDataDto eventResult = gson.fromJson(result.getEventSummaryData(), EventSummaryDataDto.class);
        assertEquals("Wrong total time", 0, eventResult.getTotalTimeSpent());
        assertEquals("Wrong average reaction", 0, eventResult.getAverageReaction());
        assertEquals("Wrong average score", 0, eventResult.getAverageScore());
        assertEquals("Wrong total answered", 0, eventResult.getTotalAnswered());
        assertEquals("Wrong total correct", 0, eventResult.getTotalCorrect());
    }

    @Test
    public void createContextProfiles() throws Exception {
        List<UUID> memberIds = new ArrayList<>();
        memberIds.add(profileId);
        memberIds.add(memberId);
        doReturn(new ContextProfile()).when(contextService, "createContextProfile", eq(contextId), any(UUID.class));
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(new ContextProfile());

        WhiteboxImpl.invokeMethod(contextService, "createContextProfiles", memberIds, contextId);

        verify(contextProfileService, times(2)).save(any(ContextProfile.class));
        verifyPrivate(contextService, times(1)).invoke("createContextProfile", contextId, profileId);
        verifyPrivate(contextService, times(1)).invoke("createContextProfile", contextId, memberId);
    }

    @Test
    public void createContextProfilesMembersNullAndEmpty() throws Exception {
        List<UUID> memberIds = null;
        doReturn(new ContextProfile()).when(contextService, "createContextProfile", any(), any());

        WhiteboxImpl.invokeMethod(contextService, "createContextProfiles", memberIds, profileId);
        verifyPrivate(contextService, times(0)).invoke("createContextProfile", any(), any());

        memberIds = new ArrayList<>();
        WhiteboxImpl.invokeMethod(contextService, "createContextProfiles", memberIds, profileId);
        verifyPrivate(contextService, times(0)).invoke("createContextProfile", any(), any());
    }

    @Test
    public void createContextObject() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setClassId(classId);
        contextPostRequestDto.setIsCollection(true);

        Context result =
                WhiteboxImpl.invokeMethod(contextService, "createContextObject", contextPostRequestDto, profileId);

        assertNull("contextId is not null", result.getId());
        assertEquals("Wrong profileId", profileId, result.getProfileId());
        assertEquals("Wrong classId", classId, result.getClassId());
        assertEquals("Wrong contextData", gson.toJson(createContextDataDto()), result.getContextData());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertTrue("isCollection is false", result.getIsCollection());
    }

    @Test
    public void createContextObjectClassNull() throws Exception {
        ContextPostRequestDto contextPostRequestDto = createContextPostRequestDto();
        contextPostRequestDto.setIsCollection(false);

        Context result =
                WhiteboxImpl.invokeMethod(contextService, "createContextObject", contextPostRequestDto, profileId);

        assertNull("contextId is not null", result.getId());
        assertEquals("Wrong profileId", profileId, result.getProfileId());
        assertNull("classId is not null", result.getClassId());
        assertEquals("Wrong contextData", gson.toJson(createContextDataDto()), result.getContextData());
        assertEquals("Wrong collectionId", collectionId, result.getCollectionId());
        assertFalse("isCollection is true", result.getIsCollection());
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
    public void findByIdAndOwnerId() {
        ContextOwnerEntity contextOwnerEntityMock = createContextOwnerEntityMock();
        when(contextRepository.findContextOwnerById(any(UUID.class))).thenReturn(contextOwnerEntityMock);

        Context context = contextService.findByIdAndOwnerId(UUID.randomUUID(),
                contextOwnerEntityMock.getOwnerProfileId());

        verify(contextRepository, times(1)).findContextOwnerById(any(UUID.class));
        assertNotNull("Context is null", context);
        assertEquals("Wrong id for context", contextOwnerEntityMock.getId(), context.getId());
        assertEquals("Wrong id for collection", contextOwnerEntityMock.getCollectionId(), context.getCollectionId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findByIdAndOwnerIdThrowsContentNotFoundException() {
        when(contextRepository.findContextOwnerById(any(UUID.class))).thenReturn(null);
        contextService.findByIdAndOwnerId(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test(expected = InvalidOwnerException.class)
    public void findByIdAndOwnerIdThrowsInvalidOwnerException() {
        ContextOwnerEntity contextOwnerEntityMock = createContextOwnerEntityMock();
        when(contextRepository.findContextOwnerById(any(UUID.class))).thenReturn(contextOwnerEntityMock);
        contextService.findByIdAndOwnerId(UUID.randomUUID(), UUID.randomUUID());
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

        doReturn(context).when(contextService).findByIdAndOwnerId(any(UUID.class), any(UUID.class));

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

    @Ignore
    @Test
    public void getAssignedContexts() throws Exception {
        ContextOwnerEntity contextOwnerEntity = createContextOwnerEntityMock();

        /*
        List<ContextOwnerEntity> list = new ArrayList<>();
        list.add(contextOwnerEntity);

        when(contextRepository.findContextOwnerByAssigneeIdAndFilters(any(UUID.class), any(Boolean.class), any(Long.class), any(Long.class))).thenReturn(list);

        List<ContextGetResponseDto> result = contextService.getAssignedContexts(UUID.randomUUID(), null, null, null);

        verify(contextRepository, times(1)).findContextOwnerByAssigneeIdAndFilters(any(UUID.class), any(Boolean.class), any(Long.class), any(Long.class));

        ContextGetResponseDto resultEntity = result.get(0);
        assertEquals("Wrong size", 1, result.size());

        assertEquals("Wrong context id", contextId, resultEntity.getId());
        assertEquals("Wrong collection id", collectionId.toString(), resultEntity.getCollection().getId());
        assertEquals("Wrong owner profile id", ownerProfileId, resultEntity.getOwner().getId());
        assertEquals("Wrong hasStarted value", true, resultEntity.getHasStarted());
        assertEquals("Wrong created date value", createdAt.getTime(), resultEntity.getCreatedDate());

        assertEquals("Wrong class id", "class-id-1", resultEntity.getContextData().getContextMap().get("classId"));

        MetadataDto metadataResult = resultEntity.getContextData().getMetadata();
        assertEquals("Wrong metadata title", "metadata title", metadataResult.getTitle());
        assertEquals("Wrong metadata description", "metadata description", metadataResult.getDescription());
        assertEquals("Wrong metadata start date", 1, metadataResult.getStartDate());
        */
    }

    @Ignore
    @Test
    public void getAssignedContextByContextIdAndAssigneeId() throws Exception {
        ContextOwnerEntity contextOwnerEntity = createContextOwnerEntityMock();

        /*
        when(contextRepository.findContextOwnerByContextIdAndAssigneeId(any(UUID.class), any(UUID.class)))
                .thenReturn(contextOwnerEntity);

        ContextGetResponseDto resultEntity =
                contextService.getAssignedContextByContextIdAndAssigneeId(UUID.randomUUID(), UUID.randomUUID());

        verify(contextRepository, times(1)).findContextOwnerByContextIdAndAssigneeId(any(UUID.class), any(UUID.class));

        assertEquals("Wrong context id", contextId, resultEntity.getId());
        assertEquals("Wrong collection id", collectionId.toString(), resultEntity.getCollection().getId());
        assertEquals("Wrong owner profile id", ownerProfileId, resultEntity.getOwner().getId());
        assertEquals("Wrong hasStarted value", true, resultEntity.getHasStarted());
        assertEquals("Wrong created date value", createdAt.getTime(), resultEntity.getCreatedDate());

        assertEquals("Wrong class id", "class-id-1", resultEntity.getContextData().getContextMap().get("classId"));

        MetadataDto metadataResult = resultEntity.getContextData().getMetadata();
        assertEquals("Wrong metadata title", "metadata title", metadataResult.getTitle());
        assertEquals("Wrong metadata description", "metadata description", metadataResult.getDescription());
        assertEquals("Wrong metadata start date", 1, metadataResult.getStartDate());
        */
    }

    @Test(expected = ContentNotFoundException.class)
    public void getAssignedContextByContextIdAndAssigneeIdThrowsContentNotFoundException() {
        when(contextRepository.findContextOwnerByContextIdAndAssigneeId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);
        contextService.getAssignedContextByContextIdAndAssigneeId(UUID.randomUUID(), UUID.randomUUID());
    }

    @Ignore
    @Test
    public void findCreatedContexts() {
        Map<UUID, List<ContextAssigneeEntity>> contextsMap = new HashMap<>();

        ContextAssigneeEntity row1 = mock(ContextAssigneeEntity.class);
        when(row1.getId()).thenReturn(contextId);
        when(row1.getCollectionId()).thenReturn(UUID.randomUUID());
        when(row1.getAssigneeProfileId()).thenReturn(UUID.randomUUID());
        when(row1.getContextData()).thenReturn("{\"metadata\": {\"description\": \"First Partial\"," +
                "\"title\": \"Math 1st Grade\"}, \"contextMap\": {" +
                "\"classId\": \"155c951b-c2c9-435a-815d-81e455e681f0\"}}");
        when(row1.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
        List<ContextAssigneeEntity> contextAssigneeEntityList1 = new ArrayList<>();
        contextAssigneeEntityList1.add(row1);
        contextsMap.put(contextId, contextAssigneeEntityList1);

        UUID groupId = UUID.randomUUID();
        contextId = UUID.randomUUID();
        ContextAssigneeEntity row2 = mock(ContextAssigneeEntity.class);
        when(row2.getId()).thenReturn(contextId);
        when(row2.getCollectionId()).thenReturn(UUID.randomUUID());
        when(row2.getAssigneeProfileId()).thenReturn(UUID.randomUUID());
        when(row2.getContextData()).thenReturn("{\"metadata\": {\"description\": \"First Partial\"," +
                "\"title\": \"Math 2nd Grade\"}, \"contextMap\": {" +
                "\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");
        when(row2.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
        List<ContextAssigneeEntity> contextAssigneeEntityList2 = new ArrayList<>();
        contextAssigneeEntityList2.add(row2);

        ContextAssigneeEntity row3 = mock(ContextAssigneeEntity.class);
        when(row3.getId()).thenReturn(contextId);
        when(row3.getCollectionId()).thenReturn(UUID.randomUUID());
        when(row3.getAssigneeProfileId()).thenReturn(UUID.randomUUID());
        when(row3.getContextData()).thenReturn("{\"metadata\": {\"description\": \"First Partial\"," +
                "\"title\": \"Math 2nd Grade\"}, \"contextMap\": {" +
                "\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");
        when(row3.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
        contextAssigneeEntityList2.add(row3);
        contextsMap.put(contextId, contextAssigneeEntityList2);

        when(contextRepository.findContextAssigneeByOwnerId(any(UUID.class))).thenReturn(contextsMap);

        List<ContextGetResponseDto> result = contextService.findCreatedContexts(UUID.randomUUID());
        verify(contextRepository, times(1)).findContextAssigneeByOwnerId(any(UUID.class));

        assertNotNull("Created contexts list in null", result);
        assertEquals("Created contexts doesn't match", 2, result.size());
        //assertNotNull("Context has no Collection", result.get(0).getCollection());
        //assertNotNull("Context has no assignees", result.get(0).getAssignees());
    }

    @Ignore
    @Test
    public void findCreatedContextByContextIdAndOwnerId() {
        Map<UUID, List<ContextAssigneeEntity>> contextsMap = new HashMap<>();
        UUID ownerId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();

        ContextAssigneeEntity contextAssigneeEntity = mock(ContextAssigneeEntity.class);
        when(contextAssigneeEntity.getId()).thenReturn(contextId);
        when(contextAssigneeEntity.getCollectionId()).thenReturn(UUID.randomUUID());
        when(contextAssigneeEntity.getAssigneeProfileId()).thenReturn(UUID.randomUUID());
        when(contextAssigneeEntity.getContextData()).thenReturn("{\"metadata\": {\"description\": \"First Partial\"," +
                "\"title\": \"Math 1st Grade\"}, \"contextMap\": {" +
                "\"classId\": \"" + classId + "\"}}");
        when(contextAssigneeEntity.getCreatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));
        when(contextAssigneeEntity.getUpdatedAt()).thenReturn(new Timestamp(System.currentTimeMillis()));

        List<ContextAssigneeEntity> contextAssigneeEntityList = new ArrayList<>();
        contextAssigneeEntityList.add(contextAssigneeEntity);
        contextsMap.put(contextId, contextAssigneeEntityList);

        when(contextRepository.findContextAssigneeByContextIdAndOwnerId(contextId, ownerId))
                .thenReturn(contextsMap);

        //ContextGetResponseDto result =
        //        contextService.findCreatedContextByContextIdAndOwnerId(contextId, ownerId);
        verify(contextRepository, times(1)).findContextAssigneeByContextIdAndOwnerId(contextId, ownerId);

        //assertNotNull("Context is null", result);
        //assertNotNull("Wrong collection Id", result.getCollection().getId());
        //assertNotNull("Is is null", result.getId());
        //assertEquals("Context has no assignees", 1, result.getAssignees().size());

        //Map<String, String> contextDataResult = result.getContextData().getContextMap();
        //assertEquals("Wrong context data value", classId.toString(), contextDataResult.get("classId"));

        //MetadataDto metadataResult = result.getContextData().getMetadata();
        //assertEquals("Context has no assignees", "First Partial", metadataResult.getDescription());
        //assertEquals("Context has no assignees", "Math 1st Grade", metadataResult.getTitle());
    }

    @Ignore
    @Test(expected = ContentNotFoundException.class)
    public void findCreatedContextByContextIdAndOwnerIdNoContentFoundResponse() {
        UUID ownerId = UUID.randomUUID();

        when(contextRepository.findContextAssigneeByContextIdAndOwnerId(contextId, ownerId))
                .thenReturn(new HashMap());

        //contextService.findCreatedContextByContextIdAndOwnerId(contextId, ownerId);
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
        ContextOwnerEntity contextOwnerEntity = createContextOwnerEntityMock();
        ContextGetResponseDto contextAssignedDto =
                WhiteboxImpl.invokeMethod(contextService, "mapContextOwnerEntityToContextAssignedDto",
                        contextOwnerEntity);
        /*
        assertEquals("Wrong context id", contextId, contextAssignedDto.getId());
        assertEquals("Wrong collection id", collectionId.toString(), contextAssignedDto.getCollection().getId());
        assertEquals("Wrong owner profile id", ownerProfileId, contextAssignedDto.getOwner().getId());
        assertEquals("Wrong createdDate value", createdAt.getTime(), contextAssignedDto.getCreatedDate());
        assertEquals("Wrong metadata value", 1, contextAssignedDto.getContextData().getMetadata().getStartDate());
        assertTrue("Wrong hasStarted value", contextAssignedDto.getHasStarted());
        */
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
        classMember.setMemberIds(Arrays.asList(memberId));
        return classMember;
    }

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
        when(contextOwnerEntity.getId()).thenReturn(contextId);
        when(contextOwnerEntity.getCollectionId()).thenReturn(collectionId);
        when(contextOwnerEntity.getContextData()).thenReturn(contextData);
        when(contextOwnerEntity.getCreatedAt()).thenReturn(createdAt);
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(ownerProfileId);
        when(contextOwnerEntity.getContextProfileId()).thenReturn(contextProfileId);
        return contextOwnerEntity;
    }

    private ContextPostRequestDto createContextPostRequestDto() {
        ContextPostRequestDto contextPostRequestDto = new ContextPostRequestDto();
        contextPostRequestDto.setCollectionId(collectionId);
        contextPostRequestDto.setContextData(createContextDataDto());
        return contextPostRequestDto;
    }

}
