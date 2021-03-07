package com.vozimishko.backend.error.exceptions;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public class UnauthorizedException extends ApiException {

  private final String message;

  @Override
  public HttpStatus getStatusCode() {
    return HttpStatus.UNAUTHORIZED;
  }
}
