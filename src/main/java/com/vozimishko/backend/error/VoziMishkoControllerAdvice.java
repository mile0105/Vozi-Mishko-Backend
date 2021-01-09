package com.vozimishko.backend.error;

import com.vozimishko.backend.error.exceptions.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class VoziMishkoControllerAdvice {

  @ExceptionHandler(value = ApiException.class)
  public ResponseEntity<Object> onApiException(ApiException exception) {

    ErrorMessage message = new ErrorMessage(exception.getMessage());

    return new ResponseEntity<>(message, exception.getStatusCode());
  }
}
