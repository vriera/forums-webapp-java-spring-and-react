package ar.edu.itba.paw.models.exceptions;

public class UsernameTakenException extends Exception {
    public UsernameTakenException() {
        super("The username already taken");
    }

}
