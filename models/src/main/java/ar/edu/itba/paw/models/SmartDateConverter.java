package ar.edu.itba.paw.models;

import javax.persistence.Converter;
import javax.persistence.AttributeConverter;
import java.sql.Timestamp;

@Converter
public class SmartDateConverter implements AttributeConverter<SmartDate, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(SmartDate smartDate) {
        return smartDate.getTime();
    }

    @Override
    public SmartDate convertToEntityAttribute(Timestamp timestamp) {
        return new SmartDate(timestamp);
    }
}
