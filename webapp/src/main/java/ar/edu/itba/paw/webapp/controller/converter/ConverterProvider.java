package ar.edu.itba.paw.webapp.controller.converter;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.SearchFilter;
import ar.edu.itba.paw.models.SearchOrder;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.Provider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

@Provider
public class ConverterProvider implements ParamConverterProvider {

    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType.equals(AccessType.class)) {
            return (ParamConverter<T>) new AccessTypeConverter();
        } else if (rawType.equals(SearchOrder.class)) {
            return (ParamConverter<T>) new SearchOrderConverter();
        } else if (rawType.equals(SearchFilter.class)) {
            return (ParamConverter<T>) new SearchFilterConverter();
        }
        return null;
    }
}