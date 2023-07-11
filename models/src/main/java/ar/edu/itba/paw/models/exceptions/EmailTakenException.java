package ar.edu.itba.paw.models.exceptions;

public class EmailTakenException extends Exception{
    public EmailTakenException() {
        super("The email is already taken");
    }
}
