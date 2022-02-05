package com.vozimishko.backend.error;

import com.vozimishko.backend.error.exceptions.ApiException;
import com.vozimishko.backend.error.model.ErrorMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class VoziMishkoControllerAdvice {

  private final Logger logger = LoggerFactory.getLogger(VoziMishkoControllerAdvice.class);

  @ExceptionHandler(value = ApiException.class)
  public ResponseEntity<Object> onApiException(ApiException exception) {

    String loggingMessage = "ApiException with status code: {} and message: {}";
    if (exception.getStatusCode().is5xxServerError()) {
      logger.error(loggingMessage, exception.getStatusCode().value(), exception.getMessageForLogging());
    } else {
      logger.info(loggingMessage, exception.getStatusCode().value(), exception.getMessageForLogging());
    }

    ErrorMessageResponse message = new ErrorMessageResponse(exception.getMessage());

    return new ResponseEntity<>(message, exception.getStatusCode());
  }
}
