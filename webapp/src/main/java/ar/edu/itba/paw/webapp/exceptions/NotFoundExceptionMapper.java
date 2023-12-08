package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import ar.edu.itba.paw.webapp.exceptions.utils.DtoGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class NotFoundExceptionMapper implements ExceptionMapper<NotFoundException> {
    @Context
    protected HttpServletRequest request;

    private final DtoGenerator dtoGenerator;

    @Autowired
    public NotFoundExceptionMapper(DtoGenerator dtoGenerator){
        this.dtoGenerator = dtoGenerator;
    }
    @Override
    public Response toResponse(NotFoundException e) {
        String msg = e.getMessage();
        ErrorDto errorDto = dtoGenerator.messageToErrorDto(msg == null ? "Not found" : msg , null, request.getLocale());
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<ErrorDto>(
                        errorDto) {
                }).build();
    }
}
