package com.vozimishko.backend.error.exceptions;

import com.vozimishko.backend.error.model.ErrorMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public class BadRequestException extends ApiException {

  private final ErrorMessage errorMessage;

  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.BAD_REQUEST;
  }
}
