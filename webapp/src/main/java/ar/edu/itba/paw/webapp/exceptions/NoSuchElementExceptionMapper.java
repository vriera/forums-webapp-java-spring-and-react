package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.controller.dto.SuccessDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.NoSuchElementException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Provider
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {
    @Override
    public Response toResponse(NoSuchElementException e) {
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<SuccessDto>(
                        SuccessDto.exceptionToSuccessDto(e)) {
                }).build();
    }
}
