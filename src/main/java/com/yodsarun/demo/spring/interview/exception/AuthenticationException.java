package com.yodsarun.demo.spring.interview.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends RuntimeException{
    private String message;
    private HttpStatus httpStatus;

    public AuthenticationException(String message) {
        super(message);
        this.message = message;
    }

    public AuthenticationException(String message,
                                   HttpStatus httpStatus) {
        super(message);
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }
}
