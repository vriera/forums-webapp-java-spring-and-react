package ar.edu.itba.paw.webapp.dto;

public class SuccessDto {

    private boolean success;



    private String message;

    public static SuccessDto boolToSuccessDto(Boolean bool , String message){
        SuccessDto s = new SuccessDto();
        s.success = bool;
        s.message = message;
        return s;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
