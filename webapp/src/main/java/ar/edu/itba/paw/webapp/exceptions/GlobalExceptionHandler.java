package ar.edu.itba.paw.webapp.exceptions;

import ar.edu.itba.paw.webapp.controller.dto.SuccessDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<SuccessDto> handleNoSuchElementException(NoSuchElementException ex) {
        SuccessDto errorResponse = SuccessDto.exceptionToSuccessDto(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SuccessDto> handleException(Exception ex) {
        SuccessDto errorResponse = SuccessDto.exceptionToSuccessDto(ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}