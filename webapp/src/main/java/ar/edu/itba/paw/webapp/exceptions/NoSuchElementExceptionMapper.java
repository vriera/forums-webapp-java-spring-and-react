package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.controller.dto.ErrorDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.NoSuchElementException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {
    @Override
    public Response toResponse(NoSuchElementException e) {
        ErrorDto errorDto = ErrorDto.exceptionToErrorDto(e);
        if(errorDto.getMessage() == null)
            errorDto.setMessage("Not found");
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<ErrorDto>(
                        errorDto) {
                }).build();
    }
}
