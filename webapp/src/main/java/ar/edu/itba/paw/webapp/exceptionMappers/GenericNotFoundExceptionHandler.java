package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.interfaces.exceptions.GenericNotFoundException;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GenericNotFoundExceptionHandler implements ExceptionMapper<GenericNotFoundException> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericBadRequestExceptionHandler.class);
    @Override
    public Response toResponse(GenericNotFoundException e) {
        LOGGER.error(e.getMessage());
        return GenericResponses.notFoundMessage(e.getMessage());
    }
}
