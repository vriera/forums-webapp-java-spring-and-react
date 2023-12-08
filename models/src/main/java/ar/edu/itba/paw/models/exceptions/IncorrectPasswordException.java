package ar.edu.itba.paw.models.exceptions;

public class IncorrectPasswordException extends RuntimeException
{
    public IncorrectPasswordException()
    {
        super("incorrect.password");
    }

    public IncorrectPasswordException(String message)
    {
        super(message);
    }
}
