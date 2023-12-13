package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.ext.ExceptionMapper;
import java.util.NoSuchElementException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class NoSuchElementExceptionMapper implements ExceptionMapper<NoSuchElementException> {
    @Context
    protected HttpServletRequest request;

    private final DtoGenerator dtoGenerator;

    @Autowired
    public NoSuchElementExceptionMapper(DtoGenerator dtoGenerator) {
        this.dtoGenerator = dtoGenerator;
    }

    @Override
    public Response toResponse(NoSuchElementException e) {
        String msg = e.getMessage();
        ErrorDto errorDto = dtoGenerator.messageToErrorDto(msg == null ? "Not found" : msg, null, request.getLocale());
        return Response.status(Response.Status.NOT_FOUND).entity(
                new GenericEntity<ErrorDto>(
                        errorDto) {
                }).build();
    }
}
