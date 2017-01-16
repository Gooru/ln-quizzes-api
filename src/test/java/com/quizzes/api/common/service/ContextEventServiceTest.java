package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.AnswerDto;
import com.quizzes.api.common.dto.ContextEventsResponseDto;
import com.quizzes.api.common.dto.EventSummaryDataDto;
import com.quizzes.api.common.dto.OnResourceEventPostRequestDto;
import com.quizzes.api.common.dto.PostRequestResourceDto;
import com.quizzes.api.common.dto.PostResponseResourceDto;
import com.quizzes.api.common.dto.ProfileEventResponseDto;
import com.quizzes.api.common.dto.QuestionDataDto;
import com.quizzes.api.common.dto.ResourceDto;
import com.quizzes.api.common.dto.StartContextEventResponseDto;
import com.quizzes.api.common.exception.ContentNotFoundException;
import com.quizzes.api.common.exception.InvalidOwnerException;
import com.quizzes.api.common.model.entities.AssigneeEventEntity;
import com.quizzes.api.common.model.jooq.tables.pojos.Context;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.ContextProfileEvent;
import com.quizzes.api.common.model.jooq.tables.pojos.CurrentContextProfile;
import com.quizzes.api.common.model.jooq.tables.pojos.Resource;
import com.quizzes.api.common.repository.ContextRepository;
import com.quizzes.api.common.service.messaging.ActiveMQClientService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.testng.AssertJUnit.assertEquals;

@RunWith(PowerMockRunner.class)
public class ContextEventServiceTest {

    @InjectMocks
    private ContextEventService contextEventService = spy(ContextEventService.class);

    @Mock
    private ContextProfileService contextProfileService;

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
    AssigneeEventEntity assigneeEventEntity;

    @Mock
    CurrentContextProfileService currentContextProfileService;

    @Mock
    ActiveMQClientService activeMQClientService;

    @Mock
    Gson gson = new Gson();

    @Mock
    JsonParser jsonParser;

    private UUID collectionId;
    private UUID contextId;
    private UUID resourceId;
    private UUID contextProfileId;
    private UUID ownerId;
    private UUID profileId;
    private CurrentContextProfile currentContextProfile;

    @Before
    public void beforeEachTest() {
        collectionId = UUID.randomUUID();
        contextId = UUID.randomUUID();
        resourceId = UUID.randomUUID();
        contextProfileId = UUID.randomUUID();
        ownerId = UUID.randomUUID();
        profileId = UUID.randomUUID();
        currentContextProfile = new CurrentContextProfile();
    }

