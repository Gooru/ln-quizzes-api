package com.quizzes.api.common.dto.controller.response;


import com.quizzes.api.common.dto.controller.AssignmentDTO;

import java.util.UUID;

public class AssignmentResponseDTO extends AssignmentDTO {

    private UUID id;

    public AssignmentResponseDTO() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
