package ar.edu.itba.paw.models.exceptions;

public class IncorrectPasswordException extends RuntimeException
{
    public IncorrectPasswordException()
    {
        super("The password is incorrect");
    }

    public IncorrectPasswordException(String message)
    {
        super(message);
    }
}
