package ar.edu.itba.paw.interfaces.exceptions;

public class UserAlreadyCreatedException extends GenericBadRequestException{
    public UserAlreadyCreatedException(){
        super("the user is already in use","user.in.use");
    }
}
