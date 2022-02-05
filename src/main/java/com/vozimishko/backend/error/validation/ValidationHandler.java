package com.vozimishko.backend.error.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ValidationHandler {

  private final Logger logger = LoggerFactory.getLogger(ValidationHandler.class);

  @ExceptionHandler(value = MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    //todo add multi language support - with properties https://www.baeldung.com/spring-custom-validation-message-source
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    if (!errors.isEmpty()) {
      logger.info("Validation failed for: {}, errors: {}", ex.getObjectName(), errors);
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
  }
}
