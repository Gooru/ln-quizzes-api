package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.gooru.service.GooruAPIService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

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
public class ContextServiceImplTest {

    @InjectMocks
    private ContextService contextService = Mockito.spy(ContextServiceImpl.class);

    @Mock
    ProfileService profileService;

    @Mock
    ContextRepository contextRepository;

    @Mock
    CollectionService collectionService;

    @Mock
    GroupService groupService;

    @Mock
    GroupProfileService groupProfileService;

    @Mock
    GooruAPIService gooruAPIService;

    @Test
    public void createContext() throws Exception {
        AssignmentDTO assignmentDTO = new AssignmentDTO();

        CollectionDTO collectionDTO = new CollectionDTO();
        assignmentDTO.setCollection(collectionDTO);

        ProfileDTO ownerDTO = new ProfileDTO();
        assignmentDTO.setOwner(ownerDTO);

        ContextDataDTO contextDataMock = new ContextDataDTO();
        Map<String, String> contextMapMock = new HashMap<>();
        contextMapMock.put("classId", "classId");
        contextDataMock.setContextMap(contextMapMock);
        assignmentDTO.setContextData(contextDataMock);

        List<ProfileDTO> assignees = new ArrayList<>();
        assignmentDTO.setAssignees(assignees);

        Lms lms = Lms.its_learning;

        Profile teacherResult = new Profile();
        teacherResult.setId(UUID.randomUUID());
        when(profileService.findOrCreateOwner(ownerDTO, lms)).thenReturn(teacherResult);

        Collection collectionResult = new Collection();
        collectionResult.setId(UUID.randomUUID());
        when(collectionService.findOrCreateCollection(collectionDTO)).thenReturn(collectionResult);

        Group groupResult = new Group();
        groupResult.setId(UUID.randomUUID());
        when(groupService.createGroup(teacherResult)).thenReturn(groupResult);

        Context contextResult = new Context(UUID.randomUUID(),
                collectionResult.getId(), groupResult.getId(), new Gson().toJson(assignmentDTO.getContextData()), null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        Context result = contextService.createContext(assignmentDTO, lms);

        verify(profileService, times(1)).findOrCreateOwner(Mockito.eq(ownerDTO), Mockito.eq(lms));
        verify(collectionService, times(1)).findOrCreateCollection(Mockito.eq(collectionDTO));
        verify(groupService, times(1)).createGroup(Mockito.eq(teacherResult));
        verify(groupProfileService, times(1)).assignAssigneesListToGroup(Mockito.eq(groupResult), Mockito.eq(assignees));
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong id for context", contextResult.getId(), result.getId());
        assertEquals("Wrong id for collection", collectionResult.getId(), result.getCollectionId());
        assertEquals("Wrong id for group", groupResult.getId(), result.getGroupId());
        assertEquals("Wrong context data", "{\"contextMap\":{\"classId\":\"classId\"}}", result.getContextData());
    }

}