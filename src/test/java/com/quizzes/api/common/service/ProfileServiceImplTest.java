package com.quizzes.api.common.service;

import com.google.gson.Gson;
import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
import com.quizzes.api.common.model.enums.Lms;
import com.quizzes.api.common.model.tables.pojos.Profile;
import com.quizzes.api.common.repository.ProfileRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

public class ProfileServiceImplTest {

    @InjectMocks
    private ProfileServiceImpl profileService = Mockito.spy(ProfileServiceImpl.class);

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private Logger logger;

    @Test
    public void findById() throws Exception {
        doReturn(null).when(profileService).findById(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));

        Profile result = profileService.findById(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"));
        verify(profileService, times(1)).findById(eq(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db")));
        assertNull(result);
    }

    @Test
    public void findOrCreateStudent() throws Exception {
        StudentDTO student = new StudentDTO();
        Lms lms = Lms.its_learning;

        Profile result = profileService.findOrCreateStudent(student, lms);
        verify(profileService, times(1)).findOrCreateStudent(eq(student), eq(lms));
        assertNull(result);
    }

    @Test
    public void findOrCreateTeacherFind() throws Exception {
        TeacherDTO teacher = new TeacherDTO();
        teacher.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db").toString());
        when(profileRepository
                .findByExternalIdAndLms(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"), Lms.its_learning))
                .thenReturn(
                        new Profile(UUID.randomUUID(), "external-id", Lms.its_learning, "{\"firstName\":\"name\"}", null)
                );

        Profile result = profileService.findOrCreateTeacher(teacher, Lms.its_learning);

        verify(profileService, times(1)).findOrCreateTeacher(Mockito.eq(teacher), Mockito.eq(Lms.its_learning));
        verify(profileRepository, times(1))
                .findByExternalIdAndLms(
                        Mockito.eq(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db")),
                        Mockito.eq(Lms.its_learning)
                );
        verify(profileRepository, times(0)).save(Mockito.eq(new Profile()));

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(result.getExternalId(), "external-id");
        assertEquals(result.getLmsId(), Lms.its_learning);
        assertEquals(result.getProfileData(), "{\"firstName\":\"name\"}");

    }

    @Test
    public void findOrCreateTeacherCreate() throws Exception {
        TeacherDTO teacher = new TeacherDTO();
        teacher.setId(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db").toString());

        when(profileRepository
                .findByExternalIdAndLms(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db"), Lms.its_learning))
                .thenReturn(null);

        when(profileRepository
                .save(any(Profile.class)))
                .thenReturn(
                        new Profile(UUID.randomUUID(), "external-id", Lms.its_learning, "{\"firstName\":\"name\"}", null)
                );

        Profile result = profileService.findOrCreateTeacher(teacher, Lms.its_learning);

        verify(profileService, times(1)).findOrCreateTeacher(Mockito.eq(teacher), Mockito.eq(Lms.its_learning));
        verify(profileRepository, times(1))
                .findByExternalIdAndLms(
                        Mockito.eq(UUID.fromString("8dc0dddb-f6c2-4884-97ed-66318a9958db")),
                        Mockito.eq(Lms.its_learning)
                );

        verify(profileRepository, times(1)).save(any(Profile.class));

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(result.getExternalId(), "external-id");
        assertEquals(result.getLmsId(), Lms.its_learning);
        assertEquals(result.getProfileData(), "{\"firstName\":\"name\"}");
    }

}