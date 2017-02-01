package com.quizzes.api.core.services;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.core.dtos.*;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.exceptions.ContentNotFoundException;
import com.quizzes.api.core.exceptions.InvalidOwnerException;
import com.quizzes.api.core.model.entities.ContextAssigneeEntity;
import com.quizzes.api.core.model.entities.ContextOwnerEntity;
import com.quizzes.api.core.model.jooq.tables.pojos.Context;
import com.quizzes.api.core.repositories.ContextRepository;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
public class ContextServiceTest {

    @InjectMocks
    private ContextService contextService = spy(ContextService.class);

    @Mock
    private ContextRepository contextRepository;

    @Mock
    private ContextProfileEventService contextProfileEventService;

    @Mock
    private ContextProfileService contextProfileService;

    @Mock
    private Gson gson = new Gson();

    private UUID contextId;
    private UUID groupId;
    private UUID collectionId;
    private UUID ownerProfileId;
    private UUID contextProfileId;
    private Timestamp createdAt;

    @Before
    public void before() throws Exception {
        contextId = UUID.randomUUID();
        collectionId = UUID.randomUUID();
        ownerProfileId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        createdAt = Timestamp.from(Instant.now());
        groupId = UUID.randomUUID();
    }

    @Ignore
    @Test
    public void createContextFindProfile() throws Exception {
        ContextPostRequestDto contextPostRequestDto = new ContextPostRequestDto();
        contextPostRequestDto.setExternalCollectionId(UUID.randomUUID().toString());

        ProfileDto ownerDTO = new ProfileDto();
        ownerDTO.setId("external-id");
        contextPostRequestDto.setOwner(ownerDTO);

        ContextDataDto contextDataMock = new ContextDataDto();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        contextPostRequestDto.setContextData(contextDataMock);

        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId("1");
        ProfileDto profile2 = new ProfileDto();
        profile1.setId("2");
        assignees.add(profile1);
        assignees.add(profile2);
        contextPostRequestDto.setAssignees(assignees);

        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(UUID.randomUUID());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        //when(collectionContentService.createCollection(any(String.class), any(Profile.class)))
        //        .thenReturn(collectionResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto);

        verify(contextRepository, times(1)).save(any(Context.class));
        //verify(collectionContentService, times(1)).createCollection(any(String.class), any(Profile.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
    }

    @Ignore
    @Test
    public void createContextWithExistingCollection() throws Exception {
        String externalCollectionId = UUID.randomUUID().toString();

        ContextPostRequestDto contextPostRequestDto = new ContextPostRequestDto();
        contextPostRequestDto.setExternalCollectionId(externalCollectionId);

        ProfileDto ownerDto = new ProfileDto();
        ownerDto.setId("external-id1");
        contextPostRequestDto.setOwner(ownerDto);

        ContextDataDto contextDataMock = new ContextDataDto();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        contextPostRequestDto.setContextData(contextDataMock);

        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId("1");
        ProfileDto profile2 = new ProfileDto();
        profile1.setId("2");
        assignees.add(profile1);
        assignees.add(profile2);
        contextPostRequestDto.setAssignees(assignees);

        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(UUID.randomUUID());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto);

