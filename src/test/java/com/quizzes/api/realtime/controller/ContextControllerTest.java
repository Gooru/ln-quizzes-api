package com.quizzes.api.realtime.controller;

import com.google.gson.JsonArray;
import com.quizzes.api.common.controller.ContextController;
import com.quizzes.api.common.dto.CommonContextGetResponseDto;
import com.quizzes.api.common.dto.ContextGetAssignedResponseDto;
import com.quizzes.api.common.dto.ContextGetCreatedResponseDto;
import com.quizzes.api.common.dto.ContextGetResponseDto;
import com.quizzes.api.common.dto.ContextIdResponseDto;
import com.quizzes.api.common.dto.ContextPutRequestDto;
import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.dto.controller.request.OnResourceEventRequestDTO;
import com.quizzes.api.common.dto.controller.request.ResourceDTO;
import com.quizzes.api.common.dto.controller.response.AnswerDTO;
import com.quizzes.api.common.dto.controller.response.StartContextEventResponseDto;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Context;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import com.quizzes.api.common.service.ContextService;
import com.quizzes.api.common.service.GroupProfileService;
import com.quizzes.api.common.service.GroupService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.boot.json.JsonParser;
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ContextControllerTest {

    @InjectMocks
    private ContextController controller = new ContextController();

    @Mock
    private ContextService contextService;

    @Mock
    private GroupService groupService;

    @Mock
    private GroupProfileService groupProfileService;

    @Mock
    private JsonParser jsonParser;

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
        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        ContextDataDTO contextData = new ContextDataDTO();
        assignment.setContextData(contextData);

        ResponseEntity<?> result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        Object resultBody = result.getBody();
        assertSame(resultBody.getClass(), ContextIdResponseDto.class);
        assertEquals("Response body is wrong", ((ContextIdResponseDto) resultBody).getId(), context.getId());
    }

    @Ignore
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

    @Ignore
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
        assignment.setExternalCollectionId(UUID.randomUUID().toString());

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

    @Ignore
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

        assignment.setExternalCollectionId(UUID.randomUUID().toString());

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

    @Ignore
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

        assignment.setExternalCollectionId(UUID.randomUUID().toString());

        //testing empty collection
        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.NOT_ACCEPTABLE.value(), result.getStatusCode().value());
        assertThat(result.getBody().toString(), containsString("Error in collection"));
        assertThat(result.getBody().toString(), containsString("ID is required"));

        result = controller.assignContext(assignment, Lms.its_learning.getLiteral(), UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK.value(), result.getStatusCode().value());
        assertEquals("Response body is wrong:", "{contextId=" + contextId.toString() + "}", result.getBody().toString());
    }

    @Ignore
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

        assignment.setExternalCollectionId(UUID.randomUUID().toString());

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
        UUID id = UUID.randomUUID();
        UUID resourceId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        CollectionDTO collection = new CollectionDTO();
        collection.setId(String.valueOf(collectionId));

        StartContextEventResponseDto startContext = new StartContextEventResponseDto();
        startContext.setId(id);
        startContext.setCurrentResourceId(resourceId);
        startContext.setCollection(collection);

        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("answer", new JsonArray());
        list.add(map);

        startContext.setAttempt(list);

        when(contextService.startContextEvent(any(UUID.class), any(UUID.class))).thenReturn(startContext);

        ResponseEntity<StartContextEventResponseDto> result = controller.startContextEvent(UUID.randomUUID(), "quizzes", UUID.randomUUID());

        verify(contextService, times(1)).startContextEvent(any(UUID.class), any(UUID.class));

        StartContextEventResponseDto resultBody = result.getBody();
        assertSame(resultBody.getClass(), StartContextEventResponseDto.class);
        assertEquals("Wrong resource id is null", resourceId, resultBody.getCurrentResourceId());
        assertEquals("Wrong id", id, resultBody.getId());
        assertEquals("Wrong collection id", collection.getId(), resultBody.getCollection().getId());
        assertEquals("Wrong collection id", 1, resultBody.getAttempt().size());
        assertTrue("Answer key not found", resultBody.getAttempt().get(0).containsKey("answer"));
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void registerResource() throws Exception {
        AnswerDTO answerDTO = new AnswerDTO("1");
        List<AnswerDTO> answerDTOList = new ArrayList<>();
        answerDTOList.add(answerDTO);
        ResourceDTO resource = new ResourceDTO(UUID.randomUUID(), 120, 3, answerDTOList);
        OnResourceEventRequestDTO requestBody = new OnResourceEventRequestDTO(resource);

        ResponseEntity<?> result = controller.onResourceEvent("1", "1", requestBody, "quizzes", UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertEquals("Body is not null", null, result.getBody());
    }

    @Test
    public void finishContextEvent() throws Exception {
        ResponseEntity<?> result = controller.finishContextEvent(UUID.randomUUID(), "its_learning", UUID.randomUUID());
        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code:", HttpStatus.OK, result.getStatusCode());
        assertNull("Body is not null", result.getBody());
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

        when(contextService.getContext(any(UUID.class))).thenReturn(context);

        Group group = new Group();
        group.setId(UUID.randomUUID());
        group.setOwnerProfileId(UUID.randomUUID());

        when(groupService.findById(any(UUID.class))).thenReturn(group);

        List<GroupProfile> groupProfiles = new ArrayList<>();

        GroupProfile asignee1 = new GroupProfile();
        asignee1.setGroupId(group.getId());
        asignee1.setId(UUID.randomUUID());
        asignee1.setProfileId(UUID.randomUUID());
        groupProfiles.add(asignee1);

        GroupProfile asignee2 = new GroupProfile();
        asignee2.setGroupId(group.getId());
        asignee2.setId(UUID.randomUUID());
        asignee2.setProfileId(UUID.randomUUID());
        groupProfiles.add(asignee2);

        when(groupProfileService.findGroupProfilesByGroupId(any(UUID.class))).thenReturn(groupProfiles);

        Map<String,Object> contextDataMap = new HashMap<>();
        Map<String, String> contextMap = new HashMap<>();
        contextMap.put("classId", UUID.randomUUID().toString());
        contextDataMap.put("contextMap", contextMap);
        Map<String, String> metadata = new HashMap<>();
        metadata.put("title", "Math 1st Grade");
        metadata.put("description", "First Partial");
        contextDataMap.put("metadata", metadata);

        when(jsonParser.parseMap(any(String.class))).thenReturn(contextDataMap);

        ResponseEntity<ContextGetResponseDto> result = controller.getContext(UUID.randomUUID(), "its_learning", UUID.randomUUID());

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code", HttpStatus.OK, result.getStatusCode());
        assertNotNull("Body is null", result.getBody());

        assertNotNull("Context id is null", result.getBody().getId());
        assertNotNull("Collection id is null", result.getBody().getCollection().getId());

        ProfileDTO ownerResult = result.getBody().getOwner();
        assertNotNull("Owner id is null", ownerResult.getId());

        List<ProfileDTO> profiles = result.getBody().getAssignees();
        assertEquals("Wrong list size for assignees", 2, profiles.size());
        assertNotNull("Profile1 id is null", profiles.get(0).getId());

        CommonContextGetResponseDto.ContextDataDto contextResult = result.getBody().getContextData();
        assertEquals("Wrong size inside context map", 1, contextResult.getContextMap().size());
        assertEquals("Wrong size inside metadata", 2, contextResult.getMetadata().size());
        assertEquals("Key title with invalid value in metadata", "Math 1st Grade", contextResult.getMetadata().get("title"));
        assertEquals("Key description with invalid value in metadata", "First Partial", contextResult.getMetadata().get("description"));
    }

    @Test
    public void getContextsCreated() throws Exception {
        Map<String, String> filter = new HashMap<>();
        filter.put("classId", UUID.randomUUID().toString());
        ResponseEntity<List<ContextGetCreatedResponseDto>> response = controller
                .getContextsCreated(Lms.its_learning.getLiteral(), UUID.randomUUID(), filter);

        assertNotNull("Response is Null", response);
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong list size for assignments", 1, response.getBody().size());

        ContextGetCreatedResponseDto result = response.getBody().get(0);
        assertNotNull("Body is null", result);
        assertNotNull("Context id is null", result.getId());

        assertNotNull("Collection id is null", result.getCollection().getId());

        List<ProfileDTO> profiles = result.getAssignees();
        assertEquals("Wrong list size for assignees", 2, profiles.size());
        assertNotNull("Profile1 id is null", profiles.get(0).getId());
        assertEquals("Wrong first name in owner", "Karol", profiles.get(0).getFirstName());
        assertEquals("Wrong last name in owner", "Fernandez", profiles.get(0).getLastName());
        assertEquals("Wrong username in owner", "karol1", profiles.get(0).getUsername());

        CommonContextGetResponseDto.ContextDataDto contextResult = result.getContextData();
        assertEquals("Wrong size inside context map", 1, contextResult.getContextMap().size());
        assertEquals("Wrong size inside metadata", 2, contextResult.getMetadata().size());
        assertEquals("Key title with invalid value in metadata", "Math 1st Grade", contextResult.getMetadata().get("title"));
        assertEquals("Key description with invalid value in metadata", "First Partial", contextResult.getMetadata().get("description"));
    }

    @Test
    public void getAssignedContexts() throws Exception {

        ResponseEntity<List<ContextGetAssignedResponseDto>> response = controller.getAssignedContexts("its_learning", UUID.randomUUID());

        assertNotNull("Response is Null", response);
        assertEquals("Invalid status code", HttpStatus.OK, response.getStatusCode());
        assertEquals("Wrong list size for assignments", 1, response.getBody().size());

        ContextGetAssignedResponseDto result = response.getBody().get(0);
        assertNotNull("Body is null", result);
        assertNotNull("Context id is null", result.getId());

        assertNotNull("Collection id is null", result.getCollection().getId());

        ProfileDTO ownerResult = result.getOwner();
        assertNotNull("Owner id is null", ownerResult.getId());
        assertEquals("Wrong first name in owner", "Michael", ownerResult.getFirstName());
        assertEquals("Wrong last name in owner", "Guth", ownerResult.getLastName());
        assertEquals("Wrong username in owner", "migut", ownerResult.getUsername());

        CommonContextGetResponseDto.ContextDataDto contextResult = result.getContextData();
        assertEquals("Wrong size inside context map", 1, contextResult.getContextMap().size());
        assertEquals("Wrong size inside metadata", 2, contextResult.getMetadata().size());
        assertEquals("Key title with invalid value in metadata", "Math 1st Grade", contextResult.getMetadata().get("title"));
        assertEquals("Key description with invalid value in metadata", "Second Partial", contextResult.getMetadata().get("description"));
    }

    @Test
    public void updateContext() throws Exception {
        Context contextResult = new Context(UUID.randomUUID(), UUID.randomUUID(),
                UUID.randomUUID(), "{\"context\":\"value\"}", null);
        when(contextService.update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class))).thenReturn(contextResult);

        ResponseEntity<ContextIdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());

        verify(contextService, times(1)).update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class));

        assertNotNull("Response is Null", result);
        assertEquals("Invalid status code", HttpStatus.OK, result.getStatusCode());
        assertEquals("Invalid status code", contextResult.getId(), result.getBody().getId());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateContextException() throws Exception {
        when(contextService.update(any(UUID.class), any(ContextPutRequestDto.class), any(Lms.class))).thenReturn(null);
        ResponseEntity<ContextIdResponseDto> result = controller.updateContext(UUID.randomUUID(),
                new ContextPutRequestDto(), "its_learning", UUID.randomUUID());
    }

}