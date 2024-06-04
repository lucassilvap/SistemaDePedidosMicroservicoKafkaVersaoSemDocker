package com.example.servicodepagamento.kafka.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionGlobalHandler  {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleException(ValidationException e){
           var details = new ExceptionDetail(HttpStatus.BAD_REQUEST.value(), e.getMessage());
           return new ResponseEntity<>(details, HttpStatus.BAD_REQUEST);
    }
}
