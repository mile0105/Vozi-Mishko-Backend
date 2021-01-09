package com.vozimishko.backend.error.exceptions;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

  public abstract HttpStatus getStatusCode();

  public abstract String getMessage();

}

