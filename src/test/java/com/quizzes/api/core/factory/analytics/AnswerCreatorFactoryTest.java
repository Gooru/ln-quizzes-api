package com.quizzes.api.core.factory.analytics;

import com.quizzes.api.core.enums.QuestionTypeEnum;
import com.quizzes.api.core.factory.analytics.impl.FillInTheBlankAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.HotSpotAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.HotTextHighlightAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.HotTextReorderAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.MultipleAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.MultipleChoiceAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.OpenEndedAnswerCreator;
import com.quizzes.api.core.factory.analytics.impl.UnknownAnswerCreator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(AnswerCreatorFactory.class)
public class AnswerCreatorFactoryTest {

    AnswerCreatorFactory answerCreatorFactory = spy(new AnswerCreatorFactory());

    @Test
    public void getAnswerCreatorTrueFalse() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.TrueFalse);

        assertThat(creator, instanceOf(MultipleChoiceAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorSingleChoice() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.SingleChoice);

        assertThat(creator, instanceOf(MultipleChoiceAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorDragAndDrop() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.DragAndDrop);

        assertThat(creator, instanceOf(HotTextReorderAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorTextEntry() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.TextEntry);

        assertThat(creator, instanceOf(FillInTheBlankAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorMultipleChoice() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.MultipleChoice);

        assertThat(creator, instanceOf(MultipleAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorMultipleChoiceText() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.MultipleChoiceText);

        assertThat(creator, instanceOf(HotSpotAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorMultipleChoiceImage() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.MultipleChoiceImage);

        assertThat(creator, instanceOf(HotSpotAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorHotTextSentence() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.HotTextSentence);

        assertThat(creator, instanceOf(HotTextHighlightAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorHotTextWord() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.HotTextWord);

        assertThat(creator, instanceOf(HotTextHighlightAnswerCreator.class));
    }

    @Test
    public void getAnswerCreatorExtendedText() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.ExtendedText);

        assertThat(creator, instanceOf(OpenEndedAnswerCreator.class));
    }

    @Test
    public void getUnknownAnswerCreator() {
        AnswerCreator creator = answerCreatorFactory.getAnswerCreator(QuestionTypeEnum.Unknown);
        assertThat(creator, instanceOf(UnknownAnswerCreator.class));
    }

}
