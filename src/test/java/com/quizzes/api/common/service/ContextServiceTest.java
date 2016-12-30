package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextPostRequestDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.IdResponseDto;
import com.quizzes.api.common.dto.MetadataDto;
import com.quizzes.api.common.dto.ProfileDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextService.class)
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
    Gson gson = new Gson();

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

        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(collectionResult.getId());
        contextResult.setGroupId(groupResult.getId());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollection(any(String.class), any(Profile.class)))
                .thenReturn(collectionResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, lms);

        verify(collectionService, times(1)).findByOwnerProfileIdAndExternalParentId(any(UUID.class), (any(String.class)));
        verify(collectionService, times(1)).findByExternalId(any(String.class));
        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(2)).save(any(GroupProfile.class));
        verify(profileService, times(0)).save(any(Profile.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(collectionContentService, times(1)).createCollection(any(String.class), any(Profile.class));

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
        UUID ownerProfileID = UUID.randomUUID();
        collectionResult.setOwnerProfileId(ownerProfileID);
        when(collectionService.findByExternalId(any(String.class))).thenReturn(collectionResult);

        //We create a new group for this new context
        Group groupResult = new Group();
        groupResult.setId(UUID.randomUUID());
        when(groupService.createGroup(any(UUID.class))).thenReturn(groupResult);

        //We assume all the profiles exists
        //it doesn't matter if the profiles exists in this test or should be created
        Profile profileResponse = new Profile();
        profileResponse.setId(ownerProfileID);
        when(profileService.findByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(profileResponse);

        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(collectionResult.getId());
        contextResult.setGroupId(groupResult.getId());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, lms);

        verify(collectionService, times(1)).findByOwnerProfileIdAndExternalParentId(any(UUID.class), (any(String.class)));
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

        Lms lms = Lms.its_learning;

        //This means that the collection exists
        Collection parentCollectionResult = new Collection();
        parentCollectionResult.setId(UUID.randomUUID());
        UUID ownerProfileID = UUID.randomUUID();
        parentCollectionResult.setOwnerProfileId(ownerProfileID);
        when(collectionService.findByOwnerProfileIdAndExternalParentId(any(UUID.class), any(String.class))).thenReturn(parentCollectionResult);

        //We create a new group for this new context
        Group groupResult = new Group();
        groupResult.setId(UUID.randomUUID());
        when(groupService.createGroup(any(UUID.class))).thenReturn(groupResult);

        //We assume all the profiles exists
        //it doesn't matter if the profiles exists in this test or should be created
        Profile profileResponse = new Profile();
        profileResponse.setId(ownerProfileID);
        when(profileService.findByExternalIdAndLmsId(any(String.class), any(Lms.class))).thenReturn(profileResponse);

        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(parentCollectionResult.getId());
        contextResult.setGroupId(groupResult.getId());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        IdResponseDto result = contextService.createContext(contextPostRequestDto, lms);

        verify(collectionService, times(1)).findByOwnerProfileIdAndExternalParentId(any(UUID.class), (any(String.class)));
        verify(collectionService, never()).findByExternalId(any(String.class));
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

        Context contextResult = new Context();
        contextResult.setId(UUID.randomUUID());
        contextResult.setCollectionId(collectionResult.getId());
        contextResult.setGroupId(groupResult.getId());
        contextResult.setContextData(gson.toJson(contextPostRequestDto.getContextData()));
        contextResult.setIsDeleted(false);
        contextResult.setIsActive(true);

        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollection(any(String.class), any(Profile.class)))
                .thenReturn(collectionResult);

        ProfileDto anyProfile = new ProfileDto();
        anyProfile.setId(UUID.randomUUID().toString());
        anyProfile.setFirstName("Celso");
        anyProfile.setLastName("Borges");
        anyProfile.setUsername("cborges");

        IdResponseDto result = contextService.createContext(contextPostRequestDto, lms);

        verify(collectionService, times(1)).findByExternalId(any(String.class));
        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(1)).save(any(GroupProfile.class));
        verify(profileService, times(2)).save(any(Profile.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(collectionContentService, times(1)).createCollection(any(String.class), any(Profile.class));

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
    public void findByIdAndOwnerId() {
        UUID id = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        Context contextResult = new Context();
        contextResult.setId(id);
        contextResult.setGroupId(groupId);
        contextResult.setCollectionId(collectionId);
        contextResult.setContextData("{\"context\":\"value\"}");
        contextResult.setIsDeleted(false);
        when(contextRepository.findByIdAndOwnerId(any(UUID.class), any(UUID.class))).thenReturn(contextResult);

        Context result = contextService.findByIdAndOwnerId(UUID.randomUUID(), UUID.randomUUID());

        verify(contextRepository, times(1)).findByIdAndOwnerId(any(UUID.class), any(UUID.class));
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

        when(contextRepository.findByIdAndOwnerId(any(UUID.class), any(UUID.class))).thenReturn(contextResult);

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

        Context result = contextService.update(UUID.randomUUID(), UUID.randomUUID(), contextDataMock, Lms.its_learning);
        contextResult.setContextData("{\"contextMap\":{\"classId\":\"classId\"}}");

        verify(contextRepository, times(1)).findByIdAndOwnerId(any(UUID.class), any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(profileService, times(1)).save(any(List.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionId, result.getCollectionId());
        assertEquals("Wrong id for group", groupId, result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

    @Test(expected = ContentNotFoundException.class)
    public void updateException() throws Exception {
        when(contextRepository.findByIdAndOwnerId(any(UUID.class), any(UUID.class))).thenReturn(null);
        Context result = contextService.update(UUID.randomUUID(), UUID.randomUUID(), new ContextPutRequestDto(), Lms.its_learning);
    }

    //TODO: switch to not Started
    @Test
    public void getAssignedContextsStarted() {
        UUID id = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        long startDate = 32424;
        String contextData = "{\n" +
                "\t\t\"metadata\": {\n" +
                "\t\t  \"description\": \"First Partial\",\n" +
                "\t\t  \"startDate\": \"" + startDate + "\",\n" +
                "\t\t  \"title\": \"Math 1st Grade\"\n" +
                "\t\t},\n" +
                "\t\t\"contextMap\": {\n" +
                "\t\t  \"classId\": \"" + classId + "\"\n" +
                "\t\t}\n" +
                "\t}";

        when(contextOwnerEntity.getId()).thenReturn(id);
        when(contextOwnerEntity.getCollectionId()).thenReturn(collectionId);
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(ownerId);
        when(contextOwnerEntity.getContextData()).thenReturn(contextData);
        when(contextOwnerEntity.getCreatedAt()).thenReturn(new Timestamp(new Date().getTime()));
        when(contextOwnerEntity.getContextProfileId()).thenReturn(UUID.randomUUID());

        List<ContextOwnerEntity> list = new ArrayList<>();
        list.add(contextOwnerEntity);

        when(contextRepository.findContextOwnerByAssigneeId(any(UUID.class))).thenReturn(list);

        List<ContextAssignedGetResponseDto> result = contextService.getAssignedContexts(UUID.randomUUID());

        verify(contextRepository, times(1)).findContextOwnerByAssigneeId(any(UUID.class));

        ContextAssignedGetResponseDto resultEntity = result.get(0);

        assertEquals("Wrong context profile id", true, resultEntity.getHasStarted());
    }



    @Test
    public void getAssignedContexts() {
        UUID id = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID contextProfileId = UUID.randomUUID();
        long startDate = 324234;
        String contextData = "{\n" +
                "\t\t\"metadata\": {\n" +
                "\t\t  \"description\": \"First Partial\",\n" +
                "\t\t  \"startDate\": \"" + startDate + "\",\n" +
                "\t\t  \"title\": \"Math 1st Grade\"\n" +
                "\t\t},\n" +
                "\t\t\"contextMap\": {\n" +
                "\t\t  \"classId\": \"" + classId + "\"\n" +
                "\t\t}\n" +
                "\t}";

        when(contextOwnerEntity.getId()).thenReturn(id);
        when(contextOwnerEntity.getCollectionId()).thenReturn(collectionId);
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(ownerId);
        when(contextOwnerEntity.getContextData()).thenReturn(contextData);
        when(contextOwnerEntity.getContextProfileId()).thenReturn(contextProfileId);
        when(contextOwnerEntity.getCreatedAt()).thenReturn(new Timestamp(new Date().getTime()));

        List<ContextOwnerEntity> list = new ArrayList<>();
        list.add(contextOwnerEntity);

        when(contextRepository.findContextOwnerByAssigneeId(any(UUID.class))).thenReturn(list);

        List<ContextAssignedGetResponseDto> result = contextService.getAssignedContexts(UUID.randomUUID());

        verify(contextRepository, times(1)).findContextOwnerByAssigneeId(any(UUID.class));

        ContextAssignedGetResponseDto resultEntity = result.get(0);
        assertEquals("Wrong size", 1, result.size());

        assertNotNull("First object is null", resultEntity);
        assertEquals("Wrong id", id, resultEntity.getId());
        assertEquals("Wrong collection id", collectionId.toString(), resultEntity.getCollection().getId());
        assertEquals("Wrong context profile id", true, resultEntity.getHasStarted());
        assertNotNull("Created Date is null", resultEntity.getCreatedDate());

        MetadataDto metadataResult = resultEntity.getContextData().getMetadata();
        assertNotNull("Metadata is null", metadataResult);
        assertEquals("Wrong title", "Math 1st Grade", metadataResult.getTitle());
        assertEquals("Wrong description", "First Partial", metadataResult.getDescription());
        assertEquals("Wrong start date", startDate, metadataResult.getStartDate());

        assertEquals("Wrong owner id", ownerId, resultEntity.getOwner().getId());
        assertEquals("Wrong class id", classId.toString(), resultEntity.getContextData().getContextMap().get("classId"));
    }

    @Test
    public void getAssignedContextByContextIdAndAssigneeId() {
        UUID id = UUID.randomUUID();
        UUID classId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        long startDate = 324234;
        String contextData = "{\n" +
                "\t\t\"metadata\": {\n" +
                "\t\t  \"description\": \"First Partial\",\n" +
                "\t\t  \"startDate\": \"" + startDate + "\",\n" +
                "\t\t  \"title\": \"Math 1st Grade\"\n" +
                "\t\t},\n" +
                "\t\t\"contextMap\": {\n" +
                "\t\t  \"classId\": \"" + classId + "\"\n" +
                "\t\t}\n" +
                "\t}";

        ContextOwnerEntity contextOwnerEntity = Mockito.spy(ContextOwnerEntity.class);
        when(contextOwnerEntity.getId()).thenReturn(id);
        when(contextOwnerEntity.getCollectionId()).thenReturn(collectionId);
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(ownerId);
        when(contextOwnerEntity.getContextData()).thenReturn(contextData);
        when(contextOwnerEntity.getCreatedAt()).thenReturn(new Timestamp(new Date().getTime()));
        when(contextOwnerEntity.getContextProfileId()).thenReturn(UUID.randomUUID());

        when(contextRepository .findContextOwnerByContextIdAndAssigneeId(any(UUID.class), any(UUID.class)))
                .thenReturn(contextOwnerEntity);

        ContextAssignedGetResponseDto resultEntity =
                contextService.getAssignedContextByContextIdAndAssigneeId(UUID.randomUUID(), UUID.randomUUID());

        verify(contextRepository, times(1)).findContextOwnerByContextIdAndAssigneeId(any(UUID.class), any(UUID.class));

        assertNotNull("First object is null", resultEntity);
        assertEquals("Wrong id", id, resultEntity.getId());
        assertEquals("Wrong collection id", collectionId.toString(), resultEntity.getCollection().getId());
        assertEquals("hasStarted is false", true, resultEntity.getHasStarted());

        assertNotNull("Created Date is null", resultEntity.getCreatedDate());

        MetadataDto metadataResult = resultEntity.getContextData().getMetadata();
        assertNotNull("Metadata is null", metadataResult);
        assertEquals("Wrong title", "Math 1st Grade", metadataResult.getTitle());
        assertEquals("Wrong description", "First Partial", metadataResult.getDescription());
        assertEquals("Wrong start date", startDate, metadataResult.getStartDate());

        assertEquals("Wrong owner id", ownerId, resultEntity.getOwner().getId());
        assertEquals("Wrong class id", classId.toString(), resultEntity.getContextData().getContextMap().get("classId"));
    }

    @Test(expected = ContentNotFoundException.class)
    public void getAssignedContextByContextIdAndAssigneeIdThrowNotContentFoundException() {
        when(contextRepository .findContextOwnerByContextIdAndAssigneeId(any(UUID.class), any(UUID.class)))
                .thenReturn(null);

        ContextAssignedGetResponseDto resultEntity =
                contextService.getAssignedContextByContextIdAndAssigneeId(UUID.randomUUID(), UUID.randomUUID());
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
    public void findCreatedContextByContextIdAndOwnerId() {
        Map<UUID, List<ContextAssigneeEntity>> contextsMap = new HashMap<>();
        UUID contextId = UUID.randomUUID();
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

        CreatedContextGetResponseDto result =
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
        UUID contextId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        when(contextRepository.findContextAssigneeByContextIdAndOwnerId(contextId, ownerId))
                .thenReturn(new HashMap());

        contextService.findCreatedContextByContextIdAndOwnerId(contextId, ownerId);


    }

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
        UUID id = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID ownerProfileId = UUID.randomUUID();
        String contextData = "{\n" +
                "    \"contextMap\": {\n" +
                "        \"classId\": \"1\"\n" +
                "    },\n" +
                "    \"metadata\": {\n" +
                "    \t\"startDate\": 1\n" +
                "    }\n" +
                "  }";

        ContextOwnerEntity contextOwnerEntity = Mockito.spy(ContextOwnerEntity.class);

        when(contextOwnerEntity.getId()).thenReturn(id);
        when(contextOwnerEntity.getCollectionId()).thenReturn(collectionId);
        when(contextOwnerEntity.getOwnerProfileId()).thenReturn(ownerProfileId);
        when(contextOwnerEntity.getContextData()).thenReturn(contextData);
        when(contextOwnerEntity.getCreatedAt()).thenReturn(new Timestamp(new Date().getTime()));
        when(contextOwnerEntity.getContextProfileId()).thenReturn(UUID.randomUUID());

        ContextAssignedGetResponseDto result =
                WhiteboxImpl.invokeMethod(contextService, "mapContextOwnerEntityToContextAssignedDto",
                        contextOwnerEntity);

        assertEquals("Wrong id", id, result.getId());
        assertEquals("Wrong collection id", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong owner id", ownerProfileId, result.getOwner().getId());
        assertEquals("Wrong id", 1, result.getContextData().getMetadata().getStartDate());
        assertTrue("It is not started", result.getHasStarted());
    }
}
