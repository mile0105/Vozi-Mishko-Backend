package com.vozimishko.backend.error;

import com.vozimishko.backend.error.exceptions.ApiException;
import com.vozimishko.backend.error.model.ErrorMessageResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class VoziMishkoControllerAdvice {

  @ExceptionHandler(value = ApiException.class)
  public ResponseEntity<Object> onApiException(ApiException exception) {

    ErrorMessageResponse message = new ErrorMessageResponse(exception.getMessage());

    return new ResponseEntity<>(message, exception.getStatusCode());
  }

//  @ExceptionHandler(value = Exception.class)
//  public ResponseEntity<Object> onInternalException(Exception exception) {
//
//    exception.printStackTrace();
//
//    return new ResponseEntity<>(message, exception.getStatusCode());
//    throw new InternalServerErrorException(ErrorMessage.SOMETHING_WENT_WRONG);
//  }
}
