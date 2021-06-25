package com.vozimishko.backend.security;

import com.vozimishko.backend.security.jwt.JwtUtils;
import com.vozimishko.backend.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.vozimishko.backend.security.jwt.JwtTokenAuthenticationFilter.ACCESS_TOKEN_PREFIX;
import static com.vozimishko.backend.security.jwt.JwtTokenAuthenticationFilter.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
public class PrincipalService {

  private final JwtUtils jwtUtils;

  public Long getLoggedInUserId() {

    String authorizationHeader = RequestUtils.getCurrentHttpRequest().getHeader(AUTHORIZATION_HEADER);
    String token = authorizationHeader.replace(ACCESS_TOKEN_PREFIX, "");

    return jwtUtils.getUserIdFromToken(token);
  }

}