        //Creates the new group
        //Adds the 2 Assignees to the new roup
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Context ID is Null", result.getId());
    }

    @Ignore
    @Test
    public void createContextWithExistingCollectionByOwnerAndExternalParentID() throws Exception {
        String externalCollectionId = UUID.randomUUID().toString();

        ContextPostRequestDto contextPostRequestDto = new ContextPostRequestDto();
        contextPostRequestDto.setExternalCollectionId(externalCollectionId);

        ProfileDto ownerDto = new ProfileDto();
        ownerDto.setId("external-id1");
        contextPostRequestDto.setOwner(ownerDto);

        ContextDataDto contextDataMock = new ContextDataDto();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        contextPostRequestDto.setContextData(contextDataMock);

        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId("1");
        ProfileDto profile2 = new ProfileDto();
        profile1.setId("2");
        assignees.add(profile1);
        assignees.add(profile2);
        contextPostRequestDto.setAssignees(assignees);


        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(UUID.randomUUID());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto);

        //Creates the new group
        //Adds the 2 Assignees to the new roup
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Context ID is Null", result.getId());
    }

    @Ignore
    @Test
    public void createContextCreateProfile() throws Exception {
        ContextPostRequestDto contextPostRequestDto = new ContextPostRequestDto();
        contextPostRequestDto.setExternalCollectionId(UUID.randomUUID().toString());

        ProfileDto ownerDTO = new ProfileDto();
        ownerDTO.setId("external-id");
        contextPostRequestDto.setOwner(ownerDTO);

        ContextDataDto contextDataMock = new ContextDataDto();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        contextPostRequestDto.setContextData(contextDataMock);

        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId("1");
        assignees.add(profile1);
        contextPostRequestDto.setAssignees(assignees);

        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(UUID.randomUUID());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        //when(collectionContentService.createCollection(any(String.class), any(Profile.class)))
        //       .thenReturn(collectionResult);

        ProfileDto anyProfile = new ProfileDto();
        anyProfile.setId(UUID.randomUUID().toString());
        anyProfile.setFirstName("Celso");
        anyProfile.setLastName("Borges");
        anyProfile.setUsername("cborges");

        IdResponseDto result = contextService.createContext(contextPostRequestDto);

        verify(contextRepository, times(1)).save(any(Context.class));
        //verify(collectionContentService, times(1)).createCollection(any(String.class), any(Profile.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
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
        metadataDto.setDueDate(234234);
        metadataDto.setStartDate(324234);

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

        Context context = createContext();

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

    @Test
    public void getAssignedContexts() throws Exception {
        ContextOwnerEntity contextOwnerEntity = createContextOwnerEntityMock();

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
    }

    @Test
    public void getAssignedContextByContextIdAndAssigneeId() throws Exception {
        ContextOwnerEntity contextOwnerEntity = createContextOwnerEntityMock();

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
    }

    @Test(expected = ContentNotFoundException.class)
    public void getAssignedContextByContextIdAndAssigneeIdThrowsContentNotFoundException() {
        when(contextRepository.findContextOwnerByContextIdAndAssigneeId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);
        contextService.getAssignedContextByContextIdAndAssigneeId(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    public void findCreatedContexts() {
        Map<UUID, List<ContextAssigneeEntity>> contextsMap = new HashMap<>();

        ContextAssigneeEntity row1 = mock(ContextAssigneeEntity.class);
        when(row1.getId()).thenReturn(contextId);
        when(row1.getCollectionId()).thenReturn(UUID.randomUUID());
        when(row1.getGroupId()).thenReturn(UUID.randomUUID());
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
        when(row2.getGroupId()).thenReturn(groupId);
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
        when(row3.getGroupId()).thenReturn(groupId);
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
        assertNotNull("Context has no Collection", result.get(0).getCollection());
        assertNotNull("Context has no assignees", result.get(0).getAssignees());
    }

    @Test
    public void findCreatedContextByContextIdAndOwnerId() {
        Map<UUID, List<ContextAssigneeEntity>> contextsMap = new HashMap<>();
        UUID ownerId = UUID.randomUUID();
        UUID classId = UUID.randomUUID();

        ContextAssigneeEntity contextAssigneeEntity = mock(ContextAssigneeEntity.class);
        when(contextAssigneeEntity.getId()).thenReturn(contextId);
        when(contextAssigneeEntity.getCollectionId()).thenReturn(UUID.randomUUID());
        when(contextAssigneeEntity.getGroupId()).thenReturn(UUID.randomUUID());
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

        ContextGetResponseDto result =
                contextService.findCreatedContextByContextIdAndOwnerId(contextId, ownerId);
        verify(contextRepository, times(1)).findContextAssigneeByContextIdAndOwnerId(contextId, ownerId);

        assertNotNull("Context is null", result);
        assertNotNull("Wrong collection Id", result.getCollection().getId());
        assertNotNull("Is is null", result.getId());
        assertEquals("Context has no assignees", 1, result.getAssignees().size());

        Map<String, String> contextDataResult = result.getContextData().getContextMap();
        assertEquals("Wrong context data value", classId.toString(), contextDataResult.get("classId"));

        MetadataDto metadataResult = result.getContextData().getMetadata();
        assertEquals("Context has no assignees", "First Partial", metadataResult.getDescription());
        assertEquals("Context has no assignees", "Math 1st Grade", metadataResult.getTitle());
    }

    @Test(expected = ContentNotFoundException.class)
    public void findCreatedContextByContextIdAndOwnerIdNoContentFoundResponse() {
        UUID ownerId = UUID.randomUUID();

        when(contextRepository.findContextAssigneeByContextIdAndOwnerId(contextId, ownerId))
                .thenReturn(new HashMap());

        contextService.findCreatedContextByContextIdAndOwnerId(contextId, ownerId);
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

    @Test
    public void mapContextOwnerEntityToContextAssignedDto() throws Exception {
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
    }

    private Context createContext() {
        Context context = new Context();
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
        context.setId(contextId);
        context.setCollectionId(collectionId);
        context.setContextData(contextData);
        context.setCreatedAt(createdAt);
        return context;
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
        when(contextOwnerEntity.getGroupId()).thenReturn(groupId);
        when(contextOwnerEntity.getContextData()).thenReturn(contextData);
        when(contextOwnerEntity.getCreatedAt()).thenReturn(createdAt);
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(ownerProfileId);
        when(contextOwnerEntity.getContextProfileId()).thenReturn(contextProfileId);
        return contextOwnerEntity;
    }

}
