package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    ProfileRepository profileRepository;

    @Override
    public Profile findById(UUID id) {
        return null;
    }

    @Override
    public Profile findOrCreateStudent(StudentDTO student, Lms lms) {
        return null;
    }

    @Override
    public Profile findOrCreateTeacher(TeacherDTO teacher, Lms lms) {
        Profile profile = profileRepository.findByExternalIdAndLmsId(teacher.getId(), lms);
        if (profile != null) {
            return profile;
        }
        profile = profileRepository.save(new Profile(null, teacher.getId(), lms, new Gson().toJson(teacher), null));
        return profile;
    }
}
