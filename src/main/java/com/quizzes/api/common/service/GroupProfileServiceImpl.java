package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupProfileServiceImpl implements GroupProfileService {

    @Override
    public GroupProfile assignStudentListToGroup(Group group, List<StudentDTO> students) {
        return null;
    }
}
