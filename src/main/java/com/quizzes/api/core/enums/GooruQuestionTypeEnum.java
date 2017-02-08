package com.quizzes.api.core.enums;


public enum GooruQuestionTypeEnum {

    TrueFalseQuestion("true_false_question"),

    MultipleChoiceQuestion("multiple_choice_question"),

    HotTextReorderQuestion("hot_text_reorder_question"),

    MultipleAnswerQuestion("multiple_answer_question"),

    HotSpotImageQuestion("hot_spot_image_question"),

    HotSpotTextQuestion("hot_spot_text_question"),

    HotTextHighlightQuestion("hot_text_highlight_question"),

    WordHotTextHighlightQuestion("word_hot_text_highlight_question"),

    SentenceHotTextHighlightQuestion("sentence_hot_text_highlight_question"),

    FillInTheBlankQuestion("fill_in_the_blank_question");

    private final String literal;

    private GooruQuestionTypeEnum(String literal) {
        this.literal = literal;
    }

    public java.lang.String getLiteral() {
        return literal;
    }

}
