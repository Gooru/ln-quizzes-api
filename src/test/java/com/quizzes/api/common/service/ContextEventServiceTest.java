package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.AnswerDto;
import com.quizzes.api.common.dto.ContextEventsResponseDto;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostRequestResourceDto;
import com.quizzes.api.common.dto.PostResponseResourceDto;
import com.quizzes.api.common.dto.ProfileEventResponseDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.model.entities.AssigneeEventEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.boot.json.JsonParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ContextEventService.class)
public class ContextEventServiceTest {

    @InjectMocks
    private ContextEventService contextEventService = Mockito.spy(ContextEventService.class);

    @Mock
    ContextProfileService contextProfileService;

    @Mock
    JsonParser jsonParser;

    @Mock
    ContextProfileEventService contextProfileEventService;

    @Mock
    ContextService contextService;

    @Mock
    ProfileService profileService;

    @Mock
    ContextRepository contextRepository;

    @Mock
    ResourceService resourceService;

    @Mock
    Gson gson = new Gson();

    @Mock
    AssigneeEventEntity assigneeEventEntity;

    @Test
    public void startContextEventWithEventsAndIsCompleteFalse() throws Exception {
        //Setting context
        UUID collectionId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);

        //Setting resource
        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        //Setting contextProfile
        UUID contextProfileId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(resourceId);
        contextProfile.setIsComplete(false);

        //Setting ContextProfileEvent
        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "someJson", null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        //Setting event return value
        Map<String, Object> mapResponse = new HashMap<>();
        mapResponse.put("answer", "[{\"value\":\"A\"]");
        mapResponse.put("reaction", 2);
        mapResponse.put("resourceId", resourceId);
        mapResponse.put("timeSpent", 123);
        mapResponse.put("score", 0);

