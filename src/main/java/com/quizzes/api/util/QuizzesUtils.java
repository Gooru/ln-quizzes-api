package com.quizzes.api.util;

import com.quizzes.api.core.enums.GooruQuestionTypeEnum;
import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.repositories.UtilsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class QuizzesUtils {

    private static final String ANONYMOUS_PROFILE = "anonymous";
    private static final UUID ANONYMOUS_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public final static String ASSESSMENT = "assessment";
    public final static String COLLECTION = "collection";
    public final static String RESOURCE = "resource";
    public final static String QUESTION = "question";

    private static final Map<String, String> quizzesQuestionType;

    static {
        quizzesQuestionType = new HashMap<>();
        quizzesQuestionType.put(QuestionTypeEnum.TrueFalse.getLiteral(),
                GooruQuestionTypeEnum.TrueFalseQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.SingleChoice.getLiteral(),
                GooruQuestionTypeEnum.MultipleChoiceQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.DragAndDrop.getLiteral(),
                GooruQuestionTypeEnum.HotTextReorderQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.MultipleChoice.getLiteral(),
                GooruQuestionTypeEnum.MultipleAnswerQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.MultipleChoiceImage.getLiteral(),
                GooruQuestionTypeEnum.HotSpotImageQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.MultipleChoiceText.getLiteral(),
                GooruQuestionTypeEnum.HotSpotTextQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.HotTextWord.getLiteral(),
                GooruQuestionTypeEnum.WordHotTextHighlightQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.HotTextSentence.getLiteral(),
                GooruQuestionTypeEnum.SentenceHotTextHighlightQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.TextEntry.getLiteral(),
                GooruQuestionTypeEnum.FillInTheBlankQuestion.getLiteral());
        quizzesQuestionType.put(QuestionTypeEnum.ExtendedText.getLiteral(),
                GooruQuestionTypeEnum.OpenEndedQuestion.getLiteral());
    }

    @Autowired
    private UtilsRepository utilsRepository;

    public static void rejectAnonymous(String profileId) {
        rejectAnonymous(profileId, "Anonymous not allowed to run this service");
    }

    public static void rejectAnonymous(String profileId, String message) {
        if (profileId.equals(ANONYMOUS_PROFILE)) {
            throw new InvalidRequestException(message);
        }
    }

    public static boolean isAnonymous(String profileId) {
        return profileId.equals(ANONYMOUS_PROFILE) || profileId.equals(ANONYMOUS_ID.toString());
    }

    public static UUID getAnonymousId() {
        return ANONYMOUS_ID;
    }

    public static UUID resolveProfileId(String profileId) {
        if (isAnonymous(profileId)) {
            return QuizzesUtils.getAnonymousId();
        } else {
            return UUID.fromString(profileId);
        }
    }

    public long getCurrentTimestamp() {
        return utilsRepository.getCurrentTimestamp();
    }

    public String getGooruQuestionType(String quizzesQuestionType) {
        String mappedType = QuizzesUtils.quizzesQuestionType.get(quizzesQuestionType);
        return (mappedType == null) ? QuestionTypeEnum.None.getLiteral() : mappedType;
    }
}
