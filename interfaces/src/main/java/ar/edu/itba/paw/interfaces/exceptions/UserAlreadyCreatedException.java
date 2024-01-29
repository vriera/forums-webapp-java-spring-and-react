package ar.edu.itba.paw.interfaces.exceptions;

public class UserAlreadyCreatedException extends GenericBadRequestException{
    public UserAlreadyCreatedException(){
        super("user.in.use");
    }
}
