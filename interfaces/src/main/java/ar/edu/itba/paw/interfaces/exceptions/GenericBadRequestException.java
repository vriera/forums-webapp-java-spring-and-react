package ar.edu.itba.paw.interfaces.exceptions;

public class GenericBadRequestException extends GenericException{
    public GenericBadRequestException(String message, String code) {
        super(message,code);
    }
}
