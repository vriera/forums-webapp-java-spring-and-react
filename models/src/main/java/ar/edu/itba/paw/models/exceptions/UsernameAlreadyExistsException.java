package ar.edu.itba.paw.models.exceptions;

public class UsernameAlreadyExistsException extends AlreadyExistsException {
    public UsernameAlreadyExistsException() {
        super("username.already.exists");
    }
}
