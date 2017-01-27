package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.AssessmentMetadataDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.InteractionDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.rest.clients.AssessmentRestClient;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AssessmentService.class)
public class AssessmentServiceTest {

    @InjectMocks
    private AssessmentService collectionService = spy(new AssessmentService());

    @Mock
    private AssessmentRestClient assessmentRestClient;

    @Mock
    private AuthenticationRestClient authenticationRestClient;

    private String assessmentId;
    private UUID resourceId;
    private String assessmentTitle;
    private String resourceTitle;
    private String trueFalseQuestion;
    private String token;

    @Before
    public void before() throws Exception {
        assessmentId = UUID.randomUUID().toString();
        resourceId = UUID.randomUUID();
        assessmentTitle = "Assessment title";
        resourceTitle = "Resource title";
        trueFalseQuestion = "true_false";
        token =  "token-id";
    }

    @Test
    public void getAssessment() throws Exception {
        CollectionDto collectionDto = createCollectionDto();
        AssessmentContentDto assessmentContentDto = new AssessmentContentDto();

        when(authenticationRestClient.generateAnonymousToken()).thenReturn(token);
        when(assessmentRestClient.getAssessment(assessmentId, token)).thenReturn(assessmentContentDto);
        doReturn(collectionDto).when(collectionService, "convertGooruAssessmentToQuizzesFormat",
                assessmentContentDto, assessmentId);

        CollectionDto result = collectionService.getAssessment(assessmentId);

        verify(authenticationRestClient, times(1)).generateAnonymousToken();
        verify(assessmentRestClient, times(1)).getAssessment(assessmentId, token);
        verifyPrivate(collectionService, times(1)).invoke("convertGooruAssessmentToQuizzesFormat",
                assessmentContentDto, assessmentId);

        assertEquals("Wrong assessment ID", assessmentId, result.getId());
        assertEquals("Wrong assessment title", assessmentTitle, result.getMetadata().getTitle());
        assertEquals("Wrong number of resources", 1, result.getResources().size());

        ResourceDto resourceResult = result.getResources().get(0);
        assertEquals("Wrong assessment ID", resourceId, resourceResult.getId());
        assertFalse("IsResource is true ", resourceResult.getIsResource());
        assertEquals("Wrong sequence", 1, resourceResult.getSequence());

        ResourceMetadataDto metadataResource = resourceResult.getQuestionData();
        assertEquals("Wrong title", resourceTitle, metadataResource.getTitle());
        assertEquals("Wrong body text", resourceTitle, metadataResource.getBody());
        assertEquals("Wrong number of correct answers", 1, metadataResource.getCorrectAnswer().size());

        InteractionDto interactionResult = metadataResource.getInteraction();
        assertEquals("Wrong number of maxChoices", 0, interactionResult.getMaxChoices());
        assertEquals("Wrong prompt value", "", interactionResult.getPrompt());
        assertFalse("Shuffle is true", interactionResult.getIsShuffle());
        assertEquals("Wrong number of choices", 2, interactionResult.getChoices().size());
    }

    private CollectionDto createCollectionDto() {
        AssessmentMetadataDto metadata = new AssessmentMetadataDto();
        metadata.setTitle(assessmentTitle);

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId);
        collectionDto.setMetadata(metadata);

        List<ResourceDto> resources = new ArrayList<>();
        resources.add(createResourceDto(resourceId, false, 1, createResourceMetadataDto()));

        collectionDto.setResources(resources);

        return collectionDto;
    }

    private ResourceMetadataDto createResourceMetadataDto() {
        ResourceMetadataDto metadata = new ResourceMetadataDto();
        metadata.setTitle(resourceTitle);
        metadata.setType(trueFalseQuestion);
        metadata.setCorrectAnswer(Arrays.asList(new AnswerDto("A")));
        metadata.setInteraction(createInteractionDto());
        metadata.setBody(resourceTitle);
        return metadata;
    }

    private InteractionDto createInteractionDto() {
        InteractionDto interactionDto = new InteractionDto();
        interactionDto.setPrompt("");
        interactionDto.setShuffle(false);
        interactionDto.setMaxChoices(0);
        interactionDto.setChoices(Arrays.asList(
                createChoiceDto(true, "text", "A", 1), createChoiceDto(true, "text2", "B", 2)));
        return interactionDto;
    }

    private ChoiceDto createChoiceDto(boolean fixed, String text, String value, int sequence) {
        ChoiceDto choiceDto = new ChoiceDto();
        choiceDto.setFixed(fixed);
        choiceDto.setText(text);
        choiceDto.setValue(value);
        choiceDto.setSequence(sequence);
        return choiceDto;
    }

    private ResourceDto createResourceDto(UUID id, boolean isResource, int sequence, ResourceMetadataDto metadata) {
        ResourceDto resourceDto = new ResourceDto();
        resourceDto.setId(id);
        resourceDto.setIsResource(isResource);
        resourceDto.setSequence(sequence);
        resourceDto.setQuestionData(metadata);
        return resourceDto;
    }

}