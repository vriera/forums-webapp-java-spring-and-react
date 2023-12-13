package ar.edu.itba.paw.webapp.dto.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StringIsLongValidator implements ConstraintValidator<StringIsLong, String> {

    @Override
    public void initialize(StringIsLong constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are considered valid by default
        }

        try {
            Long.parseLong(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}