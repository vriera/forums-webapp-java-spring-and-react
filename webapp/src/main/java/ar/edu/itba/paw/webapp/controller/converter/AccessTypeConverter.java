package ar.edu.itba.paw.webapp.controller.converter;

import ar.edu.itba.paw.models.AccessType;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class AccessTypeConverter implements ParamConverter<AccessType> {

    @Override
    public AccessType fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null; // Or handle this as you see fit
        }
        try {
            return AccessType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid access type");
        }
    }

    @Override
    public String toString(AccessType value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
