package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

public class GenericExceptionHandler implements ExceptionMapper<Exception> {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericNotFoundExceptionHandler.class);

    @Override
    public Response toResponse(Exception e) {
        LOGGER.error(e.getMessage());
        return GenericResponses.serverError();
    }
}
