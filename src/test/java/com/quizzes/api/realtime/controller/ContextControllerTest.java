package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ProfileIdDTO;
import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.service.ContextService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ContextControllerTest {

    @InjectMocks
    private ContextController controller = new ContextController();

    @Mock
    private ContextService contextService;

    @Test
    public void assignContext() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        AssignmentDTO assignment = new AssignmentDTO();

        TeacherDTO teacher = new TeacherDTO();
        teacher.setId("1");
        teacher.setFirstName("firstName");
        teacher.setLastName("lastname");
        teacher.setUsername("username");
        assignment.setTeacher(teacher);

        StudentDTO student = new StudentDTO();
        student.setId("12345");
        student.setFirstName("firstname01");
        student.setLastName("lastname01");
        student.setUsername("firstname01");
        List<StudentDTO> students = new ArrayList<>();
        students.add(student);
        assignment.setStudents(students);

        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        collection.setDescription("collection description test");
        collection.setName("collection test01");
        assignment.setCollection(collection);

        Map<String, String> contextDto = new HashMap<>();
        contextDto.put("classId","123");
        assignment.setContext(contextDto);

        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Test
    public void assignContextEmptyAssignment() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        ResponseEntity<?> result = controller.assignContext(new AssignmentDTO(), Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("Error in teacher"));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("Error in students"));
    }

    @Test
    public void assignContextStudentValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        AssignmentDTO assignment = new AssignmentDTO();
        TeacherDTO teacher = new TeacherDTO();
        teacher.setId("1");
        teacher.setFirstName("firstName");
        teacher.setLastName("lastname");
        teacher.setUsername("username");
        assignment.setTeacher(teacher);
        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        collection.setDescription("collection description test");
        collection.setName("collection test01");
        assignment.setCollection(collection);
        Map<String, String> contextDto = new HashMap<>();
        contextDto.put("classId","123");
        assignment.setContext(contextDto);

        //Testing no students
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), not(containsString("Error in teacher")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in students"));
        assertThat(result.getBody().toString(), containsString("At least one student is required"));

        StudentDTO student = new StudentDTO();
        List<StudentDTO> students = new ArrayList<>();
        students.add(student);
        assignment.setStudents(students);

        //testing empty student
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in students"));
        assertThat(result.getBody().toString(), containsString("ID is required"));
        assertThat(result.getBody().toString(), containsString("Firstname is required"));
        assertThat(result.getBody().toString(), containsString("Lastname is required"));
        assertThat(result.getBody().toString(), containsString("Username is required"));

        student.setId("12345");
        student.setFirstName("firstname01");
        student.setLastName("lastname01");
        student.setUsername("firstname01");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Test
    public void assignContextTeacherValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        AssignmentDTO assignment = new AssignmentDTO();

        StudentDTO student = new StudentDTO();
        student.setId("12345");
        student.setFirstName("firstname01");
        student.setLastName("lastname01");
        student.setUsername("firstname01");
        List<StudentDTO> students = new ArrayList<>();
        students.add(student);
        assignment.setStudents(students);

        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        collection.setDescription("collection description test");
        collection.setName("collection test01");
        assignment.setCollection(collection);

        Map<String, String> contextDto = new HashMap<>();
        contextDto.put("classId","123");
        assignment.setContext(contextDto);

        //Testing no teacher
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), not(containsString("Error in teachers")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in teacher"));
        assertThat(result.getBody().toString(), containsString("A Teacher is required"));

        TeacherDTO teacher = new TeacherDTO();
        assignment.setTeacher(teacher);

        //testing empty teacher
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in teacher"));
        assertThat(result.getBody().toString(), containsString("ID is required"));
        assertThat(result.getBody().toString(), containsString("Firstname is required"));
        assertThat(result.getBody().toString(), containsString("Lastname is required"));
        assertThat(result.getBody().toString(), containsString("Username is required"));

        teacher.setId("12345");
        teacher.setFirstName("firstname01");
        teacher.setLastName("lastname01");
        teacher.setUsername("firstname01");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Test
    public void assignContextCollectionValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        AssignmentDTO assignment = new AssignmentDTO();

        StudentDTO student = new StudentDTO();
        student.setId("12345");
        student.setFirstName("firstname01");
        student.setLastName("lastname01");
        student.setUsername("firstname01");
        List<StudentDTO> students = new ArrayList<>();
        students.add(student);
        assignment.setStudents(students);

        TeacherDTO teacher = new TeacherDTO();
        teacher.setId("12345");
        teacher.setFirstName("firstname01");
        teacher.setLastName("lastname01");
        teacher.setUsername("firstname01");
        assignment.setTeacher(teacher);

        Map<String, String> contextDto = new HashMap<>();
        contextDto.put("classId","123");
        assignment.setContext(contextDto);

        //Testing no collection
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in students")));
        assertThat(result.getBody().toString(), not(containsString("Error in teachers")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("A Collection is required"));

        CollectionDTO collection = new CollectionDTO();
        assignment.setCollection(collection);

        //testing empty collection
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("ID is required"));
        assertThat(result.getBody().toString(), containsString("Description is required"));
        assertThat(result.getBody().toString(), containsString("Name is required"));

        collection.setId("2");
        collection.setDescription("collection description test");
        collection.setName("collection test01");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Test
    public void assignContextContextValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        AssignmentDTO assignment = new AssignmentDTO();

        StudentDTO student = new StudentDTO();
        student.setId("12345");
        student.setFirstName("firstname01");
        student.setLastName("lastname01");
        student.setUsername("firstname01");
        List<StudentDTO> students = new ArrayList<>();
        students.add(student);
        assignment.setStudents(students);

        TeacherDTO teacher = new TeacherDTO();
        teacher.setId("12345");
        teacher.setFirstName("firstname01");
        teacher.setLastName("lastname01");
        teacher.setUsername("firstname01");
        assignment.setTeacher(teacher);

        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        collection.setDescription("collection description test");
        collection.setName("collection test01");
        assignment.setCollection(collection);

        //Testing no context
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in students")));
        assertThat(result.getBody().toString(), not(containsString("Error in teachers")));
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("A Context is required"));

        Map<String, String> contextDto = new HashMap<>();
        contextDto.put("classId","123");
        assignment.setContext(contextDto);

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Test
    public void startContextEvent() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.randomUUID());

        ResponseEntity<?> result = controller.startContextEvent("externalId", requestBody);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void registerResource() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.randomUUID());

        ResponseEntity<?> result = controller.onResourceEvent("resourceId", "externalId", requestBody);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Body is not null", null, result.getBody());
    }

    @Test
    public void finishContextEvent() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.randomUUID());

        ResponseEntity<?> result = controller.finishContextEvent("externalId", requestBody);
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Body is not null", null, result.getBody());
    }

}