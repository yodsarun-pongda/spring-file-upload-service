package com.yodsarun.demo.spring.interview.exception;

import com.yodsarun.demo.spring.interview.model.base.ResponseModel;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class AdviceHandler {
    public AdviceHandler() {}

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex) {
        String message = ex.getMessage();
        return ((ResponseEntity.BodyBuilder) ResponseEntity.status(ex.getHttpStatus() == null ? HttpStatus.CONFLICT : ex.getHttpStatus())).body(new ResponseModel(message));
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<?> handleBusinessException(BusinessException ex) {
        String message = ex.getMessage();
        return ((ResponseEntity.BodyBuilder) ResponseEntity.status(ex.getHttpStatus() == null ? HttpStatus.CONFLICT : ex.getHttpStatus())).body(new ResponseModel(message));
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<?> handleBusinessException(Exception ex) {
        return ((ResponseEntity.BodyBuilder) ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)).body(new ResponseModel(ExceptionUtils.getRootCauseMessage(ex)));
    }
}
