package ar.edu.itba.paw.models.exceptions;

public abstract class AlreadyExistsException extends RuntimeException{

    AlreadyExistsException(String message) {
        super(message);
    }
}
