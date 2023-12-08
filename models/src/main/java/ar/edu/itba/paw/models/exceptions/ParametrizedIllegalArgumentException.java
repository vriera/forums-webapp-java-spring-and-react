package ar.edu.itba.paw.models.exceptions;

import java.util.Collections;
import java.util.List;

public abstract class ParametrizedIllegalArgumentException extends IllegalArgumentException{
    private List<String> parameters = Collections.emptyList();

    protected void setParameters(List<String> parameters){
        this.parameters = parameters;
    }
    ParametrizedIllegalArgumentException(String errorCode){
        super(errorCode);
    }
    public List<String> getParameters() {
        return parameters;
    }

}
