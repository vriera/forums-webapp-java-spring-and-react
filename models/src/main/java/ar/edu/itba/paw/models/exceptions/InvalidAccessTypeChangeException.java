package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.AccessType;

public class InvalidAccessTypeChangeException extends IllegalArgumentException{
    public InvalidAccessTypeChangeException(AccessType originAccessType, AccessType targetAccessType){
        super("The access type change from " + originAccessType.name() + " to " + targetAccessType.name() + " is invalid");
    }
}
