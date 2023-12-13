package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

@Component
@Provider
public class IOExceptionMapper implements ExceptionMapper<IOException> {
    @Context
    protected HttpServletRequest request;

    private final DtoGenerator dtoGenerator;
    @Autowired
    public IOExceptionMapper(DtoGenerator dtoGenerator){
        this.dtoGenerator = dtoGenerator;
    }
    @Override
    public Response toResponse(IOException e) {
        ErrorDto errorDto = dtoGenerator.messageToErrorDto("io.exception" , null, request.getLocale());
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(
                new GenericEntity<ErrorDto>(
                        errorDto) {
                }).build();
    }
}
