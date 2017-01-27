package com.quizzes.api.core.services.content;

import com.quizzes.api.core.dtos.AnswerDto;
import com.quizzes.api.core.dtos.AssessmentMetadataDto;
import com.quizzes.api.core.dtos.ChoiceDto;
import com.quizzes.api.core.dtos.CollectionDto;
import com.quizzes.api.core.dtos.InteractionDto;
import com.quizzes.api.core.dtos.ResourceMetadataDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.content.AnswerContentDto;
import com.quizzes.api.core.dtos.content.AssessmentContentDto;
import com.quizzes.api.core.dtos.content.QuestionContentDto;
import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.rest.clients.AssessmentRestClient;
import com.quizzes.api.core.rest.clients.AuthenticationRestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private static final Map<String, String> questionTypeMap;

    static {
        questionTypeMap = new HashMap<>();
        questionTypeMap.put(GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral(),
                QuestionTypeEnum.TrueFalse.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral(),
                QuestionTypeEnum.SingleChoice.getLiteral());
        questionTypeMap.put(GooruQuestionTypeEnum.HotTextReorderQuestion.getLiteral(),
                QuestionTypeEnum.DragAndDrop.getLiteral());
    }

    @Autowired
    AuthenticationRestClient authenticationRestClient;

    @Autowired
    AssessmentRestClient assessmentRestClient;

    public CollectionDto getAssessment(String assessmentId) {
        String userToken = authenticationRestClient.generateAnonymousToken();
        AssessmentContentDto assessmentDto = assessmentRestClient.getAssessment(assessmentId, userToken);
        return convertGooruAssessmentToQuizzesFormat(assessmentDto, assessmentId);
    }

    private CollectionDto convertGooruAssessmentToQuizzesFormat(AssessmentContentDto assessmentDto, String assessmentId) {
        AssessmentMetadataDto metadata = new AssessmentMetadataDto();
        metadata.setTitle(assessmentDto.getTitle());

        CollectionDto collectionDto = new CollectionDto();
        collectionDto.setId(assessmentId);
        collectionDto.setMetadata(metadata);
        collectionDto.setResources(getResources(assessmentDto.getQuestions()));

        return collectionDto;
    }

    private List<ResourceDto> getResources(List<QuestionContentDto> questions) {
        List<ResourceDto> resources = new ArrayList<>();

        if (questions != null) {
            resources = questions.stream().map(questionContentDto -> {
                ResourceDto resourceDto = new ResourceDto();
                resourceDto.setId(UUID.fromString(questionContentDto.getId()));
                resourceDto.setIsResource(false);
                resourceDto.setSequence((short) questionContentDto.getSequence());

                ResourceMetadataDto metadata = new ResourceMetadataDto();
                metadata.setTitle(questionContentDto.getTitle());
                metadata.setType(mapQuestionType(questionContentDto.getContentSubformat()));
                metadata.setCorrectAnswer(getCorrectAnswers(questionContentDto.getAnswers()));
                metadata.setInteraction(createInteraction(questionContentDto.getAnswers()));
                metadata.setBody(questionContentDto.getTitle());

                resourceDto.setQuestionData(metadata);

                return resourceDto;
            }).collect(Collectors.toList());
        }

        return resources;
    }

    private String mapQuestionType(String gooruQuestionType) {
        String mappedType = questionTypeMap.get(gooruQuestionType);
        if (mappedType == null) {
            mappedType = QuestionTypeEnum.None.getLiteral();
        }
        return mappedType;
    }

    private List<AnswerDto> getCorrectAnswers(List<AnswerContentDto> answers) {
        List<AnswerDto> correctAnswers = new ArrayList<>();
        if (answers != null) {
            correctAnswers = answers.stream()
                    .filter(answer -> answer.isCorrect().equalsIgnoreCase("true") || answer.isCorrect().equals("1"))
                    .map(answer -> new AnswerDto(answer.getId()))
                    .collect(Collectors.toList());
        }
        return correctAnswers;
    }

    private InteractionDto createInteraction(List<AnswerContentDto> answers) {
        List<ChoiceDto> choices = answers.stream().map(answer -> {
            ChoiceDto choiceDto = new ChoiceDto();
            choiceDto.setFixed(true);
            choiceDto.setText(answer.getAnswerText());
            choiceDto.setValue(answer.getId());
            choiceDto.setSequence(answer.getSequence());
            return choiceDto;
        }).collect(Collectors.toList());

        InteractionDto interactionDto = new InteractionDto();
        interactionDto.setShuffle(false);
        interactionDto.setMaxChoices(0);
        interactionDto.setPrompt("");
        interactionDto.setChoices(choices);

        return interactionDto;
    }
}
