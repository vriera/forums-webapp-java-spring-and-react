package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.models.exceptions.InvalidAccessTypeChangeException;
import ar.edu.itba.paw.webapp.controller.dto.SuccessDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class InvalidAccessTypeChangeExceptionMapper implements ExceptionMapper<InvalidAccessTypeChangeException> {
    @Override
    public Response toResponse(InvalidAccessTypeChangeException e) {

        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<SuccessDto>(
                        SuccessDto.exceptionToSuccessDto(e)) {
                }).build();
    }
}