    @Test
    public void startContextEventWithCurrentContextProfileFalse() throws Exception {
        Context context = createContext();
        Resource resource = createResource();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(false);
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        //Setting Answers
        List<AnswerDto> answers = new ArrayList<>();
        answers.add(new AnswerDto("A"));
        //Setting Events
        PostResponseResourceDto resourceDto1 = createResponseResourceDto(UUID.randomUUID(), 0, 0, 1234, answers, false);
        PostResponseResourceDto resourceDto2 = createResponseResourceDto(UUID.randomUUID(), 0, 0, 1234,
                new ArrayList<>(), false);
        ContextProfileEvent contextProfileEvent1 = createContextProfileEvent(contextProfileId, resourceId,
                gson.toJson(resourceDto1));
        ContextProfileEvent contextProfileEvent2 = createContextProfileEvent(contextProfileId, resourceId,
                gson.toJson(resourceDto2));

        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(contextProfileEvent1);
        contextProfileEvents.add(contextProfileEvent2);

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(contextProfileService.findById(any(UUID.class))).thenReturn(contextProfile);
        when(currentContextProfileService.findByContextIdAndProfileId(any(UUID.class), any(UUID.class)))
                .thenReturn(currentContextProfile);
        when(resourceService.findFirstByContextIdOrderBySequence(any(UUID.class))).thenReturn(resource);
        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(contextProfileEvents);

        StartContextEventResponseDto result =
                contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findById(contextId);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(eq(contextId), eq(profileId));
        verify(contextProfileService, times(1)).findById(eq(currentContextProfile.getContextProfileId()));
        verify(resourceService, times(0)).findFirstByContextIdOrderBySequence(any(UUID.class));
        verify(contextProfileService, times(0)).save(any(ContextProfile.class));
        verify(currentContextProfileService, times(0)).create(any(CurrentContextProfile.class));
        verify(contextProfileEventService, times(1)).findByContextProfileId(any(UUID.class));
        verify(activeMQClientService, times(1)).sendStartContextEventMessage(any(), any(), any());

        assertNotNull("Response is Null", result);
        assertEquals("Wrong context ID", contextId, result.getId());
        assertEquals("Wrong current resource ID", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size", 2, result.getEvents().size());

        PostResponseResourceDto result1 = result.getEvents().get(0);
        assertEquals("Wrong result1 resource1 ID", resource1, result1.getResourceId());
        assertEquals("Wrong score for result1", 0, result1.getScore());
        assertEquals("Wrong reaction for result1", 0, result1.getReaction());
        assertEquals("Wrong timeSpent for result1", 1234, result1.getTimeSpent());
        assertEquals("Wrong timeSpent for result1", "A", result1.getAnswer().get(0).getValue());
        assertFalse("IsSkipped is true in result1", result1.getIsSkipped());

        PostResponseResourceDto result2 = result.getEvents().get(1);
        assertEquals("Wrong result1 resource2 ID", resource2, result2.getResourceId());
        assertEquals("Wrong score for result2", 0, result2.getScore());
        assertEquals("Wrong reaction for result2", 0, result2.getReaction());
        assertEquals("Wrong timeSpent for result2", 1234, result2.getTimeSpent());
        assertTrue("Answer list is not empty for result2", result2.getAnswer().isEmpty());
        assertTrue("IsSkipped is true in result2", result2.getIsSkipped());
    }

    @Test
    public void startContextEventWithCurrentContextProfileTrue() throws Exception {
        Context context = createContext();
        Resource resource = createResource();
        ContextProfile contextProfile = createContextProfile();
        contextProfile.setIsComplete(false);
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();

        when(contextService.findById(contextId)).thenReturn(context);
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(currentContextProfile.getContextProfileId()))
                .thenReturn(contextProfile);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(contextProfile);
        when(resourceService.findFirstByContextIdOrderBySequence(any(UUID.class))).thenReturn(resource);

        StartContextEventResponseDto result =
                contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findById(contextId);
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(eq(contextId), eq(profileId));
        verify(contextProfileService, times(1)).findById(eq(currentContextProfile.getContextProfileId()));
        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(currentContextProfileService, times(0)).save(any(CurrentContextProfile.class));
        verify(currentContextProfileService, times(1)).startAttempt(any(CurrentContextProfile.class));
        verify(activeMQClientService, times(1)).sendStartContextEventMessage(any(), any(), any());

        assertNotNull("Response is Null", result);
        assertEquals("Wrong context ID", contextId, result.getId());
        assertEquals("Wrong current resource ID", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size", 0, result.getEvents().size());
    }

    @Test
    public void startContextEventWhenCurrentContextProfileNull() throws Exception {
        //Setting context
        Context context = createContext();

        //Setting resource
        Resource resource = new Resource();
        resource.setId(resourceId);

        //Setting ContextProfile
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(resourceId);
        List<ContextProfileEvent> list = new ArrayList<>();

        when(contextService.findById(any(UUID.class))).thenReturn(context);
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenThrow(ContentNotFoundException.class);
        when(resourceService.findFirstByContextIdOrderBySequence(any(UUID.class))).thenReturn(resource);
        when(contextProfileService.save(any(ContextProfile.class))).thenReturn(contextProfile);

        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(list);

        StartContextEventResponseDto result =
                contextEventService.processStartContextEvent(contextId, profileId);

        verify(contextService, times(1)).findById(eq(contextId));
        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(eq(contextId), eq(profileId));
        verify(contextProfileService, times(0)).findById(any(UUID.class));
        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(eq(contextId));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(currentContextProfileService, times(1)).save(any(CurrentContextProfile.class));
        verify(activeMQClientService, times(1)).sendStartContextEventMessage(any(), any(), any());

        assertNotNull("Response is Null", result);
        assertEquals("Wrong context ID", contextId, result.getId());
        assertEquals("Wrong current resource ID", resourceId, result.getCurrentResourceId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Event list has wrong size", 0, result.getEvents().size());
    }

    @Test
    public void getFirstResourceByContextId() throws Exception {
        Resource result =
                WhiteboxImpl.invokeMethod(contextEventService, "findFirstResourceByContextId", UUID.randomUUID());
        verify(resourceService, times(1)).findFirstByContextIdOrderBySequence(any(UUID.class));
    }

    @Test
    public void onResourceEvent() throws Exception {
        //Creating contextProfile
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);

        //Creating resource
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

        CurrentContextProfile currentContextProfile = createCurrentContextProfile();

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);
        when(contextProfileService.findById(currentContextProfile.getContextProfileId())).thenReturn(contextProfile);
        when(resourceService.findById(bodyResourceId)).thenReturn(previousResource);
        when(resourceService.findById(resourceId)).thenReturn(resource);
        when(contextProfileEventService
                .findByContextProfileIdAndResourceId(contextProfileId, bodyResourceId)).thenReturn(null);

        contextEventService.processOnResourceEvent(contextId, profileId, resourceId, body);

        ArgumentCaptor<ContextProfile> contextProfileArgument = ArgumentCaptor.forClass(ContextProfile.class);
        verify(contextEventService, times(1)).doOnResourceEventTransaction(contextProfileArgument.capture(),
                any(ContextProfileEvent.class));
        //contextProfileArgument.getValue().getProfileId()

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(eq(contextId), eq(profileId));
        verify(contextProfileService, times(1)).findById(currentContextProfile.getContextProfileId());
        verify(resourceService, times(2)).findById(any(UUID.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
        verify(contextProfileEventService, times(1)).findByContextProfileId(contextProfileId);
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
        AnswerDto answer = new AnswerDto("A");
        List<AnswerDto> userAnswers = Arrays.asList(answer);
        List<AnswerDto> correctAnswers = Arrays.asList(answer);

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType", questionType,
                userAnswers, correctAnswers);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreByQuestionTypeSingleChoiceWrongAnswer() throws Exception {
        String questionType = "single_choice";

        AnswerDto userAnswer = new AnswerDto();
        userAnswer.setValue("A");
        List<AnswerDto> userAnswers = Arrays.asList(userAnswer);

        AnswerDto correctAnswer = new AnswerDto();
        correctAnswer.setValue("b");
        List<AnswerDto> correctAnswers = Arrays.asList(correctAnswer);

        int result =
                WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreByQuestionType",
                        questionType, userAnswers, correctAnswers);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void calculateScoreForSimpleOptionRightAnswer() throws Exception {
        String userAnswer = "A";
        String correctAnswer = "a";

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswer,
                correctAnswer);
        assertEquals("Score should be 100", 100, result);
    }

    @Test
    public void calculateScoreForSimpleOptionWrongAnswer() throws Exception {
        String userAnswer = "A";
        String correctAnswer = "B";

        int result = WhiteboxImpl.invokeMethod(contextEventService, "calculateScoreForSimpleOption", userAnswer,
                correctAnswer);
        assertEquals("Score should be 0", 0, result);
    }

    @Test
    public void finishContextEvent() throws Exception {
        CurrentContextProfile currentContextProfile = createCurrentContextProfile();
        
        // TODO Fix me
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setIsComplete(false);
        contextProfile.setId(UUID.randomUUID());
        UUID resourceId1 = UUID.randomUUID();
        UUID resourceId2 = UUID.randomUUID();
        UUID resourceId3 = UUID.randomUUID();
        UUID resourceId4 = UUID.randomUUID();
        UUID resourceId5 = UUID.randomUUID();

        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenReturn(currentContextProfile);

        contextEventService.processFinishContextEvent(contextId, profileId);
        
        // TODO Fix me
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();

        ContextProfileEvent contextProfileEvent1 = new ContextProfileEvent();
        contextProfileEvent1.setId(UUID.randomUUID());
        contextProfileEvent1.setContextProfileId(contextProfile.getId());
        contextProfileEvent1.setResourceId(resourceId1);
        PostRequestResourceDto eventData1 =  new PostRequestResourceDto();
        eventData1.setIsSkipped(false);
        eventData1.setScore(100);
        eventData1.setReaction(1);
        eventData1.setResourceId(resourceId1);
        eventData1.setTimeSpent(12000);
        List<AnswerDto> answerDtosEvent1 = new ArrayList<>();
        AnswerDto answerDtoEvent1 = new AnswerDto();
        answerDtoEvent1.setValue("A");
        answerDtosEvent1.add(answerDtoEvent1);
        eventData1.setAnswer(answerDtosEvent1);
        contextProfileEvent1.setEventData(gson.toJson(eventData1, PostRequestResourceDto.class));

        ContextProfileEvent contextProfileEvent2 = new ContextProfileEvent();
        contextProfileEvent2.setId(UUID.randomUUID());
        contextProfileEvent2.setContextProfileId(contextProfile.getId());
        contextProfileEvent2.setResourceId(resourceId2);
        PostRequestResourceDto eventData2 =  new PostRequestResourceDto();
        eventData2.setIsSkipped(true);
        eventData2.setScore(0);
        eventData2.setReaction(1);
        eventData2.setResourceId(resourceId2);
        eventData2.setTimeSpent(11000);
        List<AnswerDto> answerDtosEvent2 = new ArrayList<>();// skipped so no answers
        eventData2.setAnswer(answerDtosEvent2);
        contextProfileEvent2.setEventData(gson.toJson(eventData2, PostRequestResourceDto.class));

        ContextProfileEvent contextProfileEvent3 = new ContextProfileEvent();
        contextProfileEvent3.setId(UUID.randomUUID());
        contextProfileEvent3.setContextProfileId(contextProfile.getId());
        contextProfileEvent3.setResourceId(resourceId3);
        PostRequestResourceDto eventData3 =  new PostRequestResourceDto();
        eventData3.setIsSkipped(false);
        eventData3.setScore(0);
        eventData3.setReaction(3);
        eventData3.setResourceId(resourceId3);
        eventData3.setTimeSpent(10000);
        List<AnswerDto> answerDtosEvent3 = new ArrayList<>();
        AnswerDto answerDtoEvent3 = new AnswerDto();
        answerDtoEvent3.setValue("C");
        answerDtosEvent3.add(answerDtoEvent3);
        eventData3.setAnswer(answerDtosEvent3);
        contextProfileEvent3.setEventData(gson.toJson(eventData3, PostRequestResourceDto.class));

        contextProfileEvents.add(contextProfileEvent1);
        contextProfileEvents.add(contextProfileEvent2);
        contextProfileEvents.add(contextProfileEvent3);

        when(contextProfileEventService.findByContextProfileId(any(UUID.class))).thenReturn(contextProfileEvents);

        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);
        when(contextService.findById(any(UUID.class))).thenReturn(context);

        List<Resource> resources = new ArrayList<>();
        Resource resource1 = new Resource();
        resource1.setCollectionId(collectionId);
        resource1.setId(resourceId1);
        resource1.setIsDeleted(false);
        resource1.setSequence((short) 1);
        resources.add(resource1);

        Resource resource2 = new Resource();
        resource2.setCollectionId(collectionId);
        resource2.setId(resourceId2);
        resource2.setIsDeleted(false);
        resource2.setSequence((short)2);
        resources.add(resource2);

        Resource resource3 = new Resource();
        resource3.setCollectionId(collectionId);
        resource3.setId(resourceId3);
        resource3.setIsDeleted(false);
        resource3.setSequence((short)3);
        resources.add(resource3);

        Resource resource4 = new Resource();
        resource4.setCollectionId(collectionId);
        resource4.setId(resourceId4);
        resource4.setIsDeleted(false);
        resource4.setSequence((short)4);
        resources.add(resource4);

        Resource resource5 = new Resource();
        resource5.setCollectionId(collectionId);
        resource5.setId(resourceId5);
        resource5.setIsDeleted(false);
        resource5.setSequence((short) 5);
        resources.add(resource5);

        when(resourceService.findByCollectionId(any(UUID.class))).thenReturn(resources);

        contextEventService.processFinishContextEvent(UUID.randomUUID(), UUID.randomUUID());

        verify(currentContextProfileService, times(1)).findByContextIdAndProfileId(eq(contextId), eq(profileId));
        verify(currentContextProfileService, times(1)).finish(eq(currentContextProfile));
        verify(activeMQClientService, times(1)).sendFinishContextEventMessage(any(), any(), any());
        verify(contextProfileService, times(1)).findByContextIdAndProfileId(any(UUID.class), any(UUID.class));
        verify(contextProfileEventService, times(1)).findByContextProfileId(any(UUID.class));
        verify(contextService, times(1)).findById(any(UUID.class));
        verify(resourceService, times(1)).findByCollectionId(any(UUID.class));
        //there are 3 events but 2 are already saved, here we save 1 skipped event plus 2 more just created
        verify(contextProfileEventService, times(3)).save(any(ContextProfileEvent.class));
        verify(contextProfileService, times(1)).save(any(ContextProfile.class));
    }

