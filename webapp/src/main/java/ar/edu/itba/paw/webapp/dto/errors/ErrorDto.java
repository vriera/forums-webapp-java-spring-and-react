package ar.edu.itba.paw.webapp.dto.errors;

public class ErrorDto {
//    public static ErrorDto uriToErrorDto(Boolean bool , String message , URI uri){
//        ErrorDto s = new ErrorDto();
//        s.code = message;
//        s.url = uri.toString();
//        return s;
//    }
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    private String url;


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

    public static ErrorDto exceptionToErrorDto(Exception e ){
        ErrorDto s = new ErrorDto();
        s.message = e.getMessage();
        return  s;
    }
    public static ErrorDto boolToErrorDto(Boolean bool , String code , String message){
        ErrorDto s = new ErrorDto();
        s.code = code;
        s.message = message;
        return s;
    }
    public static ErrorDto boolToErrorDto(Boolean bool , String message){
        ErrorDto s = new ErrorDto();
        s.code = message;
        return s;
    }


}
