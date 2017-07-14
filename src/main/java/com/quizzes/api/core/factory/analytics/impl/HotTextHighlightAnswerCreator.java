package com.quizzes.api.core.factory.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.factory.analytics.AnswerCreator;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HotTextHighlightAnswerCreator implements AnswerCreator {

    private final String bodyPartsSeparator;

    public HotTextHighlightAnswerCreator(String bodyPartsSeparator) {
        Assert.notNull(bodyPartsSeparator, "Body parts separator cannot be null");

        this.bodyPartsSeparator = bodyPartsSeparator;
    }

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        List<AnswerObject> answerObjects = new ArrayList<>();

        List<Integer> optionsOrder = getOptionsOrder(resource.getMetadata().getBody());
        List<String> correctValues = getAnswerValues(resource.getMetadata().getCorrectAnswer());
        List<String> userAnswers = getAnswerValues(answerResource.getAnswer());

        for (String answer : userAnswers) {
            Pattern answerPattern = Pattern.compile("(.*),(.*)");
            Matcher answerMatcher = answerPattern.matcher(answer);
            answerMatcher.find();

            answerObjects.add(AnswerObject.builder()
                    .answerId("0")
                    .timeStamp(answerResource.getTimeSpent())
                    .order(optionsOrder.indexOf(new Integer(answerMatcher.group(2))) + 1)
                    .status(isCorrectContains(answer, correctValues))
                    .skip(false)
                    .text(answerMatcher.group(1))
                    .build());
        }

        return answerObjects;
    }

    protected List<Integer> getOptionsOrder(String body) {
        List<String> parts = Arrays.asList(body.split(bodyPartsSeparator));

        return parts.stream().map(part -> body.indexOf(part.trim())).collect(Collectors.toList());
    }
}