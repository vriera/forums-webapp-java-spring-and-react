package ar.edu.itba.paw.models.exceptions;

public class IllegalUsersSearchArgumentsException extends  IllegalArgumentException{

    public IllegalUsersSearchArgumentsException(){
        super("search.user.illegal.argument");
    }
}
