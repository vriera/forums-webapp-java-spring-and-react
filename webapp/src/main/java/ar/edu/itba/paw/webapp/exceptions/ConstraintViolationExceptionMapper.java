package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import ar.edu.itba.paw.webapp.exceptions.utils.DtoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.core.Response;

@Component
@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Context
    private HttpServletRequest request;

    private final DtoGenerator dtoGenerator;
    @Autowired
    public ConstraintViolationExceptionMapper(DtoGenerator dtoGenerator){
        this.dtoGenerator = dtoGenerator;
    }

    @Override
    public Response toResponse(ConstraintViolationException e) {
        ErrorDto errorDto = dtoGenerator.constrainViolationToErrorDto(e.getConstraintViolations() , request.getLocale());
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<ErrorDto>(errorDto) {
                }).build();    }
}