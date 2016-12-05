package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextPostRequestDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.MetadataDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.entities.ContextAssigneeEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.jooq.enums.Lms;
import com.quizzes.api.common.model.jooq.tables.pojos.Collection;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.Group;
import com.quizzes.api.common.model.jooq.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.content.CollectionContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.boot.json.JsonParser;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ContextService.class, Gson.class})
public class ContextServiceTest {

    @InjectMocks
    private ContextService contextService = Mockito.spy(ContextService.class);

    @Mock
    ProfileService profileService;

    @Mock
    ContextRepository contextRepository;

    @Mock
    ContextProfileEventService contextProfileEventService;

    @Mock
    ContextProfileService contextProfileService;

    @Mock
    JsonParser jsonParser;

    @Mock
    CollectionService collectionService;

    @Mock
    GroupService groupService;

    @Mock
    GroupProfileService groupProfileService;

    @Mock
    CollectionContentService collectionContentService;

    @Mock
    ContextOwnerEntity contextOwnerEntity;

    @Mock
    Gson gson;

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

        Lms lms = Lms.its_learning;

        Profile profileResponse = new Profile();
        UUID profileResponseId = UUID.randomUUID();
        profileResponse.setId(profileResponseId);
        when(profileService.findByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(profileResponse);

        Collection collectionResult = new Collection();
        collectionResult.setId(UUID.randomUUID());
        collectionResult.setOwnerProfileId(UUID.randomUUID());
        when(collectionService.save(any(Collection.class))).thenReturn(collectionResult);

        Group groupResult = new Group();
        groupResult.setId(UUID.randomUUID());
        when(groupService.createGroup(any(UUID.class))).thenReturn(groupResult);

        Context contextResult = new Context(UUID.randomUUID(), collectionResult.getId(), groupResult.getId(),
                new Gson().toJson(contextPostRequestDto.getContextData()), false, null, null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollectionCopy(any(String.class), any(Profile.class)))
                .thenReturn(collectionResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, lms);

        verify(collectionService, times(1)).findByExternalId(any(String.class));
        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(2)).save(any(GroupProfile.class));
        verify(profileService, times(0)).save(any(Profile.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(collectionContentService, times(1)).createCollectionCopy(any(String.class), any(Profile.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
    }

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

        Lms lms = Lms.its_learning;

        //This means that the collection exists
        Collection collectionResult = new Collection();
        collectionResult.setId(UUID.randomUUID());
        collectionResult.setOwnerProfileId(UUID.randomUUID());
        when(collectionService.findByExternalId(any(String.class))).thenReturn(collectionResult);

        //We create a new group for this new context
        Group groupResult = new Group();
        groupResult.setId(UUID.randomUUID());
        when(groupService.createGroup(any(UUID.class))).thenReturn(groupResult);

        //We assume all the profiles exists
        //it doesn't matter if the profiles exists in this test or should be created
        Profile profileResponse = new Profile();
        UUID profileResponseId = UUID.randomUUID();
        profileResponse.setId(profileResponseId);
        when(profileService.findByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(profileResponse);

        Context contextResult = new Context(UUID.randomUUID(), collectionResult.getId(), groupResult.getId(),
                new Gson().toJson(contextPostRequestDto.getContextData()), false, null, null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, lms);

        verify(collectionService, times(1)).findByExternalId(any(String.class));
        //Creates the new group
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(profileService, times(3)).findByExternalIdAndLmsId(any(String.class), any(Lms.class));
        //Adds the 2 Assignees to the new roup
        verify(groupProfileService, times(2)).save(any(GroupProfile.class));
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Context ID is Null", result.getId());
    }

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

        Lms lms = Lms.its_learning;

        Profile profileResponse = new Profile();
        UUID profileResponseId = UUID.randomUUID();
        profileResponse.setId(profileResponseId);
        when(profileService.findByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(null);
        when(profileService.save(any(Profile.class))).thenReturn(profileResponse);

        Collection collectionResult = new Collection();
        collectionResult.setId(UUID.randomUUID());
        collectionResult.setOwnerProfileId(UUID.randomUUID());
        when(collectionService.save(any(Collection.class))).thenReturn(collectionResult);

        Group groupResult = new Group();
        groupResult.setId(UUID.randomUUID());
        when(groupService.createGroup(any(UUID.class))).thenReturn(groupResult);

        Context contextResult = new Context(UUID.randomUUID(), collectionResult.getId(), groupResult.getId(),
                new Gson().toJson(contextPostRequestDto.getContextData()), false, null, null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollectionCopy(any(String.class), any(Profile.class)))
                .thenReturn(collectionResult);

        ProfileDto anyProfile = new ProfileDto();
        anyProfile.setId(UUID.randomUUID().toString());
        anyProfile.setFirstName("Celso");
        anyProfile.setLastName("Borges");
        anyProfile.setUsername("cborges");

        JsonElement jsonElement = new Gson().toJsonTree(anyProfile);

        when(gson.toJsonTree(any(ProfileDto.class))).thenReturn(jsonElement);

        String serializedContextData = new Gson().toJson(contextPostRequestDto.getContextData());

        when(gson.toJson(any(ProfileDto.class))).thenReturn(serializedContextData);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, lms);

        verify(collectionService, times(1)).findByExternalId(any(String.class));
        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(1)).save(any(GroupProfile.class));
        verify(profileService, times(2)).save(any(Profile.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(collectionContentService, times(1)).createCollectionCopy(any(String.class), any(Profile.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
    }

    @Test
    public void findById() {
        UUID id = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        Context contextResult = new Context();
        contextResult.setId(id);
        contextResult.setGroupId(groupId);
        contextResult.setCollectionId(collectionId);
        contextResult.setContextData("{\"context\":\"value\"}");
        contextResult.setIsDeleted(false);
        when(contextRepository.findById(any(UUID.class))).thenReturn(contextResult);

        Context result = contextService.findById(UUID.randomUUID());

        verify(contextRepository, times(1)).findById(any(UUID.class));
        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", id, result.getId());
        assertEquals("Wrong id for collection", collectionId, result.getCollectionId());
        assertEquals("Wrong id for group", groupId, result.getGroupId());
    }

    @Test
    public void update() throws Exception {
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

        UUID id = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        Context contextResult = new Context();
        contextResult.setId(id);
        contextResult.setGroupId(groupId);
        contextResult.setCollectionId(collectionId);
        contextResult.setContextData("{\"context\":\"value\"}");
        contextResult.setIsDeleted(false);

        when(contextRepository.findById(any(UUID.class))).thenReturn(contextResult);

        List<String> externalProfileIdsToFind = new ArrayList<>();
        //we are looking for this 2 profiles in the DB
        externalProfileIdsToFind.add(profile1.getId());
        externalProfileIdsToFind.add(profile2.getId());
        List<String> foundExternalProfileIds = new ArrayList<>();
        //this means only 1 out of 2 assignees exist in this context group
        foundExternalProfileIds.add(profile1.getId());

        when(profileService.findExternalProfileIds(externalProfileIdsToFind, Lms.its_learning)).thenReturn(foundExternalProfileIds);

        when(profileService.save(any(Profile.class))).thenReturn(new Profile());

        List<UUID> profileIds = new ArrayList<>();
        //we know that there are 2 profiles created in the context group, these are the assignee ids
        profileIds.add(UUID.randomUUID());
        profileIds.add(UUID.randomUUID());

        when(profileService.findProfileIdsByExternalIdAndLms(externalProfileIdsToFind, Lms.its_learning)).thenReturn(profileIds);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(UUID.randomUUID().toString());
        profileDto.setFirstName("Keylor");
        profileDto.setLastName("Navas");
        profileDto.setUsername("knavas");

        JsonElement jsonElement = new Gson().toJsonTree(profileDto);

        when(gson.toJsonTree(any(ProfileDto.class))).thenReturn(jsonElement);

//        ContextDataDto contextDataDto = new Gson().fromJson(contextDataMock.getContextData().getMetadata(), ContextDataDto.class);

        when(gson.fromJson(any(String.class), any())).thenReturn(contextDataDto);

        Context result = contextService.update(UUID.randomUUID(), contextDataMock, Lms.its_learning);
        contextResult.setContextData("{\"contextMap\":{\"classId\":\"classId\"}}");

        verify(contextRepository, times(1)).findById(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(profileService, times(1)).save(any(List.class));
        verify(gson, times(1)).toJsonTree(any(ProfileDto.class));
        verify(gson, times(1)).fromJson(any(String.class), any());

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionId, result.getCollectionId());
        assertEquals("Wrong id for group", groupId, result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

    @Test(expected = ContentNotFoundException.class)
    public void updateException() throws Exception {
        when(contextRepository.findById(any(UUID.class))).thenReturn(null);
        Context result = contextService.update(UUID.randomUUID(), new ContextPutRequestDto(), Lms.its_learning);
    }

    @Test
    public void getContextNotFound() throws Exception {
        when(contextRepository.findContextOwnerByContextId(any(UUID.class))).thenReturn(null);
        ContextGetResponseDto result = contextService.getContext(UUID.randomUUID());

        assertNull("Result is not null", result);
        verify(contextRepository, times(1)).findContextOwnerByContextId(any(UUID.class));
        verify(profileService, times(0)).findAssignedIdsByContextId(any(UUID.class));
    }

    @Test
    public void getContext() throws Exception {
        UUID assignee = UUID.randomUUID();

        List<UUID> assignees = new ArrayList<>();
        assignees.add(assignee);

        when(contextOwnerEntity.getCollectionId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getContextData()).thenReturn("\"metadata\":{}");

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

        when(gson.fromJson(any(String.class), any())).thenReturn(contextDataDto);
        when(contextRepository.findContextOwnerByContextId(any(UUID.class))).thenReturn(contextOwnerEntity);
        when(profileService.findAssignedIdsByContextId(any(UUID.class))).thenReturn(assignees);

        ContextGetResponseDto result = contextService.getContext(UUID.randomUUID());

        verify(contextRepository, times(1)).findContextOwnerByContextId(any(UUID.class));
        verify(profileService, times(1)).findAssignedIdsByContextId(any(UUID.class));

        assertNotNull("Result is Null", result);
        assertNotNull("Context id is null", result.getId());
        assertNotNull("Owner id is null", result.getOwner().getId());
        assertNotNull("Metadata is null", result.getContextData().getMetadata().getTitle());
        assertNotNull("ContextMap is null", result.getContextData().getContextMap());
        assertEquals("Size of the list is wrong", 1, result.getAssignees().size());
    }

    @Test
    public void getAssignedContexts() {
        when(contextOwnerEntity.getId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getCollectionId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getContextData()).thenReturn("context");
        when(contextOwnerEntity.getCreatedAt()).thenReturn(new Timestamp(new Date().getTime()));

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

        when(gson.fromJson(any(String.class), any())).thenReturn(contextDataDto);

        List<ContextOwnerEntity> list = new ArrayList<>();
        list.add(contextOwnerEntity);

        when(contextRepository.findContextOwnerByAssigneeId(any(UUID.class))).thenReturn(list);

        List<ContextAssignedGetResponseDto> result = contextService.getAssignedContexts(UUID.randomUUID());

        verify(contextRepository, times(1)).findContextOwnerByAssigneeId(any(UUID.class));

        ContextAssignedGetResponseDto resultEntity = result.get(0);
        assertEquals("Wrong size", 1, result.size());

        assertNotNull("First object is null", resultEntity);
        assertNotNull("Id is null", resultEntity.getId());
        assertNotNull("Id is null", resultEntity.getCollection().getId());

        assertNotNull("Created Date is null", resultEntity.getCreatedDate());

        MetadataDto metadataResult = resultEntity.getContextData().getMetadata();
        assertNotNull("Metadata is null", metadataResult);
        assertEquals("Metadata is null", "Math 1st Grade", metadataResult.getTitle());
        assertEquals("Metadata is null", "First Partial", metadataResult.getDescription());

        assertNotNull("Owner is null", resultEntity.getOwner().getId());
        assertNotNull("Context is null", resultEntity.getContextData());
    }

    @Test
    public void findContextByOwnerId() {
        List<Context> contextsByOwner = new ArrayList<>();

        Context context = new Context();
        context.setId(UUID.randomUUID());
        context.setCollectionId(UUID.randomUUID());
        context.setGroupId(UUID.randomUUID());
        context.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");
        contextsByOwner.add(context);

        when(contextRepository.findByOwnerId(any(UUID.class))).thenReturn(contextsByOwner);

        List<Context> result = contextService.findContextByOwnerId(UUID.randomUUID());

        verify(contextRepository, times(1)).findByOwnerId(any(UUID.class));
        assertNotNull("Context by owner is null", result);

    }

    @Test
    public void findCreatedContexts() {
        Map<UUID, List<ContextAssigneeEntity>> contextsMap = new HashMap<>();
        UUID contextId = UUID.randomUUID();

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

        List<CreatedContextGetResponseDto> result = contextService.findCreatedContexts(UUID.randomUUID());
        verify(contextRepository, times(1)).findContextAssigneeByOwnerId(any(UUID.class));

        assertNotNull("Created contexts list in null", result);
        assertEquals("Created contexts doesn't match", 2, result.size());
        assertNotNull("Context has no Collection", result.get(0).getCollection());
        assertNotNull("Context has no assignees", result.get(0).getAssignees());
    }

    @Test
    public void profileDtoToJsonObject() throws Exception {
        ProfileDto profileDto = new ProfileDto();
        profileDto.setId(UUID.randomUUID().toString());
        profileDto.setFirstName("Keylor");
        profileDto.setLastName("Navas");
        profileDto.setUsername("knavas");

        JsonElement jsonElement = new Gson().toJsonTree(profileDto);

        when(gson.toJsonTree(any(ProfileDto.class))).thenReturn(jsonElement);

        JsonObject jsonObject = WhiteboxImpl.invokeMethod(contextService, "profileDtoToJsonObject", profileDto);

        verify(gson, times(1)).toJsonTree(any(ProfileDto.class));

        assertEquals(jsonObject.size(), 3);
        assertEquals(jsonObject.get("firstName").getAsString(), "Keylor");
        assertEquals(jsonObject.get("lastName").getAsString(), "Navas");
        assertEquals(jsonObject.get("username").getAsString(), "knavas");
        assertNull(jsonObject.get("id"));

    }
}
