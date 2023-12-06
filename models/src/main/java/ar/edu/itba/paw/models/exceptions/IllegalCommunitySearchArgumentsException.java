package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.AccessType;

public class IllegalCommunitySearchArgumentsException extends IllegalArgumentException{
    public IllegalCommunitySearchArgumentsException(){
        super("Arguments must be only one or none from: query, (accessType + userId) or moderatorId");
    }
}
