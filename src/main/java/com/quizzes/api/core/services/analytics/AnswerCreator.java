package com.quizzes.api.core.services.analytics;

import com.quizzes.api.core.dtos.PostRequestResourceDto;
import com.quizzes.api.core.dtos.ResourceDto;
import com.quizzes.api.core.dtos.analytics.AnswerObject;

import java.util.List;

public interface AnswerCreator {

    List<AnswerObject> createAnswerObjects(PostRequestResourceDto answerResource, ResourceDto resource);

}
