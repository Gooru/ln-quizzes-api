package com.quizzes.api.core.factory.analytics.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.spy;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HotTextHighlightWordAnswerCreator.class)
public class HotTextHighlightWordAnswerCreatorTest {

    private HotTextHighlightWordAnswerCreator creator = spy(new HotTextHighlightWordAnswerCreator());

    String text = "The big bad wolf blew down the big house";

    @Test
    public void getTokenIndexForWords() {
        int badAbsoluteIndex = 8;
        int wordIndex = creator.getTokenIndex(text, badAbsoluteIndex);
        assertEquals(3, wordIndex);
    }

    @Test
    public void getTokenIndexForRepeatedWords() {

        int secondBigAbsoluteIndex = 31;
        int wordIndex = creator.getTokenIndex(text, secondBigAbsoluteIndex);
        assertEquals(8, wordIndex);
    }
}
