package com.buildmate.config;

import com.buildmate.service.MailDeliveryException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(MailDeliveryException.class)
  public ResponseEntity<Map<String,String>> mailDelivery(MailDeliveryException ex) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message",ex.getMessage()));
  }
}
