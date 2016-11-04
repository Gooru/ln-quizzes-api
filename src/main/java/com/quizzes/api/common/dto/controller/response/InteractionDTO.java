package com.quizzes.api.common.dto.controller.response;

import java.util.List;

public class InteractionDTO {
    boolean shuffle;
    int maxChoices;
    String prompt;
    List<ChoiceDTO> choices;

    public InteractionDTO(boolean shuffle, int maxChoices, String prompt, List<ChoiceDTO> choices) {
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

    public List<ChoiceDTO> getChoices() {
        return choices;
    }
}
