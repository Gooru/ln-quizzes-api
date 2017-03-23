package com.quizzes.api.core.services.analytics;

import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.services.analytics.impl.DragAndDropAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.ExtendedTextAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.HotTextSentenceAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.HotTextWordAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.MultipleChoiceAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.MultipleChoiceImageAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.MultipleChoiceTextAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.SingleChoiceAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.TextEntryAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.TrueFalseAnswerCreator;
import com.quizzes.api.core.services.analytics.impl.UnknownAnswerCreator;
import org.springframework.stereotype.Component;

@Component
public class AnswerCreatorFactory {

    public AnswerCreator getAnswerCreator(QuestionTypeEnum questionType) {
        switch (questionType) {
            case TrueFalse:
                return new TrueFalseAnswerCreator();
            case SingleChoice:
                return new SingleChoiceAnswerCreator();
            case DragAndDrop:
                return new DragAndDropAnswerCreator();
            case TextEntry:
                return new TextEntryAnswerCreator();
            case MultipleChoiceText:
                return new MultipleChoiceTextAnswerCreator();
            case MultipleChoice:
                return new MultipleChoiceAnswerCreator();
            case MultipleChoiceImage:
                return new MultipleChoiceImageAnswerCreator();
            case HotTextSentence:
                return new HotTextSentenceAnswerCreator();
            case HotTextWord:
                return new HotTextWordAnswerCreator();
            case ExtendedText:
                return new ExtendedTextAnswerCreator();
            default:
                return new UnknownAnswerCreator();
        }
    }
}
