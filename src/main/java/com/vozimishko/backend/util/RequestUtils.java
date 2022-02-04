package com.vozimishko.backend.util;


import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.util.models.RequestLanguage;
import lombok.experimental.UtilityClass;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@UtilityClass
public final class RequestUtils {

  private final String LANG_PARAM = "lang";

  public HttpServletRequest getCurrentHttpRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return ((ServletRequestAttributes)requestAttributes).getRequest();
    }
    throw new BadRequestException(ErrorMessage.INVALID_REQUEST);
  }

  public RequestLanguage getRequestLanguage() {
    String language = getCurrentHttpRequest().getParameter(LANG_PARAM);
    return RequestLanguage.findFromLanguageText(language);
  }

}
