package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class BadRequestExceptionMapper implements ExceptionMapper<BadRequestException> {
    @Override
    public Response toResponse(BadRequestException e) {
        // Return a 400 Bad Request response with exception details
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<ErrorDto>(
                        ErrorDto.exceptionToErrorDto(e)) {
                }).build();
    }
}