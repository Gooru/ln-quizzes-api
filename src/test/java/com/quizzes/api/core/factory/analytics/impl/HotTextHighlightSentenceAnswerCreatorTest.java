package com.quizzes.api.core.factory.analytics.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HotTextHighlightSentenceAnswerCreator.class)
public class HotTextHighlightSentenceAnswerCreatorTest {

    private HotTextHighlightSentenceAnswerCreator creator = spy(new HotTextHighlightSentenceAnswerCreator());
    private String text = "The first little pig built his house of straw. The big bad wolf blew down the house. The first little pig built his house of straw.";

    @Test
    public void getTokenIndexForSentences() {
        int secondSentenceAbsoluteIndex = 47;
        int sentenceIndex = creator.getTokenIndex(text, secondSentenceAbsoluteIndex);
        assertEquals(2, sentenceIndex);
    }

    @Test
    public void getTokenIndexForRepeatedSentences() {
        int repeatedSentenceAbsoluteIndex = 85;
        int sentenceIndex = creator.getTokenIndex(text, repeatedSentenceAbsoluteIndex);
        assertEquals(3, sentenceIndex);
    }
}
