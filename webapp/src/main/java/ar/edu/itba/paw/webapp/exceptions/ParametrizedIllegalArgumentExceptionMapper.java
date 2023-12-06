package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.models.exceptions.ParametrizedIllegalArgumentException;
import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@Provider
public class ParametrizedIllegalArgumentExceptionMapper implements ExceptionMapper<ParametrizedIllegalArgumentException> {
    @Context
    private HttpServletRequest request;

    @Autowired
    public ParametrizedIllegalArgumentExceptionMapper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    private Locale determineLocale() {
        return request.getLocale();
    }
    private final MessageSource messageSource;

    @Override
    public Response toResponse(ParametrizedIllegalArgumentException e) {
        ErrorDto errorDto = new ErrorDto();
        Locale currentLocale = this.determineLocale();
        String code = e.getMessage();
        List<String> parameters = e.getParameters();
        errorDto.setCode(code);
        try{
             String message =  this.messageSource.getMessage(code , parameters.toArray() , currentLocale);
             errorDto.setMessage(message);
        }catch (NoSuchMessageException ignored){}

        // Build your custom response
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<ErrorDto>(errorDto) {
                }).build();
    }
}