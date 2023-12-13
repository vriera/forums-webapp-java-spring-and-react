package ar.edu.itba.paw.webapp.dto.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = StringIsLongValidator.class)
@Target({ METHOD, PARAMETER })
@Retention(RUNTIME)
public @interface StringIsLong {

    String message() default "The string is not a valid Long";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}