    @Test(expected = ContentNotFoundException.class)
    public void finishContextEventDoNothing() throws Exception {
        when(currentContextProfileService.findByContextIdAndProfileId(contextId, profileId))
                .thenThrow(ContentNotFoundException.class);

        contextEventService.processFinishContextEvent(contextId, profileId);
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
        boolean isSkipped = false;
        String answerValue = "D";

        String eventData = "{\n" +
                "\t\t\"resourceId\": \"" + bodyResourceId + "\",\n" +
                "\t\t\"timeSpent\": " + timeSpent + ",\n" +
                "\t\t\"reaction\": " + reaction + ",\n" +
                "\t\t\"score\": " + score + ",\n" +
                "\t\t\"isSkipped\": " + isSkipped + ",\n" +
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
        Context context = createContext();

        when(contextProfileEventService.findByContextId(contextId)).thenReturn(contextEventsMap);
        when(contextService.findByIdAndOwnerId(contextId, ownerId)).thenReturn(context);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId, ownerId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        verify(contextService, times(1)).findByIdAndOwnerId(contextId, ownerId);

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
        assertFalse("Wrong isSkipped value", eventResult.getIsSkipped());
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
        Context context = createContext();

        when(contextProfileEventService.findByContextId(contextId)).thenReturn(contextEventsMap);
        when(contextService.findByIdAndOwnerId(contextId, ownerId)).thenReturn(context);

        ContextEventsResponseDto result = contextEventService.getContextEvents(contextId, ownerId);

        verify(contextProfileEventService, times(1)).findByContextId(contextId);
        verify(contextService, times(1)).findByIdAndOwnerId(contextId, ownerId);

        assertNotNull("Result is null", result);
        assertEquals("Wrong context ID", contextId, result.getContextId());
        assertEquals("Wrong collection ID", collectionId.toString(), result.getCollection().getId());
        assertEquals("Wrong size of events ID", 1, result.getProfileEvents().size());

        ProfileEventResponseDto profileResult = result.getProfileEvents().get(0);
        assertEquals("Wrong event size", 0, profileResult.getEvents().size());
        assertEquals("Wrong profile ID", assigneeId, profileResult.getProfileId());
        assertEquals("Wrong current resource", currentResourceId, profileResult.getCurrentResourceId());
    }

