package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.CreatedContextGetResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
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

    @Test
    public void createContextFindProfile() throws Exception {
        AssignmentDTO assignmentDTO = new AssignmentDTO();
        assignmentDTO.setExternalCollectionId(UUID.randomUUID().toString());

        ProfileDTO ownerDTO = new ProfileDTO();
        ownerDTO.setId("external-id");
        assignmentDTO.setOwner(ownerDTO);

        ContextDataDTO contextDataMock = new ContextDataDTO();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        assignmentDTO.setContextData(contextDataMock);

        List<ProfileDTO> assignees = new ArrayList<>();
        ProfileDTO profile1 = new ProfileDTO();
        profile1.setId("1");
        ProfileDTO profile2 = new ProfileDTO();
        profile1.setId("2");
        assignees.add(profile1);
        assignees.add(profile2);
        assignmentDTO.setAssignees(assignees);

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
                collectionResult.getId(), groupResult.getId(), new Gson().toJson(assignmentDTO.getContextData()), null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollectionCopy(any(String.class), any(Profile.class))).thenReturn(collectionResult);

        Context result = contextService.createContext(assignmentDTO, lms);

        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(2)).save(any(GroupProfile.class));
        verify(profileService, times(0)).save(any(Profile.class));
        verify(collectionService, times(1)).save(any(Collection.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionResult.getId(), result.getCollectionId());
        assertEquals("Wrong id for group", groupResult.getId(), result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

    @Test
    public void createContextCreateProfile() throws Exception {
        AssignmentDTO assignmentDTO = new AssignmentDTO();
        assignmentDTO.setExternalCollectionId(UUID.randomUUID().toString());

        ProfileDTO ownerDTO = new ProfileDTO();
        ownerDTO.setId("external-id");
        assignmentDTO.setOwner(ownerDTO);

        ContextDataDTO contextDataMock = new ContextDataDTO();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        assignmentDTO.setContextData(contextDataMock);

        List<ProfileDTO> assignees = new ArrayList<>();
        ProfileDTO profile1 = new ProfileDTO();
        profile1.setId("1");
        assignees.add(profile1);
        assignmentDTO.setAssignees(assignees);

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
                collectionResult.getId(), groupResult.getId(), new Gson().toJson(assignmentDTO.getContextData()), null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        when(collectionContentService.createCollectionCopy(any(String.class), any(Profile.class))).thenReturn(collectionResult);

        Context result = contextService.createContext(assignmentDTO, lms);

        verify(profileService, times(1)).findByExternalIdAndLmsId(Mockito.eq(ownerDTO.getId()), Mockito.eq(lms));
        verify(groupProfileService, times(1)).save(any(GroupProfile.class));
        verify(profileService, times(2)).save(any(Profile.class));
        verify(collectionService, times(1)).save(any(Collection.class));
        verify(groupService, times(1)).createGroup(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));

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

        List<ProfileDTO> assignees = new ArrayList<>();
        ProfileDTO profile1 = new ProfileDTO();
        profile1.setId(UUID.randomUUID().toString());
        ProfileDTO profile2 = new ProfileDTO();
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

        List<ProfileDTO> assignees = new ArrayList<>();
        ProfileDTO profile1 = new ProfileDTO();
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

        Map<String, String> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpend", "1478623337");
        eventData.put("reaction", UUID.randomUUID().toString());
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

        Map<String, String> eventData = new HashMap<>();
        eventData.put("id", UUID.randomUUID().toString());
        eventData.put("timeSpend", "1478623337");
        eventData.put("reaction", UUID.randomUUID().toString());
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
    public void getContext() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        context.setGroupId(UUID.randomUUID());
        context.setCollectionId(UUID.randomUUID());
        context.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}");

        when(contextRepository.mockedFindById(any(UUID.class))).thenReturn(context);

        Context result = contextService.getContext(UUID.randomUUID());

        assertNotNull("Result is Null", result);


        assertNotNull("Context id is null", result.getId());
        assertNotNull("Collection id is null", result.getCollectionId());
        assertNotNull("Group is null", result.getGroupId());
        assertNotNull("ContextData is null", result.getContextData());
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
        UUID groupId = UUID.randomUUID();

        List<Context> contexts = new ArrayList<>();
        Context context = new Context();
        context.setId(UUID.randomUUID());
        context.setCollectionId(UUID.randomUUID());
        context.setGroupId(groupId);
        context.setContextData("{\"metadata\": {\"description\": \"First Partial\",\"title\": \"Math 1st Grade\"}," +
                "\"contextMap\": {\"classId\": \"9e8f32bd-04fd-42c2-97f9-36addd23d850\"}}");
        contexts.add(context);

        when(contextRepository.findByOwnerId(any(UUID.class))).thenReturn(contexts);

        List<GroupProfile> groupProfiles = new ArrayList<>();
        GroupProfile asignee1 = new GroupProfile();
        asignee1.setGroupId(groupId);
        asignee1.setId(UUID.randomUUID());
        asignee1.setProfileId(UUID.randomUUID());
        groupProfiles.add(asignee1);
        GroupProfile asignee2 = new GroupProfile();
        asignee2.setGroupId(groupId);
        asignee2.setId(UUID.randomUUID());
        asignee2.setProfileId(UUID.randomUUID());
        groupProfiles.add(asignee2);

        when(groupProfileService.findGroupProfilesByGroupId(any(UUID.class))).thenReturn(groupProfiles);

        List<CreatedContextGetResponseDto> result = contextService.findCreatedContexts(UUID.randomUUID());
        verify(contextRepository, times(1)).findByOwnerId(any(UUID.class));
        verify(groupProfileService, times(1)).findGroupProfilesByGroupId(any(UUID.class));

        assertNotNull("Created contexts list in null", result);
        assertEquals("No created contexts", 1, result.size());
        assertNotNull("Context has no Collection", result.get(0).getCollection());
        assertNotNull("Context has no assignees", result.get(0).getAssignees());
        assertEquals("Wrong number of assignees", 2, result.get(0).getAssignees().size());

    }
}