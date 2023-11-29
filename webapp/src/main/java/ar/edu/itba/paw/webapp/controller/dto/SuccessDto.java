package ar.edu.itba.paw.webapp.controller.dto;

import java.net.URI;

public class SuccessDto {

    private String url;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private String message;

    public static SuccessDto exceptionToSuccessDto(Exception e ){
        SuccessDto s = new SuccessDto();
        s.message = e.getMessage();
        return  s;
    }
    public static SuccessDto boolToSuccessDto(Boolean bool , String code , String message){
        SuccessDto s = new SuccessDto();
        s.code = code;
        s.message = message;
        return s;
    }
    public static SuccessDto boolToSuccessDto(Boolean bool , String message){
        SuccessDto s = new SuccessDto();
        s.code = message;
        return s;
    }
    public static SuccessDto uriToSuccessDto(Boolean bool , String message , URI uri){
        SuccessDto s = new SuccessDto();
        s.code = message;
        s.url = uri.toString();
        return s;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
