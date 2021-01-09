package com.vozimishko.backend.security;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorsFilter extends GenericFilterBean {
  private final String DEFAULT_ALLOWED_ORIGIN = "*";
  private List<String> allowedOrigins = Arrays.asList(DEFAULT_ALLOWED_ORIGIN, "http://localhost:19006", "exp://153.19.216.71:19000");

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletResponse response = (HttpServletResponse)res;
    HttpServletRequest request = (HttpServletRequest)req;
    String origin = request.getHeader("Origin");
    String allowedOriginResponse = (allowedOrigins.contains(origin))? origin : DEFAULT_ALLOWED_ORIGIN;
    response.setHeader("Access-Control-Allow-Origin", "*");
    response.setHeader("Access-Control-Allow-Methods", "POST, PUT, GET, OPTIONS, DELETE, PATCH");
    response.setHeader("Access-Control-Max-Age", "3600");
    response.setHeader("Access-Control-Allow-Headers", "Authorization");
    response.setHeader("Access-Control-Allow-Credentials", "true");
    response.setHeader("P3P", "CP=\"ALL IND DSP COR ADM CONo CUR CUSo IVAo IVDo PSA" +
      " PSD TAI TELo OUR SAMo CNT COM INT NAV ONL PHY PRE PUR UNI\"");
    List<String> allowedHeaders = Arrays.asList(
      "x-requested-with",
      "x-statistics-identifier",
      HttpHeaders.CONTENT_TYPE,
      HttpHeaders.AUTHORIZATION,
      HttpHeaders.USER_AGENT,
      HttpHeaders.ORIGIN,
      HttpHeaders.ACCEPT
    );
    response.setHeader("Access-Control-Allow-Headers", String.join(",", allowedHeaders));
    if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
      response.setStatus(HttpServletResponse.SC_OK);
    } else {
      chain.doFilter(req, res);
    }
  }
}
