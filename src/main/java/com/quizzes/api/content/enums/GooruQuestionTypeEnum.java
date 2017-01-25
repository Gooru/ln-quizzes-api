package com.quizzes.api.content.enums;


public enum GooruQuestionTypeEnum {

    TrueFalseQuestion("true_false_question"),

    MultipleChoiceQuestion("multiple_choice_question");


    private final String literal;

    private GooruQuestionTypeEnum(String literal) {
        this.literal = literal;
    }

    public java.lang.String getLiteral() {
        return literal;
    }

}
