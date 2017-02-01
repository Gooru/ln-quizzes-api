package com.quizzes.api.core.enums;


public enum GooruQuestionTypeEnum {

    TrueFalseQuestion("true_false_question"),

    MultipleChoiceQuestion("multiple_choice_question"),

    HotTextReorderQuestion("hot_text_reorder_question"),

    MultipleAnswerQuestion("multiple_answer_question"),

    HotSpotImageQuestion("hot_spot_image_question");

    private final String literal;

    private GooruQuestionTypeEnum(String literal) {
        this.literal = literal;
    }

    public java.lang.String getLiteral() {
        return literal;
    }

}
