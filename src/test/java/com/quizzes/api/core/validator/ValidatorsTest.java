package com.quizzes.api.core.validator;

import com.quizzes.api.core.dtos.ContextPostRequestDto;
import com.quizzes.api.core.dtos.controller.CollectionDto;
import com.quizzes.api.core.dtos.controller.ContextDataDto;
import com.quizzes.api.core.dtos.ProfileDto;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class ValidatorsTest {

    private static Validator validator;

    @BeforeClass
    public static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    public void testProfileDto() {

        ProfileDto profile = new ProfileDto();

        Set<ConstraintViolation<ProfileDto>> constraintViolations =
                validator.validate(profile);
        assertEquals(5, constraintViolations.size());
    }

    @Test
    public void testCollectionDto() {

        CollectionDto collection = new CollectionDto();

        Set<ConstraintViolation<CollectionDto>> constraintViolations =
                validator.validate(collection);
        assertEquals(1, constraintViolations.size());
    }

    @Test
    public void testAssignmentDto() {

        ContextPostRequestDto assignment = new ContextPostRequestDto();

        Set<ConstraintViolation<ContextPostRequestDto>> constraintViolations =
                validator.validate(assignment);
        assertEquals(2, constraintViolations.size());
    }

    @Test
    public void testContext() {

        ContextPostRequestDto assignment = new ContextPostRequestDto();
        assignment.setCollectionId(UUID.randomUUID());
        ContextDataDto contextData = new ContextDataDto();
        assignment.setContextData(contextData);

        Set<ConstraintViolation<ContextPostRequestDto>> constraintViolations =
                validator.validate(assignment);
        assertEquals(0, constraintViolations.size());
    }


}
