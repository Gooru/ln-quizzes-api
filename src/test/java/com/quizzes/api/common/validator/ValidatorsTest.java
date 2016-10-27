package com.quizzes.api.common.validator;

import com.quizzes.api.common.dto.controller.AssignmentDTO;
import com.quizzes.api.common.dto.controller.CollectionDTO;
import com.quizzes.api.common.dto.controller.StudentDTO;
import com.quizzes.api.common.dto.controller.TeacherDTO;
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
    public void testStudentDTO() {

        StudentDTO student = new StudentDTO();

        Set<ConstraintViolation<StudentDTO>> constraintViolations =
                validator.validate(student);
        assertEquals(4, constraintViolations.size());
    }

    @Test
    public void testTeacherDTO() {

        TeacherDTO teacher = new TeacherDTO();

        Set<ConstraintViolation<TeacherDTO>> constraintViolations =
                validator.validate(teacher);
        assertEquals(4, constraintViolations.size());
    }

    @Test
    public void testCollectionDTO() {

        CollectionDTO collection = new CollectionDTO();

        Set<ConstraintViolation<CollectionDTO>> constraintViolations =
                validator.validate(collection);
        assertEquals(3, constraintViolations.size());
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
        TeacherDTO teacher = new TeacherDTO();
        assignment.setTeacher(teacher);
        StudentDTO student = new StudentDTO();
        List<StudentDTO> students = new ArrayList<>();
        students.add(student);
        assignment.setStudents(students);
        CollectionDTO collection = new CollectionDTO();
        assignment.setCollection(collection);
        Map<String, String> context = new HashMap<>();
        context.put("classId","123");
        assignment.setContext(context);

        Set<ConstraintViolation<AssignmentDTO>> constraintViolations =
                validator.validate(assignment);
        assertEquals(11, constraintViolations.size());
    }


}
