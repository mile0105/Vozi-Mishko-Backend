package com.vozimishko.backend.security;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.security.jwt.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import static com.vozimishko.backend.security.jwt.JwtTokenAuthenticationFilter.ACCESS_TOKEN_PREFIX;
import static com.vozimishko.backend.security.jwt.JwtTokenAuthenticationFilter.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
public class PrincipalService {

  private final JwtUtils jwtUtils;

  public Long getLoggedInUserId() {


    String authorizationHeader = getCurrentHttpRequest().getHeader(AUTHORIZATION_HEADER);

    String token = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, "");

    return jwtUtils.getUserIdFromToken(token);
  }

  private HttpServletRequest getCurrentHttpRequest() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes instanceof ServletRequestAttributes) {
      return ((ServletRequestAttributes)requestAttributes).getRequest();
    }
    throw new BadRequestException("Invalid request");
  }


}
