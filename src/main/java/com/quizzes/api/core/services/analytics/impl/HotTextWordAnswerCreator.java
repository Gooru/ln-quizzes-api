package com.quizzes.api.core.services.analytics.impl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class HotTextWordAnswerCreator extends HotTextAnswerCreator {

    @Override
    protected List<Integer> getOptionsOrder(String body) {
        List<String> words = Arrays.asList(body.split(" "));

        return words.stream().map(word -> body.indexOf(word.trim())).collect(Collectors.toList());
    }

}
