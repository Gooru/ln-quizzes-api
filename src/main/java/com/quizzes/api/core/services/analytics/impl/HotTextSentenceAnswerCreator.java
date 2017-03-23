package com.quizzes.api.core.services.analytics.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HotTextSentenceAnswerCreator extends HotTextAnswerCreator {

    @Override
    protected List<Integer> getOptionsOrder(String body) {
        List<String> sentences = Arrays.asList(body.split("\\."));

        return sentences.stream().map(sentence -> body.indexOf(sentence.trim())).collect(Collectors.toList());
    }

}
