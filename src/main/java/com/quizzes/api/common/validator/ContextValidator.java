package com.quizzes.api.common.validator;

import com.quizzes.api.common.utils.JsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;

/**
 * Created by mzumbado on 10/20/16.
 */
public class ContextValidator implements ConstraintValidator<ValidContext, Map<?, ?>> {

    @Resource
    public Environment environment;

    private String lms = "";

    private String[] mandatoryFields;


    @Override
    public void initialize(final ValidContext annotation) {
        lms = annotation.lms();
    }

    @Override
    public boolean isValid(final Map<?, ?> map, final ConstraintValidatorContext context) {
        if (map == null || map.size() == 0)
            return false;

        if (!JsonUtil.getMissingPropertiesList((Map<String, Object>)map, mandatoryFields).isEmpty()){
            return false;
        }
        for (String mandatoryField : mandatoryFields){
            Object value = map.get(mandatoryField);
            if (value == null){
                return false;
            }
            if (value instanceof String && ((String) value).isEmpty()){
                return false;
            }
            if (value instanceof java.util.Collection && ((java.util.Collection) value).isEmpty()){
                return false;
            }
            if (value instanceof java.util.Map && ((Map) value).isEmpty()){
                return false;
            }

    }
        return true;
    }
}