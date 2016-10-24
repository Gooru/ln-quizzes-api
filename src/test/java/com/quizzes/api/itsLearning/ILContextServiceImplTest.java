package com.quizzes.api.itsLearning;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.model.tables.pojos.Collection;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.CollectionService;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.GooruAPIService;
import com.quizzes.api.common.service.GroupProfileService;
import com.quizzes.api.common.service.GroupService;
import com.quizzes.api.common.service.ProfileService;
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
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ILContextServiceImplTest {

    @InjectMocks
    private ContextService contextService = Mockito.spy(ILContextServiceImpl.class);

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

        TeacherDTO teacherDTO = new TeacherDTO();
        assignmentDTO.setTeacher(teacherDTO);

        Map<String, String> contextMock = new HashMap<>();
        contextMock.put("classId", "classId");
        assignmentDTO.setContext(contextMock);

        List<StudentDTO> students = new ArrayList<>();
        assignmentDTO.setStudents(students);

        Profile teacherResult = new Profile();
        teacherResult.setId(UUID.fromString("aa778e3a-fa35-48f2-a0b8-32dd5ce42edd"));
        when(profileService.findOrCreateTeacher(teacherDTO)).thenReturn(teacherResult);

        Collection collectionResult = new Collection();
        collectionResult.setId(UUID.fromString("b3ec9d42-5dc2-4486-a22b-6a0e347fb98b"));
        when(collectionService.findOrCreateCollection(collectionDTO)).thenReturn(collectionResult);

        Group groupResult = new Group();
        groupResult.setId(UUID.fromString("b3ec9d42-7777-4486-a22b-6a0e347fb98b"));
        when(groupService.createGroup(teacherResult)).thenReturn(groupResult);

        Context contextResult = new Context(UUID.fromString("a7ec9d42-7777-4486-a22b-6a0e347fb98b"),
                collectionResult.getId(), groupResult.getId(),  new Gson().toJson(assignmentDTO.getContext()), null);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);
        when(contextRepository.save(any(Context.class))).thenReturn(contextResult);

        doNothing().when(gooruAPIService).getAccessToken();

        Context result = contextService.createContext(assignmentDTO);

        verify(gooruAPIService, times(1)).getAccessToken();
        verify(profileService, times(1)).findOrCreateTeacher(Mockito.eq(teacherDTO));
        verify(collectionService, times(1)).findOrCreateCollection(Mockito.eq(collectionDTO));
        verify(groupService, times(1)).createGroup(Mockito.eq(teacherResult));
        verify(groupProfileService, times(1)).assignStudentListToGroup(Mockito.eq(groupResult), Mockito.eq(students));
        verify(contextRepository, times(1)).save(any(Context.class));

        assertNotNull(result);
        assertEquals(result.getId().toString(), "a7ec9d42-7777-4486-a22b-6a0e347fb98b");

        assertEquals(result.getCollectionId().toString(), "b3ec9d42-5dc2-4486-a22b-6a0e347fb98b");

        assertEquals(result.getGroupId().toString(), "b3ec9d42-7777-4486-a22b-6a0e347fb98b");

        assertEquals(result.getContextData().toString(), "{\"classId\":\"classId\"}");
    }

}