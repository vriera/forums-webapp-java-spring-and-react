package ar.edu.itba.paw.webapp.dto.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = AccessTypeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidAccessType {
    String message() default "Invalid access type";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}