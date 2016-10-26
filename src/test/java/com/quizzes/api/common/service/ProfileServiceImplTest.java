package com.quizzes.api.common.service;

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

    @Test
    public void findById() throws Exception {
        UUID id = UUID.randomUUID();
        doReturn(null).when(profileService).findById(id);

        Profile result = profileService.findById(id);
        verify(profileService, times(1)).findById(eq(id));
        assertNull("Response is not null", result);
    }

    @Test
    public void findOrCreateStudent() throws Exception {
        StudentDTO student = new StudentDTO();
        Lms lms = Lms.its_learning;

        Profile result = profileService.findOrCreateStudent(student, lms);
        verify(profileService, times(1)).findOrCreateStudent(eq(student), eq(lms));
        assertNull("Response is not null", result);
    }

    @Test
    public void findOrCreateTeacherFind() throws Exception {
        TeacherDTO teacher = new TeacherDTO();
        String profileExternalId = UUID.randomUUID().toString();
        String profileData = "{\"firstName\":\"name\"}";
        teacher.setId(profileExternalId);

        when(profileRepository
                .findByExternalIdAndLmsId(profileExternalId, Lms.its_learning))
                .thenReturn(new Profile(UUID.randomUUID(), profileExternalId, Lms.its_learning, profileData, null));

        Profile result = profileService.findOrCreateTeacher(teacher, Lms.its_learning);

        verify(profileService, times(1)).findOrCreateTeacher(Mockito.eq(teacher), Mockito.eq(Lms.its_learning));
        verify(profileRepository, times(1))
                .findByExternalIdAndLmsId(
                        Mockito.eq(profileExternalId),
                        Mockito.eq(Lms.its_learning));
        verify(profileRepository, times(0)).save(Mockito.eq(new Profile()));

        assertNotNull("Response is null", result);
        assertNotNull("Id is null", result.getId());
        assertEquals("Wrong external id", profileExternalId, result.getExternalId());
        assertEquals("Wrong Lms", Lms.its_learning, result.getLmsId());
        assertEquals("Wrong profile data", profileData, result.getProfileData());
    }

    @Test
    public void findOrCreateTeacherCreate() throws Exception {
        TeacherDTO teacher = new TeacherDTO();
        String profileExternalId = UUID.randomUUID().toString();
        String profileData = "{\"firstName\":\"name\"}";
        teacher.setId(profileExternalId);

        when(profileRepository
                .findByExternalIdAndLmsId(profileExternalId, Lms.its_learning))
                .thenReturn(null);
        when(profileRepository
                .save(any(Profile.class)))
                .thenReturn(new Profile(UUID.randomUUID(), profileExternalId, Lms.its_learning, profileData, null));

        Profile result = profileService.findOrCreateTeacher(teacher, Lms.its_learning);

        verify(profileService, times(1)).findOrCreateTeacher(Mockito.eq(teacher), Mockito.eq(Lms.its_learning));
        verify(profileRepository, times(1))
                .findByExternalIdAndLmsId(
                        Mockito.eq(profileExternalId),
                        Mockito.eq(Lms.its_learning));
        verify(profileRepository, times(1)).save(any(Profile.class));

        assertNotNull("Response is null", result);
        assertNotNull("Id is null", result.getId());
        assertEquals("Wrong external id", profileExternalId, result.getExternalId());
        assertEquals("Wrong Lms", Lms.its_learning, result.getLmsId());
        assertEquals("Wrong profile data", profileData, result.getProfileData());
    }

}