    @Test(expected = ContentNotFoundException.class)
    public void getContextEventsThrowsContentNotFoundException() {
        when(contextService.findByIdAndOwnerId(any(UUID.class), any(UUID.class)))
                .thenThrow(ContentNotFoundException.class);
        contextEventService.getContextEvents(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test(expected = InvalidOwnerException.class)
    public void getContextEventsThrowsInvalidOwnerException() {
        when(contextService.findByIdAndOwnerId(any(UUID.class), any(UUID.class)))
                .thenThrow(InvalidOwnerException.class);
        contextEventService.getContextEvents(UUID.randomUUID(), UUID.randomUUID());
    }

    @Test
    public void calculateEventSummaryDataSkipTrue() throws Exception {
        List<ContextProfileEvent> contextProfileEvents = createContextProfileEvents();

        EventSummaryDataDto eventSummaryDataDto = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateEventSummary",
                contextProfileEvents, true);

        assertNotNull("ContextProfile EventSummaryData is null", eventSummaryDataDto);
        assertEquals(eventSummaryDataDto.getAverageScore(), 60);
        assertEquals(eventSummaryDataDto.getTotalTimeSpent(), 50);
        assertEquals(eventSummaryDataDto.getAverageReaction(), 2);
        assertEquals(eventSummaryDataDto.getTotalCorrect(), 3);
        assertEquals(eventSummaryDataDto.getTotalAnswered(), 5);
    }

    @Test
    public void calculateEventSummaryDataSkipFalse() throws Exception {
        List<ContextProfileEvent> contextProfileEvents = createContextProfileEvents();

        EventSummaryDataDto eventSummaryDataDto = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateEventSummary",
                contextProfileEvents, false);

        assertNotNull("ContextProfile EventSummaryData is null", eventSummaryDataDto);
        assertEquals(eventSummaryDataDto.getAverageScore(), 75);
        assertEquals(eventSummaryDataDto.getTotalTimeSpent(), 50);
        assertEquals(eventSummaryDataDto.getAverageReaction(), 2);
        assertEquals(eventSummaryDataDto.getTotalCorrect(), 3);
        assertEquals(eventSummaryDataDto.getTotalAnswered(), 4);
    }

