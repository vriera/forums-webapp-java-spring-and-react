package ar.edu.itba.paw.webapp.controller.converter;

import ar.edu.itba.paw.models.SearchFilter;
import ar.edu.itba.paw.models.SearchOrder;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ext.ParamConverter;

public class SearchOrderConverter implements ParamConverter<SearchOrder> {


    @Override
    public SearchOrder fromString(String value) {
        if (value == null || value.isEmpty()) {
            return null; // Or handle this as you see fit
        }
        try {
            return SearchOrder.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            //TODO: change into a "code"
            throw new BadRequestException("invalid.search.order");
        }
    }

    @Override
    public String toString(SearchOrder value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

}
