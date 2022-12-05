package ar.edu.itba.paw.webapp.controller.dto.errors;


public enum ErrorCode {
    USER_NOT_FOUND("user.not.found", "The username has not been found"),
    INVALID_PASSWORD("invalid.password", "Password is invalid."),
    INVALID_JSON_FIELD("JSON.fields.incomplete", "At least one of the JSON fields provided were invalid or not provided.");

    private final String code;
    private final String message;

    ErrorCode(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return this.code;
    }

    public String getMessage() {
        return message;
    }
}
