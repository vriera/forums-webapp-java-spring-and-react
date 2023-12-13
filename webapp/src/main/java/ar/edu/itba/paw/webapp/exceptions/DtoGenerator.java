package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.ws.rs.core.Context;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DtoGenerator {

    @Autowired
    public DtoGenerator(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    public ErrorDto messageToErrorDto(String messageKey, List<String> params, Locale locale) {
        ErrorDto errorDto = new ErrorDto();
        List<String> parameters = params == null ? Collections.emptyList() : params;
        errorDto.setCode(messageKey);
        try {
            String message = this.messageSource.getMessage(messageKey, parameters.toArray(), locale);
            errorDto.setMessage(message);
        } catch (NoSuchMessageException ignored) {
        }

        return errorDto;
    }

    public ErrorDto constrainViolationToErrorDto(Set<ConstraintViolation<?>> violations, Locale locale) {
        ErrorDto errorDto = new ErrorDto();
        String errors = violations.stream().map(constraintViolation -> {
            try {
                return this.messageSource.getMessage(constraintViolation.getMessage(),
                        constraintViolation.getExecutableParameters(), locale);
            } catch (Exception exception) {
                return constraintViolation.getMessage();
            }
        })
                .collect(Collectors.joining(", "));
        errorDto.setMessage(errors);
        errorDto.setCode("dto.validation.error");
        return errorDto;
    }

    public final MessageSource messageSource;

}
