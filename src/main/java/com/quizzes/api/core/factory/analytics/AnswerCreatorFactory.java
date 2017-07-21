package com.quizzes.api.core.factory.analytics;

import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.factory.analytics.impl.FillInTheBlankAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.HotSpotAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.HotTextWordHighlightAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.HotTextSentenceHighlightAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.HotTextReorderAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.MultipleAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.MultipleChoiceAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.OpenEndedAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.UnknownAnswerCreator;
import org.springframework.stereotype.Component;

@Component
public class AnswerCreatorFactory {

    public AnswerCreator getAnswerCreator(QuestionTypeEnum questionType) {
        switch (questionType) {
            case TrueFalse:
            case SingleChoice:
                return new MultipleChoiceAnswerCreator();
            case DragAndDrop:
                return new HotTextReorderAnswerCreator();
            case TextEntry:
                return new FillInTheBlankAnswerCreator();
            case MultipleChoice:
                return new MultipleAnswerCreator();
            case MultipleChoiceText:
            case MultipleChoiceImage:
                return new HotSpotAnswerCreator();
            case HotTextSentence:
                return new HotTextSentenceHighlightAnswerCreator();
            case HotTextWord:
                return new HotTextWordHighlightAnswerCreator();
            case ExtendedText:
                return new OpenEndedAnswerCreator();
            default:
                return new UnknownAnswerCreator();
        }
    }
}
