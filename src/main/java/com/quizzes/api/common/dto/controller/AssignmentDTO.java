package com.quizzes.api.common.dto.controller;

import com.quizzes.api.common.validator.ValidContext;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;

/**
 * This class is used to get the specific body context (json) in the endpoints
 */
public class AssignmentDTO {

    @NotNull(message = "{assignment.collection.not_null}")
    @Valid
    private CollectionDTO collection;
    @NotNull(message = "{assignment.teacher.not_null}")
    @Valid
    private TeacherDTO teacher;
    @NotNull(message = "{assignment.students.not_null}")
    @Size(min = 1, message = "{assignment.students.size}")
    @Valid
    private List<StudentDTO> students;
    @NotNull(message = "{assignment.context.not_null}")
    @ValidContext(lms="itslearning")
    private Map<String, String> context;

    public AssignmentDTO() {
    }

    public CollectionDTO getCollection() {
        return collection;
    }

    public void setCollection(CollectionDTO collection) {
        this.collection = collection;
    }

    public TeacherDTO getTeacher() {
        return teacher;
    }

    public void setTeacher(TeacherDTO teacher) {
        this.teacher = teacher;
    }

    public List<StudentDTO> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDTO> students) {
        this.students = students;
    }

    public Map<String, String> getContext() {
        return context;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }
}
