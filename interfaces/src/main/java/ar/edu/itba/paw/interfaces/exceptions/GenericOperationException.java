package ar.edu.itba.paw.interfaces.exceptions;

public class GenericOperationException extends GenericException{
    public GenericOperationException(String message, String code) {
        super(message,code);
    }
}
