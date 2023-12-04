package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Context
    private HttpServletRequest request;

    // ... constructor and other methods

    private Locale determineLocale() {
        return request.getLocale(); // This gets the best-matching locale based on the Accept-Language header
    }
    private final MessageSource messageSource;
    @Autowired
    public ConstraintViolationExceptionMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }
    @Override
    public Response toResponse(ConstraintViolationException e) {
        ErrorDto errorDto = new ErrorDto();
        Locale currentLocale = this.determineLocale();
        String errors = e.getConstraintViolations().stream().map( constraintViolation -> {
//                    Optional<String> property = Arrays.stream(constraintViolation.getPropertyPath().toString().split("\\.")).reduce((a, b)->b);
//                    StringBuilder error = new StringBuilder();
//                    if(property.isPresent()) {
//                        error.append(property.get());
//                        error.append(": ");
//                    }

                    try{
                        return this.messageSource.getMessage(constraintViolation.getMessage() , null , currentLocale);
                    }catch (Exception exception ){
                        return constraintViolation.getMessage();
                    }
                })
                .collect(Collectors.joining( ", "));

        errorDto.setMessage(errors);
        // Build your custom response
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<ErrorDto>(errorDto) {
                }).build();    }
}