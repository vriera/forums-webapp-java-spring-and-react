package ar.edu.itba.paw.models.exceptions;

public class EmailAlreadyExistsException extends AlreadyExistsException{
    public EmailAlreadyExistsException() {
        super("Email already exists");
    }
}
