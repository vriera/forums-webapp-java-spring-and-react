package ar.edu.itba.paw.models.exceptions;

import ar.edu.itba.paw.models.AccessType;

public class IllegalCommunitySearchArgumentsException extends IllegalArgumentException{
    public IllegalCommunitySearchArgumentsException(){
        super("search.community.illegal.argument");
    }
}
