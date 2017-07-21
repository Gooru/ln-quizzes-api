package com.quizzes.api.core.factory.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HotTextWordHighlightAnswerCreator implements AnswerCreator {

    private static final Pattern answerPattern = Pattern.compile("(.*),(.*)");
    protected String tokenSeparator = " ";

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());
        String body = resource.getMetadata().getBody();

        for (String answer : userAnswers) {
            Matcher answerMatcher = answerPattern.matcher(answer);
            answerMatcher.find();

            Integer absoluteIndex = new Integer(answerMatcher.group(2));
            String text = answerMatcher.group(1);

            answerObjects.add(AnswerObject.builder()
                    .answerId("0")
                    .timeStamp(answerResource.getTimeSpent())
                    .order(getTokenIndex(body, absoluteIndex))
                    .status(isCorrectContains(answer, correctValues))
                    .skip(false)
                    .text(text)
                    .build());
        }

        return answerObjects;
    }

    protected int getTokenIndex(String body, int absoluteIndex) {
        return StringUtils.countOccurrencesOf(body.substring(0, absoluteIndex), tokenSeparator) + 1;
    }
}