package com.quizzes.api.common.validator;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.ContextDataDTO;
import com.quizzes.api.common.dto.controller.ProfileDTO;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ValidatorsTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testProfileDTO() {

        ProfileDTO profile = new ProfileDTO();

        Set<ConstraintViolation<ProfileDTO>> constraintViolations =
                validator.validate(profile);
        assertEquals(4, constraintViolations.size());
    }
    
    @Test
    public void testCollectionDTO() {

        CollectionDTO collection = new CollectionDTO();

        Set<ConstraintViolation<CollectionDTO>> constraintViolations =
                validator.validate(collection);
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testAssignmentDTO() {

        AssignmentDTO assignment = new AssignmentDTO();

        Set<ConstraintViolation<AssignmentDTO>> constraintViolations =
                validator.validate(assignment);
        assertEquals(4, constraintViolations.size());
    }

    @Test
    public void testContext() {

        AssignmentDTO assignment = new AssignmentDTO();
        ProfileDTO owner = new ProfileDTO();
        assignment.setOwner(owner);
        ProfileDTO assignee = new ProfileDTO();
        List<ProfileDTO> assignees = new ArrayList<>();
        assignees.add(assignee);
        assignment.setAssignees(assignees);
        CollectionDTO collection = new CollectionDTO();
        assignment.setCollection(collection);
        ContextDataDTO contextData = new ContextDataDTO();
        assignment.setContextData(contextData);

        Set<ConstraintViolation<AssignmentDTO>> constraintViolations =
                validator.validate(assignment);
        assertEquals(9, constraintViolations.size());
    }


}
