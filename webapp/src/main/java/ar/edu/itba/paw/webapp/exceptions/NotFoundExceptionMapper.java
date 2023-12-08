package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Override
    public Response toResponse(NotFoundException e) {
        ErrorDto errorDto = ErrorDto.exceptionToErrorDto(e);
        if(errorDto.getMessage() == null)
            errorDto.setMessage("Not found");
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<ErrorDto>(
                        errorDto) {
                }).build();
    }
}
