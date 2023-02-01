package ar.edu.itba.paw.interfaces.services.exceptions;

public class CantAccess extends Exception{
    public CantAccess(String errorMessage) {
        super(errorMessage);
    }
}