        when(jsonParser.parseMap(any(String.class))).thenReturn(mapResponse);

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);
        when(resourceService.findFirstByContextIdOrderBySequence(any(UUID.class))).thenReturn(resource);
        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(contextId, UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(0)).findFirstByContextIdOrderBySequence(any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findByContextProfileId(any(UUID.class));
        verify(contextProfileEventService, times(0)).deleteByContextProfileId(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong context ID", contextId, result.getId());
        assertEquals("Wrong current resource ID", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertNull("Events doc is not empty", result.getEvents());
        assertEquals("Wrong size", 1, result.getEventsResponse().size());
        assertNotNull("Answer list is Null", result.getEventsResponse().get(0));
    }

    @Test
    public void startContextEventWhenContextProfileNull() throws Exception {
        //Setting context
        UUID collectionId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);

        //Setting resource
        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        //Setting ContextProfile
        UUID contextProfileId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(resourceId);
        List<ContextProfileEvent> list = new ArrayList<>();

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(null);
        when(resourceService.findFirstByContextIdOrderBySequence(any(UUID.class))).thenReturn(resource);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(contextProfile);

        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result = contextEventService.startContextEvent(contextId, UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(0)).findByContextProfileId(any(UUID.class));
        verify(contextProfileEventService, times(0)).deleteByContextProfileId(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong context ID", contextId, result.getId());
        assertEquals("Wrong current resource ID", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertNull("Events doc is not null", result.getEvents());
        assertEquals("Event list has wrong size", 0, result.getEventsResponse().size());
    }

    @Test
    public void startContextEventWithEventsAndIsCompleteTrue() throws Exception {
        //Setting context
        UUID collectionId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);

        //Setting resource
        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        //Setting ContextProfile
        UUID contextProfileId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(resourceId);
        contextProfile.setIsComplete(true);

        ContextProfileEvent contextProfileEvent =
                new ContextProfileEvent(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), "someJson", null);

        List<ContextProfileEvent> list = new ArrayList<>();
        list.add(contextProfileEvent);

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);
        when(resourceService.findFirstByContextIdOrderBySequence(any(UUID.class))).thenReturn(resource);
        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(list);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(contextProfile);

        StartContextEventResponseDto result = contextEventService.startContextEvent(contextId, UUID.randomUUID());

        verify(contextService, times(1)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(0)).findByContextProfileId(any(UUID.class));
        verify(contextProfileEventService, times(1)).deleteByContextProfileId(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong context ID", contextId, result.getId());
        assertEquals("Wrong current resource ID", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertNull("Events doc is not null", result.getEvents());
        assertEquals("Events list is not empty", 0, result.getEventsResponse().size());
    }

    @Test
    public void restartContextProfile() throws Exception {
        //Setting Resource
        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        //Setting ContextProfile
        UUID profileId = UUID.randomUUID();
        UUID contextId = UUID.randomUUID();
        UUID id = UUID.randomUUID();

        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(false);
        contextProfile.setContextId(contextId);
        contextProfile.setCurrentResourceId(resourceId);
        contextProfile.setProfileId(profileId);
        contextProfile.setId(id);

        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(contextProfile);
        when(resourceService.findFirstByContextIdOrderBySequence(any(UUID.class))).thenReturn(resource);

        ContextProfile result = WhiteboxImpl.invokeMethod(contextEventService, "restartContextProfile",
                contextProfile);

        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(any(UUID.class));

        assertNotNull("Response is Null", result);
        assertEquals("Wrong ID", id, result.getId());
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong profile ID", profileId, result.getProfileId());
        assertEquals("Wrong resource ID", resourceId, result.getCurrentResourceId());
        assertFalse("ContextProfile is not complete", result.getIsComplete());
    }

    @Test
    public void getFirstResourceByContextId() throws Exception {
        Resource result =
                WhiteboxImpl.invokeMethod(contextEventService, "findFirstResourceByContextId", UUID.randomUUID());
        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(any(UUID.class));
    }

    @Test
    public void onResourceEvent() throws Exception {
        //Params
        UUID contextId = UUID.randomUUID();
        UUID profileId = UUID.randomUUID();

        //Creating contextProfile
        UUID contextProfileId = UUID.randomUUID();
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);

        //Creating resource
        UUID resourceId = UUID.randomUUID();
        Resource resource = new Resource();
        resource.setId(resourceId);

        //Body values
        UUID bodyResourceId = UUID.randomUUID(); //Last resource id
        long timeSpent = 1000;
        int reaction = 3;
        String answerValue = "D";

        //Setting user answer
        List<AnswerDto> answers = new ArrayList<>();
        AnswerDto answer = new AnswerDto();
        answer.setValue(answerValue);
        answers.add(answer);

        //Setting resource
        PostRequestResourceDto resourceDto = new PostRequestResourceDto();
        resourceDto.setTimeSpent(timeSpent);
        resourceDto.setReaction(reaction);
        resourceDto.setResourceId(bodyResourceId);
        resourceDto.setAnswer(answers);

        //Previous resource pojo
        Resource previousResource = new Resource();
        previousResource.setId(bodyResourceId);
        previousResource.setResourceData("{\n" +
                "\t\"body\":\"Body\",\n" +
                "\t\"type\":\"single_choice\",\n" +
                "\t\"correctAnswer\":[{\"value\":\"D\"}],\n" +
                "\t\"title\":\"Body\",\n" +
                "\t\"interaction\": {\n" +
                "\t\t\"maxChoices\":0,\n" +
                "\t\t\"choices\":[\n" +
                "\t\t\t{\"value\":\"A\",\"sequence\":1,\"text\":\"A\",\"isFixed\":true},\n" +
                "\t\t\t{\"value\":\"D\",\"sequence\":2,\"text\":\"D\",\"isFixed\":true}\n" +
                "\t\t],\n" +
                "\t\t\"prompt\":\"\",\"shuffle\":false\n" +
                "\t}\n" +
                "}");

        //Setting body
        OnResourceEventPostRequestDto body = new OnResourceEventPostRequestDto();
        body.setPreviousResource(resourceDto);

        when(contextProfileService.findByContextIdAndProfileId(contextId, profileId)).thenReturn(contextProfile);
        when(resourceService.findById(bodyResourceId)).thenReturn(previousResource);
        when(resourceService.findById(resourceId)).thenReturn(resource);
        when(contextProfileEventService
                .findByContextProfileIdAndResourceId(contextProfileId, bodyResourceId)).thenReturn(null);

        contextEventService.onResourceEvent(contextId, resourceId, profileId, body);

        verify(contextProfileService, times(1)).findByContextIdAndProfileId(contextId, profileId);
        verify(resourceService, times(2)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1))
                .findByContextProfileIdAndResourceId(contextProfileId, bodyResourceId);
        verify(contextProfileEventService, times(1)).save(any(ContextProfileEvent.class));
    }

    @Test
    public void calculateScoreByQuestionTypeTrueFalseRightAnswer() throws Exception {
        String questionType = "true_false";

        //Same answer for both
        AnswerDto answer = new AnswerDto();
        answer.setValue("A");

        List<AnswerDto> userAnswers = Arrays.asList(answer);
        List<AnswerDto> correctAnswers = Arrays.asList(answer);

        int result =
                WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                        questionType, userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreByQuestionTypeTrueFalseWrongAnswer() throws Exception {
        String questionType = "true_false";

        AnswerDto userAnswer = new AnswerDto();
        userAnswer.setValue("A");
        List<AnswerDto> userAnswers = Arrays.asList(userAnswer);

        AnswerDto correctAnswer = new AnswerDto();
        correctAnswer.setValue("B");
        List<AnswerDto> correctAnswers = Arrays.asList(correctAnswer);

        int result =
                WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                        questionType, userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreByQuestionTypeSingleChoiceRightAnswer() throws Exception {
        String questionType = "single_choice";

        //Same answer for both
        AnswerDto answer = new AnswerDto();
        answer.setValue("A");

        List<AnswerDto> userAnswers = Arrays.asList(answer);
        List<AnswerDto> correctAnswers = Arrays.asList(answer);

        int result =
                WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                        questionType, userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreByQuestionTypeSingleChoiceWrongAnswer() throws Exception {
        String questionType = "single_choice";

        AnswerDto userAnswer = new AnswerDto();
        userAnswer.setValue("A");
        List<AnswerDto> userAnswers = Arrays.asList(userAnswer);

        AnswerDto correctAnswer = new AnswerDto();
        correctAnswer.setValue("B");
        List<AnswerDto> correctAnswers = Arrays.asList(correctAnswer);

        int result =
                WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                        questionType, userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreForSimpleOptionRightAnswer() throws Exception {
        String userAnswer = "A";
        String correctAnswer = "A";

        int result =
                WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswer, correctAnswer);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreForSimpleOptionWrongAnswer() throws Exception {
        String userAnswer = "A";
        String correctAnswer = "B";

        int result =
                WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswer, correctAnswer);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void finishContextEvent() throws Exception {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(false);

        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        contextEventService.finishContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
    }

    @Test
    public void finishContextEventDoNothing() throws Exception {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(true);

        when(contextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class))).thenReturn(contextProfile);

        contextEventService.finishContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
    }

    @Test
    public void getContextEvents() throws Exception {
        //Map values for findAllContextEvents
        Map<UUID, List<AssigneeEventEntity>> contextEventsMap = new HashMap<>();
        List<AssigneeEventEntity> events = new ArrayList<>();

        //Setting events
        UUID currentResourceId = UUID.randomUUID();

        //Body values
        UUID bodyResourceId = UUID.randomUUID(); //Last resource id
        long timeSpent = 1000;
        int reaction = 3;
        int score = 100;
        String answerValue = "D";

        String eventData = "{\n" +
                "\t\t\"resourceId\": \"" + bodyResourceId + "\",\n" +
                "\t\t\"timeSpent\": " + timeSpent + ",\n" +
                "\t\t\"reaction\": " + reaction + ",\n" +
                "\t\t\"score\": " + score + ",\n" +
                "\t\t\"answer\": [\n" +
                "\t\t\t{\"value\":\"" + answerValue + "\"}\n" +
                "\t\t]\n" +
                "\t}";

        //Setting entity values
        AssigneeEventEntity assigneeEventEntity = Mockito.spy(AssigneeEventEntity.class);
        when(assigneeEventEntity.getCurrentResourceId()).thenReturn(currentResourceId);
        when(assigneeEventEntity.getEventData()).thenReturn(eventData);
        events.add(assigneeEventEntity);

        //Adding students
        UUID assigneeId1 = UUID.randomUUID();
        contextEventsMap.put(assigneeId1, events);

        //Setting context
        Context contextMock = new Context();
        UUID contextId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        contextMock.setId(contextId);
        contextMock.setCollectionId(collectionId);

        when(contextProfileEventService.findByContextId(contextId)).thenReturn(contextEventsMap);
        when(contextService.findById(contextId)).thenReturn(contextMock);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        verify(contextService, times(1)).findById(contextId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 1, result.getProfileEvents().size());

        ProfileEventResponseDto profileResult1 = result.getProfileEvents().get(0);
        assertEquals("Wrong event size for assigneeId1", 1, profileResult1.getEvents().size());
        assertEquals("Wrong profile ID for assigneeId1", assigneeId1, profileResult1.getProfileId());
        assertEquals("Wrong current resource", currentResourceId, profileResult1.getCurrentResourceId());

        PostResponseResourceDto eventResult = profileResult1.getEvents().get(0);
        assertEquals("Wrong reaction", 3, eventResult.getReaction());
        assertEquals("Wrong timeSpent", 1000, eventResult.getTimeSpent());
        assertEquals("Wrong resource Id", bodyResourceId, eventResult.getResourceId());
        assertEquals("Wrong score", 100, eventResult.getScore());
        assertEquals("Wrong answer value", answerValue, eventResult.getAnswer().get(0).getValue());
    }

    @Test
    public void getContextEventsWithoutEvents() throws Exception {
        //Map values for findAllContextEvents
        Map<UUID, List<AssigneeEventEntity>> contextEventsMap = new HashMap<>();
        List<AssigneeEventEntity> events = new ArrayList<>();

        //Setting events
        UUID currentResourceId = UUID.randomUUID();

        //Setting entity values
        AssigneeEventEntity assigneeEventEntity = Mockito.spy(AssigneeEventEntity.class);
        when(assigneeEventEntity.getCurrentResourceId()).thenReturn(currentResourceId);
        when(assigneeEventEntity.getEventData()).thenReturn(null);
        events.add(assigneeEventEntity);

        //Adding student
        UUID assigneeId = UUID.randomUUID();
        contextEventsMap.put(assigneeId, events);

        //Setting context
        Context contextMock = new Context();
        UUID contextId = UUID.randomUUID();
        UUID collectionId = UUID.randomUUID();
        contextMock.setId(contextId);
        contextMock.setCollectionId(collectionId);

        when(contextProfileEventService.findByContextId(contextId)).thenReturn(contextEventsMap);
        when(contextService.findById(contextId)).thenReturn(contextMock);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        verify(contextService, times(1)).findById(contextId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 1, result.getProfileEvents().size());

        ProfileEventResponseDto profileResult = result.getProfileEvents().get(0);
        assertEquals("Wrong event size", 0, profileResult.getEvents().size());
        assertEquals("Wrong profile ID", assigneeId, profileResult.getProfileId());
        assertEquals("Wrong current resource", currentResourceId, profileResult.getCurrentResourceId());
    }

}