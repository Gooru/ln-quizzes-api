package com.quizzes.api.realtime.controller;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.ProfileIdDTO;
import com.quizzes.api.common.dto.controller.StartContextEventResponseDTO;
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
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
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

        ProfileDTO owner = new ProfileDTO();
        owner.setId("1");
        owner.setFirstName("firstName");
        owner.setLastName("lastname");
        owner.setUsername("username");
        assignment.setOwner(owner);

        ProfileDTO assignee = new ProfileDTO();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDTO> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        assignment.setCollection(collection);

        ContextDataDTO contextData = new ContextDataDTO();
        assignment.setContextData(contextData);

        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
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

        ResponseEntity<?> result = controller.assignContext(new AssignmentDTO(), Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("Error in owner"));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("Error in assignees"));
    }

    @Test
    public void assignContextStudentValidation() throws Exception {
        Context context = new Context();
        UUID contextId = UUID.randomUUID();
        context.setId(contextId);
        when(contextService.createContext(any(AssignmentDTO.class), any(Lms.class))).thenReturn(context);

        AssignmentDTO assignment = new AssignmentDTO();
        ProfileDTO owner = new ProfileDTO();
        owner.setId("1");
        owner.setFirstName("firstName");
        owner.setLastName("lastname");
        owner.setUsername("username");
        assignment.setOwner(owner);
        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        assignment.setCollection(collection);
        ContextDataDTO contextData = new ContextDataDTO();
        assignment.setContextData(contextData);

        //Testing no assignees
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), not(containsString("Error in owner")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in assignees"));
        assertThat(result.getBody().toString(), containsString("At least one assignee is required"));

        ProfileDTO assignee = new ProfileDTO();
        List<ProfileDTO> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        //testing empty assignee
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in assignees"));
        assertThat(result.getBody().toString(), containsString("ID is required"));
        assertThat(result.getBody().toString(), containsString("Firstname is required"));
        assertThat(result.getBody().toString(), containsString("Lastname is required"));
        assertThat(result.getBody().toString(), containsString("Username is required"));

        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
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

        ProfileDTO assignee = new ProfileDTO();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDTO> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        assignment.setCollection(collection);

        ContextDataDTO contextData = new ContextDataDTO();
        assignment.setContextData(contextData);

        //Testing no owner
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), not(containsString("Error in owners")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in owner"));
        assertThat(result.getBody().toString(), containsString("A Owner is required"));

        ProfileDTO owner = new ProfileDTO();
        assignment.setOwner(owner);

        //testing empty owner
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in owner"));
        assertThat(result.getBody().toString(), containsString("ID is required"));
        assertThat(result.getBody().toString(), containsString("Firstname is required"));
        assertThat(result.getBody().toString(), containsString("Lastname is required"));
        assertThat(result.getBody().toString(), containsString("Username is required"));

        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
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

        ProfileDTO assignee = new ProfileDTO();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDTO> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        ProfileDTO owner = new ProfileDTO();
        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");
        assignment.setOwner(owner);

        ContextDataDTO contextData = new ContextDataDTO();
        assignment.setContextData(contextData);

        //Testing no collection
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in assignees")));
        assertThat(result.getBody().toString(), not(containsString("Error in owners")));
        assertThat(result.getBody().toString(), not(containsString("Error in context")));
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("A Collection is required"));

        CollectionDTO collection = new CollectionDTO();
        assignment.setCollection(collection);

        //testing empty collection
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("ID is required"));

        collection.setId("2");

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
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

        ProfileDTO assignee = new ProfileDTO();
        assignee.setId("12345");
        assignee.setFirstName("firstname01");
        assignee.setLastName("lastname01");
        assignee.setUsername("firstname01");
        List<ProfileDTO> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);

        ProfileDTO owner = new ProfileDTO();
        owner.setId("12345");
        owner.setFirstName("firstname01");
        owner.setLastName("lastname01");
        owner.setUsername("firstname01");
        assignment.setOwner(owner);

        CollectionDTO collection = new CollectionDTO();
        collection.setId("2");
        assignment.setCollection(collection);

        //Testing no context
        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), not(containsString("Error in assignees")));
        assertThat(result.getBody().toString(), not(containsString("Error in owners")));
        assertThat(result.getBody().toString(), not(containsString("Error in collection")));
        assertThat(result.getBody().toString(), containsString("Error in context"));
        assertThat(result.getBody().toString(), containsString("A ContextData is required"));

        ContextDataDTO contextData = new ContextDataDTO();
        assignment.setContextData(contextData);

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Test
    public void startContextEvent() throws Exception {
        ProfileIdDTO requestBody = new ProfileIdDTO();
        requestBody.setProfileId(UUID.randomUUID());

        ResponseEntity<?> result = controller.startContextEvent("123", "quizzes", UUID.randomUUID());
        Object resultBody = result.getBody();
        assertSame(resultBody.getClass(), StartContextEventResponseDTO.class);
        assertNotNull("Current resource ID is null", ((StartContextEventResponseDTO)resultBody).getCurrentResourceId());
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