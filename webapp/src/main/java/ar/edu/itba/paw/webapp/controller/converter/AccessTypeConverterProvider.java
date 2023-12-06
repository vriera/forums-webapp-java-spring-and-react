package ar.edu.itba.paw.webapp.controller.converter;

import ar.edu.itba.paw.models.AccessType;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class AccessTypeConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(AccessType.class)) {
            return (ParamConverter<T>) new AccessTypeConverter();
        }
        return null;
    }
}