package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.ContextAssignedGetResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.controller.AssignmentDto;
import com.quizzes.api.common.dto.controller.ContextDataDto;
import com.quizzes.api.common.dto.controller.ProfileDto;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.model.entities.ContextByOwnerEntity;
import com.quizzes.api.common.model.entities.ContextOwnerEntity;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.content.CollectionContentService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.boot.json.JsonParser;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
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
    JsonParser jsonParser = new GsonJsonParser();

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

    @Test
    public void createContextFindProfile() throws Exception {
        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setExternalCollectionId(UUID.randomUUID().toString());

        ProfileDto ownerDTO = new ProfileDto();
        ownerDTO.setId("external-id");
        assignmentDto.setOwner(ownerDTO);

        ContextDataDto contextDataMock = new ContextDataDto();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        assignmentDto.setContextData(contextDataMock);

        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId("1");
        ProfileDto profile2 = new ProfileDto();
        profile1.setId("2");
        assignees.add(profile1);
        assignees.add(profile2);
        assignmentDto.setAssignees(assignees);

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

        Context contextResult = new Context(UUID.randomUUID(),
                collectionResult.getId(), groupResult.getId(), new Gson().toJson(assignmentDto.getContextData()), null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollectionCopy(any(String.class), any(Profile.class))).thenReturn(collectionResult);

        Context result = contextService.createContext(assignmentDto, lms);

        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(2)).save(any(GroupProfile.class));
        verify(profileService, times(0)).save(any(Profile.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(collectionContentService, times(1)).createCollectionCopy(any(String.class), any(Profile.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionResult.getId(), result.getCollectionId());
        assertEquals("Wrong id for group", groupResult.getId(), result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

    @Test
    public void createContextCreateProfile() throws Exception {
        AssignmentDto assignmentDto = new AssignmentDto();
        assignmentDto.setExternalCollectionId(UUID.randomUUID().toString());

        ProfileDto ownerDTO = new ProfileDto();
        ownerDTO.setId("external-id");
        assignmentDto.setOwner(ownerDTO);

        ContextDataDto contextDataMock = new ContextDataDto();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        assignmentDto.setContextData(contextDataMock);

        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId("1");
        assignees.add(profile1);
        assignmentDto.setAssignees(assignees);

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

        Context contextResult = new Context(UUID.randomUUID(),
                collectionResult.getId(), groupResult.getId(), new Gson().toJson(assignmentDto.getContextData()), null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollectionCopy(any(String.class), any(Profile.class))).thenReturn(collectionResult);

        Context result = contextService.createContext(assignmentDto, lms);

        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(1)).save(any(GroupProfile.class));
        verify(profileService, times(2)).save(any(Profile.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(collectionContentService, times(1)).createCollectionCopy(any(String.class), any(Profile.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionResult.getId(), result.getCollectionId());
        assertEquals("Wrong id for group", groupResult.getId(), result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

    @Test
    public void update() throws Exception {
        ContextPutRequestDto contextDataMock = new ContextPutRequestDto();
        ContextPutRequestDto.MetadataDTO metadata = new ContextPutRequestDto.MetadataDTO();

        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("classId", "classId");
        metadata.setMetadata(metadataMap);
        contextDataMock.setContextData(metadata);

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
        Context contextResult = new Context(id, collectionId, groupId, "{\"context\":\"value\"}", null);
        Profile profile = new Profile(UUID.randomUUID(), "234", Lms.its_learning, "{body}", null);
        when(contextRepository.findById(any(UUID.class))).thenReturn(contextResult);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(profileService.save(any(Profile.class))).thenReturn(profile);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(null);

        Context result = contextService.update(UUID.randomUUID(), contextDataMock, Lms.its_learning);
        contextResult.setContextData("{\"contextMap\":{\"classId\":\"classId\"}}");

        verify(contextRepository, times(1)).findById(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(profileService, times(2)).save(any(Profile.class));
        verify(contextProfileService, times(2)).save(any(ContextProfile.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionId, result.getCollectionId());
        assertEquals("Wrong id for group", groupId, result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

    @Test
    public void updateAndDelete() throws Exception {
        ContextPutRequestDto contextDataMock = new ContextPutRequestDto();
        ContextPutRequestDto.MetadataDTO metadata = new ContextPutRequestDto.MetadataDTO();

        Map<String, String> metadataMap = new HashMap<>();
        metadataMap.put("classId", "classId");
        metadata.setMetadata(metadataMap);
        contextDataMock.setContextData(metadata);

        List<ProfileDto> assignees = new ArrayList<>();
        ProfileDto profile1 = new ProfileDto();
        profile1.setId(UUID.randomUUID().toString());
        List<UUID> ids = new ArrayList<>();
        ids.add(UUID.randomUUID());
        assignees.add(profile1);
        contextDataMock.setAssignees(assignees);

        UUID id = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        Context contextResult = new Context(id, collectionId, groupId, "{\"context\":\"value\"}", null);
        Profile profile = new Profile(UUID.randomUUID(), "234", Lms.its_learning, "{body}", null);
        when(contextRepository.findById(any(UUID.class))).thenReturn(contextResult);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(profileService.save(any(Profile.class))).thenReturn(profile);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(null);
        when(contextProfileService.findContextProfileIdsByContextId(any(UUID.class))).thenReturn(ids);

        Context result = contextService.update(UUID.randomUUID(), contextDataMock, Lms.its_learning);
        contextResult.setContextData("{\"contextMap\":{\"classId\":\"classId\"}}");

        verify(contextRepository, times(1)).findById(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));
        verify(profileService, times(1)).save(any(Profile.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileService, times(1)).delete(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionId, result.getCollectionId());
        assertEquals("Wrong id for group", groupId, result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

    @Test
    public void startContextEvent() throws Exception {
        UUID collectionId = UUID.randomUUID();

        UUID contextProfileId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setCurrentResourceId(contextProfileId);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpent", System.currentTimeMillis());
        eventData.put("reaction", 5);
        eventData.put("answer", "[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        new Gson().toJson(eventData), null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        Map<String, Object> map = new HashMap<>();
        map.put("answer", "test");
        List<Object> listMock = new ArrayList<>();
        listMock.add("[{\"value\":\"1\"},{\"value\":\"2,3\"}]");
        when(jsonParser.parseMap(any(String.class))).thenReturn(map);
        when(jsonParser.parseList(any(String.class))).thenReturn(listMock);

        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);
        when(contextRepository.findCollectionIdByContextId(any(UUID.class))).thenReturn(collectionId);
        when(contextProfileEventService.findAttemptsByContextProfileIdAndResourceId(any(UUID.class), any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileEventService, times(1)).findAttemptsByContextProfileIdAndResourceId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(1)).findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextRepository, times(1)).findCollectionIdByContextId(any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Id is Null", result.getId());
        assertNotNull("Resource id is Null", result.getCurrentResourceId());
        assertNotNull("Collection id is Null", result.getCollection().getId());
        assertEquals("Wrong size", 1, result.getAttempt().size());
        assertEquals("Answer list is Null", "{answer=[[{\"value\":\"1\"},{\"value\":\"2,3\"}]]}", result.getAttempt().get(0).toString());
    }

    @Test
    public void startContextEventListNull() throws Exception {
        UUID collectionId = UUID.randomUUID();

        UUID contextProfileId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setCurrentResourceId(contextProfileId);

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpent", System.currentTimeMillis());
        eventData.put("reaction", 5);
        eventData.put("answer", "[{\"value\":\"1\"},{\"value\":\"2,3\"}]");

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
                        new Gson().toJson(eventData), null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        when(contextProfileService.findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);
        when(contextRepository.findCollectionIdByContextId(any(UUID.class))).thenReturn(collectionId);
        when(contextProfileEventService.findAttemptsByContextProfileIdAndResourceId(any(UUID.class), any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextService.startContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileEventService, times(1)).findAttemptsByContextProfileIdAndResourceId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(1)).findContextProfileByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextRepository, times(1)).findCollectionIdByContextId(any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));

        assertNotNull("Response is Null", result);
        assertNotNull("Id is Null", result.getId());
        assertNotNull("Resource id is Null", result.getCurrentResourceId());
        assertNotNull("Collection id is Null", result.getCollection().getId());
        assertEquals("Wrong size", 1, result.getAttempt().size());
        assertEquals("Answer list is Null", "{answer=[]}", result.getAttempt().get(0).toString());
    }

    @Test(expected = ContentNotFoundException.class)
    public void updateException() throws Exception {
        when(contextRepository.findById(any(UUID.class))).thenReturn(null);
        Context result = contextService.update(UUID.randomUUID(), new ContextPutRequestDto(), Lms.its_learning);
    }

    @Test
    public void getContextNotFound() throws Exception {
        when(contextRepository.findContextAndOwnerByContextId(any(UUID.class))).thenReturn(null);
        ContextGetResponseDto result = contextService.getContext(UUID.randomUUID());

        assertNull("Result is not null", result);
        verify(contextRepository, times(1)).findContextAndOwnerByContextId(any(UUID.class));
        verify(profileService, times(0)).findAssignedIdsByContextId(any(UUID.class));
    }

    @Test
    public void getContext() throws Exception {
        UUID assignee = UUID.randomUUID();

        List<UUID> assignees = new ArrayList<>();
        assignees.add(assignee);

        when(contextOwnerEntity.getCollectionId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getOwnerId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getContextData()).thenReturn("\"metadata\":{}");

        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        when(jsonParser.parseMap(any(String.class))).thenReturn(map);

        when(contextRepository.findContextAndOwnerByContextId(any(UUID.class))).thenReturn(getContextOwnerEntityMock());
        when(profileService.findAssignedIdsByContextId(any(UUID.class))).thenReturn(assignees);

        ContextGetResponseDto result = contextService.getContext(UUID.randomUUID());

        verify(contextRepository, times(1)).findContextAndOwnerByContextId(any(UUID.class));
        verify(profileService, times(1)).findAssignedIdsByContextId(any(UUID.class));

        assertNotNull("Result is Null", result);
        assertNotNull("Context id is null", result.getId());
        assertNotNull("Owner id is null", result.getOwner().getId());
        assertFalse("ContextData is empty", result.getContextDataResponse().isEmpty());
        assertEquals("Size of the list is wrong", 1, result.getAssignees().size());
    }

    @Test
    public void getAssignedContexts() {
        when(contextOwnerEntity.getContextId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getCollectionId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getOwnerId()).thenReturn(UUID.randomUUID());
        when(contextOwnerEntity.getContextData()).thenReturn("context");

        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        when(jsonParser.parseMap(any(String.class))).thenReturn(map);

        List<ContextOwnerEntity> list = new ArrayList<>();
        list.add(getContextOwnerEntityMock());

        when(contextRepository.findAssignedContextsByProfileId(any(UUID.class))).thenReturn(list);

        List<ContextAssignedGetResponseDto> result = contextService.getAssignedContexts(UUID.randomUUID());

        verify(contextRepository, times(1)).findAssignedContextsByProfileId(any(UUID.class));

        ContextAssignedGetResponseDto resultEntity = result.get(0);
        assertEquals("Wrong size", 1, result.size());

        assertNotNull("First object is null", resultEntity);
        assertNotNull("Id is null", resultEntity.getId());
        assertNotNull("Id is null", resultEntity.getCollection().getId());

        assertFalse("Context response is empty", resultEntity.getContextDataResponse().isEmpty());
        assertNotNull("Owner is null", resultEntity.getOwner().getId());

        assertNull("Context is not null", resultEntity.getContextData());
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
        Map<UUID, List<ContextByOwnerEntity>> contextsMap = new HashMap<>();
        UUID contextId = UUID.randomUUID();
        ContextByOwnerEntity row1 = new ContextByOwnerEntity();
        row1.setId(contextId);
        row1.setCollectionId(UUID.randomUUID());
        row1.setGroupId(UUID.randomUUID());
        row1.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"155c951b-c2c9-435a-815d-81e455e681f0\"}}");
        row1.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        row1.setAssigneeId(UUID.randomUUID());
        List<ContextByOwnerEntity> contextByOwnerEntityList1 = new ArrayList<>();
        contextByOwnerEntityList1.add(row1);
        contextsMap.put(contextId, contextByOwnerEntityList1);

        contextId = UUID.randomUUID();
        UUID groupId =  UUID.randomUUID();
        ContextByOwnerEntity row2 = new ContextByOwnerEntity();
        row2.setId(contextId);
        row2.setCollectionId(UUID.randomUUID());
        row2.setGroupId(groupId);
        row2.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 2nd Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");
        row2.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        row2.setAssigneeId(UUID.randomUUID());
        List<ContextByOwnerEntity> contextByOwnerEntityList2 = new ArrayList<>();
        contextByOwnerEntityList2.add(row2);
        ContextByOwnerEntity row3 = new ContextByOwnerEntity();
        row3.setId(contextId);
        row3.setCollectionId(UUID.randomUUID());
        row3.setGroupId(groupId);
        row3.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 2nd Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");
        row3.setCreatedAt(Timestamp.valueOf(LocalDateTime.now()));
        row3.setAssigneeId(UUID.randomUUID());
        contextByOwnerEntityList2.add(row3);
        contextsMap.put(contextId, contextByOwnerEntityList2);

        when(contextRepository.findContextByOwnerId(any(UUID.class))).thenReturn(contextsMap);

        List<CreatedContextGetResponseDto> result = contextService.findCreatedContexts(UUID.randomUUID());
        verify(contextRepository, times(1)).findContextByOwnerId(any(UUID.class));

        assertNotNull("Created contexts list in null", result);
        assertEquals("Created contexts doesn't match", 2, result.size());
        assertNotNull("Context has no Collection", result.get(0).getCollection());
        assertNotNull("Context has no assignees", result.get(0).getAssignees());
    }

    private ContextOwnerEntity getContextOwnerEntityMock(){
        return new ContextOwnerEntity() {
            @Override
            public UUID getContextId() {
                return UUID.randomUUID();
            }

            @Override
            public void setContextId(UUID contextId) {

            }

            @Override
            public UUID getCollectionId() {
                return UUID.randomUUID();
            }

            @Override
            public void setCollectionId(UUID collectionId) {

            }

            @Override
            public UUID getOwnerId() {
                return UUID.randomUUID();
            }

            @Override
            public void setOwnerId(UUID profileId) {

            }

            @Override
            public String getContextData() {
                return "\"metadata\":{}";
            }

            @Override
            public void setContextData(String contextData) {

            }
        };
    }
}
