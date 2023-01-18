package com.example.edecision.controller.exception;

import com.example.edecision.model.exception.CustomException;
import com.example.edecision.model.exception.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ControllerAdvisor extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorMessage> customException(CustomException ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(
                ex.getStatusCode().value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.valueOf(errorMessage.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> globalExceptionHandler(Exception ex, WebRequest request) {
        ErrorMessage errorMessage = new ErrorMessage(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                new Date(),
                ex.getMessage(),
                request.getDescription(false));

        return new ResponseEntity<ErrorMessage>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
