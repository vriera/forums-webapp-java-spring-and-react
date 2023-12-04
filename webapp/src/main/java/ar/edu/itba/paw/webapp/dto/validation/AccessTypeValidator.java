package ar.edu.itba.paw.webapp.dto.validation;

import ar.edu.itba.paw.models.AccessType;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AccessTypeValidator implements ConstraintValidator<ValidAccessType, String> {
    @Override
    public void initialize(ValidAccessType constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        try {
            AccessType.valueOf(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}