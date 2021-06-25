package com.vozimishko.backend.error.exceptions;


import com.vozimishko.backend.error.model.ErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class InternalServerErrorException extends ApiException {

  private final ErrorMessage errorMessage;

  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
