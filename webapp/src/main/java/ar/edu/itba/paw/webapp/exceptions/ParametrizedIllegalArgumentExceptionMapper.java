package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.models.exceptions.ParametrizedIllegalArgumentException;
import ar.edu.itba.paw.webapp.dto.errors.ErrorDto;
import ar.edu.itba.paw.webapp.exceptions.utils.DtoGenerator;
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
public class ParametrizedIllegalArgumentExceptionMapper implements ExceptionMapper<ParametrizedIllegalArgumentException> {
    @Context
    protected HttpServletRequest request;

    private final DtoGenerator dtoGenerator;

    @Autowired
    public ParametrizedIllegalArgumentExceptionMapper(DtoGenerator dtoGenerator){
        this.dtoGenerator = dtoGenerator;
    }

    @Override
    public Response toResponse(ParametrizedIllegalArgumentException e) {
        ErrorDto errorDto = dtoGenerator.messageToErrorDto(e.getMessage() , e.getParameters() , request.getLocale());
        return Response.status(Response.Status.BAD_REQUEST).entity(
                new GenericEntity<ErrorDto>(errorDto) {
                }).build();
    }
}