package ar.edu.itba.paw.interfaces.exceptions;

public class AlreadyCreatedException extends GenericOperationException {
    public AlreadyCreatedException(String message) {
        super(message, "IN.USE");
    }
}
