package ar.edu.itba.paw.interfaces.exceptions;

public class BadParamsException extends GenericBadRequestException{
    public BadParamsException(String params){
        super("This list of params is wrong for some reason: " + params, "bad.params");
    }
}
