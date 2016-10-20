package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.model.tables.pojos.Profile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface ProfileService {

    Profile findById(UUID id);

    Profile findOrCreateStudent(StudentDTO studentDTO);

    Profile findOrCreateTeacher(TeacherDTO teacherDTO);

}
