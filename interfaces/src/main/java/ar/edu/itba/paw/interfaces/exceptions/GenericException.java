package ar.edu.itba.paw.interfaces.exceptions;

public class GenericException extends Exception{

    private String code;
    private String message;

    public GenericException(String message, String code) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
