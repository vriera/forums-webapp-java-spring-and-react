package ar.edu.itba.paw.webapp.controller.dto;

import java.net.URI;

public class SuccessDto {

    private boolean success;



    private String url;


    private String message;

    public static SuccessDto boolToSuccessDto(Boolean bool , String message){
        SuccessDto s = new SuccessDto();
        s.success = bool;
        s.message = message;
        return s;
    }
    public static SuccessDto uriToSuccessDto(Boolean bool , String message , URI uri){
        SuccessDto s = new SuccessDto();
        s.success = bool;
        s.message = message;
        s.url = uri.toString();
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



    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}