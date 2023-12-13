package ar.edu.itba.paw.models.exceptions;

public class IllegalUsersSearchArgumentsException extends ParametrizedIllegalArgumentException {

    public IllegalUsersSearchArgumentsException() {
        super("search.user.illegal.argument");
    }
}
