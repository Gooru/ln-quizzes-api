package com.quizzes.api.common.service;

import com.quizzes.api.common.dto.controller.ProfileDTO;
import com.quizzes.api.common.model.tables.pojos.Group;
import com.quizzes.api.common.model.tables.pojos.GroupProfile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class GroupProfileServiceImplTest {

    @InjectMocks
    private GroupProfileService groupProfileService = Mockito.spy(GroupProfileServiceImpl.class);

    @Test
    public void assignStudentListToGroup() throws Exception {
        Group group = new Group();
        List<ProfileDTO> assignees = new ArrayList<>();

        GroupProfile result = groupProfileService.assignAssigneesListToGroup(group, assignees);
        verify(groupProfileService, times(1)).assignAssigneesListToGroup(Mockito.eq(group), Mockito.eq(assignees));
        assertNull("Response is not null", result);
    }
}