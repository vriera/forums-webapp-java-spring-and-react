package ar.edu.itba.paw.interfaces.Exceptions;

public class UserAlreadyCreatedException extends GenericBadRequestException{
    public UserAlreadyCreatedException(){
        super("user.in.use");
    }
}
