package ar.edu.itba.paw.models;

import javax.persistence.Converter;
import javax.persistence.AttributeConverter;

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
