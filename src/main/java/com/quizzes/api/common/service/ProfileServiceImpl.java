package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Override
    public Profile findById(UUID id) {
        return null;
    }

    @Override
    public Profile findOrCreateStudent(StudentDTO student) {
        return null;
    }


    @Override
    public Profile findOrCreateTeacher(TeacherDTO teacher) {
        return null;
    }
}
