package com.quizzes.api.common.service;

import com.google.gson.Gson;
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

        UUID id = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        UUID groupId = UUID.randomUUID();
        Context contextResult = new Context(id, collectionId, groupId, "{\"context\":\"value\"}", null);
        when(contextRepository.findById(any(UUID.class))).thenReturn(contextResult);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        Context result = contextService.update(UUID.randomUUID(), contextDataMock);
        contextResult.setContextData("{\"contextMap\":{\"classId\":\"classId\"}}");

        verify(contextRepository, times(1)).findById(any(UUID.class));
        verify(contextRepository, times(1)).save(any(Context.class));

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
        Context result = contextService.update(UUID.randomUUID(), new ContextPutRequestDto());
    }

}