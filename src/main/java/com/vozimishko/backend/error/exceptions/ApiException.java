package com.vozimishko.backend.error.exceptions;

import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.util.RequestUtils;
import com.vozimishko.backend.util.models.RequestLanguage;
import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

  public abstract HttpStatus getStatusCode();

  public abstract ErrorMessage getErrorMessage();

  public String getMessageForLogging() {
    return getErrorMessage().getFromLanguage(RequestLanguage.ENGLISH);
  }

  public String getMessage() {
    RequestLanguage requestLanguage = RequestUtils.getRequestLanguage();
    return getErrorMessage().getFromLanguage(requestLanguage);
  }

}