    @Test
    public void calculateEventSummaryDataForEmptyEventList() throws Exception {
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        EventSummaryDataDto eventSummaryDataDto = WhiteboxImpl.invokeMethod(contextEventService,
                "calculateEventSummary",
                contextProfileEvents, false);

        assertEquals(eventSummaryDataDto.getAverageScore(), 0);
        assertEquals(eventSummaryDataDto.getTotalTimeSpent(), 0);
        assertEquals(eventSummaryDataDto.getAverageReaction(), 0);
        assertEquals(eventSummaryDataDto.getTotalCorrect(), 0);
        assertEquals(eventSummaryDataDto.getTotalAnswered(), 0);
    }

    private List<ContextProfileEvent> createContextProfileEvents() {
        UUID contextProfileId = UUID.randomUUID();
        ContextProfileEvent event1 = new ContextProfileEvent();
        event1.setId(UUID.randomUUID());
        event1.setContextProfileId(contextProfileId);
        event1.setResourceId(UUID.randomUUID());
        event1.setEventData("{\"timeSpent\":\"10\"," +
                " \"reaction\":\"2\"," +
                " \"answer\":[{\"value\":\"A\"}]," +
                " \"score\":\"100\"," +
                " \"isSkipped\":\"false\"}");
        ContextProfileEvent event2 = new ContextProfileEvent();
        event2.setId(UUID.randomUUID());
        event2.setContextProfileId(contextProfileId);
        event2.setResourceId(UUID.randomUUID());
        event2.setEventData("{\"timeSpent\":\"16\"," +
                " \"reaction\":\"1\"," +
                " \"answer\":[{\"value\":\"B\"}]," +
                " \"score\":\"100\"," +
                " \"isSkipped\":\"false\"}");
        ContextProfileEvent event3 = new ContextProfileEvent();
        event3.setId(UUID.randomUUID());
        event3.setContextProfileId(contextProfileId);
        event3.setResourceId(UUID.randomUUID());
        event3.setEventData("{\"timeSpent\":\"15\"," +
                " \"reaction\":\"5\"," +
                " \"answer\":[{\"value\":\"C\"}]," +
                " \"score\":\"0\"," +
                " \"isSkipped\":\"false\"}");
        ContextProfileEvent event4 = new ContextProfileEvent();
        event4.setId(UUID.randomUUID());
        event4.setContextProfileId(contextProfileId);
        event4.setResourceId(UUID.randomUUID());
        event4.setEventData(
                "{" +
                "   'timeSpent': 1," +
                "   'reaction': 0," +
                "   'answer': []," +
                "   'score': 0," +
                "   'isSkipped': true" +
                "}");
        ContextProfileEvent event5 = new ContextProfileEvent();
        event5.setId(UUID.randomUUID());
        event5.setContextProfileId(contextProfileId);
        event5.setResourceId(UUID.randomUUID());
        event5.setEventData("{\"timeSpent\":\"8\"," +
                " \"reaction\":\"3\"," +
                " \"answer\":[{\"value\":\"A\"}]," +
                " \"score\":\"100\"," +
                " \"isSkipped\":\"false\"}");
        List<ContextProfileEvent> contextProfileEvents = new ArrayList<>();
        contextProfileEvents.add(event1);
        contextProfileEvents.add(event2);
        contextProfileEvents.add(event3);
        contextProfileEvents.add(event4);
        contextProfileEvents.add(event5);

        return contextProfileEvents;
    }

