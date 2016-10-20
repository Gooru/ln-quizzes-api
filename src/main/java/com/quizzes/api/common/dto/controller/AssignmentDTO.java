package com.quizzes.api.common.dto.controller;

import com.quizzes.api.common.validator.ValidContext;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * This class is used to get the specific body context (json) in the endpoints
 */
public class AssignmentDTO {

    @NotNull
    private CollectionDTO collection;
    @NotNull
    private TeacherDTO teacher;
    @NotNull
    private List<StudentDTO> students;
    @NotNull
    @ValidContext
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
