package ar.edu.itba.paw.models.exceptions;

public class UsernameAlreadyExistsException extends AlreadyExistsException {
    public UsernameAlreadyExistsException() {
        super("Username already exists");
    }

}
