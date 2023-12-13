package ar.edu.itba.paw.models.exceptions;

public class IncorrectPasswordException extends ParametrizedIllegalArgumentException {
    public IncorrectPasswordException() {
        super("incorrect.password");
    }

}
