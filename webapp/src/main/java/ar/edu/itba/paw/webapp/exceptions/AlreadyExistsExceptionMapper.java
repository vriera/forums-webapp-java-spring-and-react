package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.models.exceptions.AlreadyExistsException;
import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Component
@Provider
public class AlreadyExistsExceptionMapper implements ExceptionMapper<AlreadyExistsException> {

    @Context
    protected HttpServletRequest request;

    private final DtoGenerator dtoGenerator;

    @Autowired
    public AlreadyExistsExceptionMapper(DtoGenerator dtoGenerator) {
        this.dtoGenerator = dtoGenerator;
    }

    @Override
    public Response toResponse(AlreadyExistsException e) {
        ErrorDto errorDto = dtoGenerator.messageToErrorDto(e.getMessage(), null, request.getLocale());
        return Response.status(Response.Status.CONFLICT).entity(
                new GenericEntity<ErrorDto>(errorDto) {
                }).build();
    }
}
