package ar.edu.itba.paw.webapp.exceptionMappers;

import ar.edu.itba.paw.interfaces.exceptions.GenericOperationException;
import ar.edu.itba.paw.webapp.controller.utils.GenericResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Singleton
@Component
@Provider
public class GenericBadRequestExceptionHandler implements ExceptionMapper<GenericOperationException> {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenericBadRequestExceptionHandler.class);

    @Override
    public Response toResponse(GenericOperationException e) {
        LOGGER.error(e.getMessage());
        return GenericResponses.badRequest(e.getCode(), e.getMessage());
    }
}
