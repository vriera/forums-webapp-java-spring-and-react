package ar.edu.itba.paw.models;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class AccessTypeConverter implements AttributeConverter<AccessType, Integer> {

    @Override
    public Integer convertToDatabaseColumn(AccessType accessType) {
        return accessType.ordinal();
    }

    @Override
    public AccessType convertToEntityAttribute(Integer integer) {
        return AccessType.valueOf(integer);
    }
}
