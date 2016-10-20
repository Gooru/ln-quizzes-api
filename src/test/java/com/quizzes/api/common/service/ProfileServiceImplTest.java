package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)

public class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl profileService = Mockito.spy(ProfileServiceImpl.class);

    @Mock
    private ProfileRepository profileRepository;

    @Test
    public void findById() throws Exception {
        doReturn(null).when(profileService).findById(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));

        Profile result = profileService.findById(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        verify(profileService, times(1)).findById(Mockito.eq(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db")));
        assertNull(result);
    }

    @Test
    public void findOrCreateStudent() throws Exception {
        StudentDTO student = new StudentDTO();

        Profile result = profileService.findOrCreateStudent(student);
        verify(profileService, times(1)).findOrCreateStudent(Mockito.eq(student));
        assertNull(result);
    }

    @Test
    public void findOrCreateTeacher() throws Exception {
        TeacherDTO teacher = new TeacherDTO();

        Profile result = profileService.findOrCreateTeacher(teacher);
        verify(profileService, times(1)).findOrCreateTeacher(Mockito.eq(teacher));
        assertNull(result);
    }

}