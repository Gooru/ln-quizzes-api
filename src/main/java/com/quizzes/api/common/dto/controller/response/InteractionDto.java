package com.quizzes.api.common.dto.controller.response;

import java.util.List;

public class InteractionDto {
    boolean shuffle;
    int maxChoices;
    String prompt;
    List<ChoiceDto> choices;

    public InteractionDto(boolean shuffle, int maxChoices, String prompt, List<ChoiceDto> choices) {
        this.shuffle = shuffle;
        this.maxChoices = maxChoices;
        this.prompt = prompt;
        this.choices = choices;
    }

    public boolean isShuffle() {
        return shuffle;
    }

    public int getMaxChoices() {
        return maxChoices;
    }

    public String getPrompt() {
        return prompt;
    }

    public List<ChoiceDto> getChoices() {
        return choices;
    }
}
