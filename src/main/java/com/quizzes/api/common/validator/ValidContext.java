package com.quizzes.api.common.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by mzumbado on 10/20/16.
 */
@Target({ FIELD })
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = ContextValidator.class)
public @interface ValidContext {
    String lms();

    String message() default "valid.map";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}