    private Context createContext() {
        Context context = new Context();
        context.setId(contextId);
        context.setCollectionId(collectionId);
        context.setContextData("{}");
        return context;
    }

    private ContextProfile createContextProfile() {
        ContextProfile contextProfile = new ContextProfile();
        contextProfile.setId(contextProfileId);
        contextProfile.setCurrentResourceId(resourceId);
        return contextProfile;
    }

    private CurrentContextProfile createCurrentContextProfile() {
        currentContextProfile.setContextId(contextId);
        currentContextProfile.setProfileId(profileId);
        currentContextProfile.setContextProfileId(contextProfileId);
        return currentContextProfile;
    }

    private PostResponseResourceDto createResponseResourceDto(UUID resourceId, int score, int reaction, long timeSpent,
                                                              List<AnswerDto> answers, boolean isSkipped) {
        PostResponseResourceDto resourceDto = new PostResponseResourceDto();
        resourceDto.setResourceId(resourceId);
        resourceDto.setScore(score);
        resourceDto.setReaction(reaction);
        resourceDto.setTimeSpent(timeSpent);
        resourceDto.setAnswer(answers);
        resourceDto.setIsSkipped(isSkipped);
        return resourceDto;
    }

    private ContextProfileEvent createContextProfileEvent(UUID contextProfileId, UUID resourceId, String evenData) {
        ContextProfileEvent contextProfileEvent = new ContextProfileEvent();
        contextProfileEvent.setId(UUID.randomUUID());
        contextProfileEvent.setContextProfileId(contextProfileId);
        contextProfileEvent.setResourceId(resourceId);
        contextProfileEvent.setEventData(evenData);
        return contextProfileEvent;
    }

    private Resource createResource() {
        Resource resource = new Resource();
        resource.setId(resourceId);
        return resource;
    }

}