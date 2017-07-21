package com.quizzes.api.core.factory.analytics.impl;

public class HotTextHighlightSentenceAnswerCreator extends HotTextHighlightWordAnswerCreator {

    public HotTextHighlightSentenceAnswerCreator() {
        this.tokenSeparator = ".";
    }
}
