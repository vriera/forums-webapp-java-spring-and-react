package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.AccessType;

import java.util.ArrayList;
import java.util.List;

public class InvalidAccessTypeChangeException extends ParametrizedIllegalArgumentException {
    private static final String ERROR_CODE = "invalid.access.type.change";


    public InvalidAccessTypeChangeException(AccessType originAccessType, AccessType targetAccessType) {
        super(ERROR_CODE );
        List<String> parameters = new ArrayList<>();
        parameters.add(originAccessType.name());
        parameters.add(targetAccessType.name());
        this.setParameters(parameters);
    }


}