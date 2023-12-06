package ar.edu.itba.paw.models.exceptions;

public class IllegalUsersSearchArgumentsException extends  IllegalArgumentException{

    public IllegalUsersSearchArgumentsException(){
        super("Arguments must be only one or none from: query, (accessType + communityId) or email");
    }
}
