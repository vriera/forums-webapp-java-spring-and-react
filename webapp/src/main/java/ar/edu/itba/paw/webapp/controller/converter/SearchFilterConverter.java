package ar.edu.itba.paw.webapp.controller.converter;

import ar.edu.itba.paw.models.AccessType;
import ar.edu.itba.paw.models.SearchFilter;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class SearchFilterConverter implements ParamConverter<SearchFilter> {

    @Override
    public SearchFilter fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null; // Or handle this as you see fit
        }
        try {
            return SearchFilter.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("invalid.search.filter");
        }
    }

    @Override
    public String toString(SearchFilter value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
