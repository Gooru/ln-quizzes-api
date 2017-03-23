package com.quizzes.api.core.services.analytics.impl;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;
import com.quizzes.api.core.services.analytics.AnswerCreator;

import java.util.Collections;
import java.util.List;

public class UnknownAnswerCreator implements AnswerCreator {

    @Override
    public List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource) {
        return Collections.emptyList();
    }
}
