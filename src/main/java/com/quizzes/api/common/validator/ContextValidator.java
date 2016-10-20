package com.quizzes.api.common.validator;

import com.quizzes.api.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Value;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Created by mzumbado on 10/20/16.
 */
public class ContextValidator implements ConstraintValidator<ValidContext, Map<?, ?>> {

    @Value("quizzes.context.mandatoryFields")
    private String[] mandatoryFields;

    @Override
    public void initialize(final ValidContext annotation) {
        return;
    }

    @Override
    public boolean isValid(final Map<?, ?> map, final ConstraintValidatorContext context) {
        if (map == null || map.size() == 0)
            return false;

        if (JsonUtil.getMissingPropertiesList((Map<String, Object>)map, mandatoryFields).isEmpty()){
            return true;
        }
        return false;
    }
}