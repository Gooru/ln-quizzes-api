package com.quizzes.api.core.factory.analytics.impl;

public class HotTextSentenceHighlightAnswerCreator extends HotTextWordHighlightAnswerCreator {

    public HotTextSentenceHighlightAnswerCreator() {
        this.tokenSeparator = ".";
    }
}
