package ar.edu.itba.paw.interfaces.exceptions;

public class AlreadyCreatedException extends GenericBadRequestException{
    public AlreadyCreatedException(String message){
        super(message,"user.in.use");
    }
}
