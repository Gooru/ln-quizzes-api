package com.quizzes.api.util;

import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.exceptions.InvalidRequestException;
import com.quizzes.api.core.repositories.UtilsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class QuizzesUtils {

    private static final String ANONYMOUS_PROFILE = "anonymous";
    private static final UUID ANONYMOUS_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");
    public final static String ASSESSMENT = "assessment";
    public final static String COLLECTION = "collection";
    public final static String RESOURCE = "resource";
    public final static String QUESTION = "question";

    private static final EnumMap<QuestionTypeEnum, String> quizzesQuestionType;

    static {
        quizzesQuestionType = new EnumMap<>(QuestionTypeEnum.class);
        quizzesQuestionType.put(QuestionTypeEnum.TrueFalse, "T/F");
        quizzesQuestionType.put(QuestionTypeEnum.SingleChoice, "MC");
        quizzesQuestionType.put(QuestionTypeEnum.DragAndDrop, "HT_RO");
        quizzesQuestionType.put(QuestionTypeEnum.MultipleChoice, "MA");
        quizzesQuestionType.put(QuestionTypeEnum.MultipleChoiceImage, "HS_IMG");
        quizzesQuestionType.put(QuestionTypeEnum.MultipleChoiceText, "HS_TXT");
        quizzesQuestionType.put(QuestionTypeEnum.HotTextWord, "HT_HL");
        quizzesQuestionType.put(QuestionTypeEnum.HotTextSentence, "HT_HL");
        quizzesQuestionType.put(QuestionTypeEnum.TextEntry, "FIB");
        quizzesQuestionType.put(QuestionTypeEnum.ExtendedText, "OE");
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

    public static void rejectAnonymous(UUID profileId, String message) {
        if (profileId.equals(ANONYMOUS_ID)) {
            throw new InvalidRequestException(message);
        }
    }

    public static boolean isAnonymous(String profileId) {
        return profileId.equals(ANONYMOUS_PROFILE) || profileId.equals(ANONYMOUS_ID.toString());
    }

    public static boolean isAnonymous(UUID profileId) {
        return ANONYMOUS_ID.equals(profileId);
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
        String mappedType = QuizzesUtils.quizzesQuestionType.get(QuestionTypeEnum.getEnum(quizzesQuestionType));
        return (mappedType == null) ? QuestionTypeEnum.Unknown.getLiteral() : mappedType;
    }

    public static String encodeString(String string) {
        byte[] bytes = string.getBytes(StandardCharsets.UTF_8);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String decodeString(String string) {
        return new String(Base64.getDecoder().decode(string));
    }

}
