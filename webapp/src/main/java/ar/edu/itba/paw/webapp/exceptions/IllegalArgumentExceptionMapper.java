package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.controller.dto.SuccessDto;

import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    @Override
    public Response toResponse(IllegalArgumentException e) {
        SuccessDto successDto = new SuccessDto();
        successDto.setCode(e.getMessage());
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<SuccessDto>(
                        successDto) {
                }).build();
    }
}