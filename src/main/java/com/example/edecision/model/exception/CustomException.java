package com.example.edecision.model.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class CustomException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private HttpStatus statusCode;

    public CustomException(String msg, HttpStatus statusCode) {
        super(msg);
        this.statusCode = statusCode;
    }

}
