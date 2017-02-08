package com.quizzes.api.core.enums;

import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

public enum QuestionTypeEnum {

    @SerializedName("true_false")
    TrueFalse("true_false"),

    @SerializedName("single_choice")
    SingleChoice("single_choice"),

    @SerializedName("drag_and_drop")
    DragAndDrop("drag_and_drop"),

    @SerializedName("multiple_choice")
    MultipleChoice("multiple_choice"),

    @SerializedName("multiple_choice_image")
    MultipleChoiceImage("multiple_choice_image"),

    @SerializedName("multiple_choice_text")
    MultipleChoiceText("multiple_choice_text"),

    @SerializedName("hot_text_word")
    HotTextWord("hot_text_word"),

    @SerializedName("hot_text_sentence")
    HotTextSentence("hot_text_sentence"),

    @SerializedName("text_entry")
    TextEntry("text_entry"),

    @SerializedName("match")
    Match("match"),

    @SerializedName("none")
    None("none");

    private final String literal;

    private QuestionTypeEnum(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }

    public static QuestionTypeEnum fromString(String text) {
        return Arrays.stream(QuestionTypeEnum.values())
                .filter(e -> e.literal.equals(text))
                .findAny()
                .orElseThrow(() -> new IllegalStateException(String.format("Unsupported type %s.", text)));
    }

}
