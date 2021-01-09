package com.vozimishko.backend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.vozimishko.backend.security.jwt.JwtTokenAuthenticationFilter.ACCESS_TOKEN_PREFIX;
import static com.vozimishko.backend.security.jwt.JwtTokenAuthenticationFilter.REFRESH_TOKEN_PREFIX;

@Component
@RequiredArgsConstructor
public class JwtUtils {

  @Value("${jwt.clientId:client}")
  private String clientId;

  @Value("${jwt.client-secret:secret}")
  private String clientSecret;

  @Value("${jwt.signing-key:IQ09J2BdDuc3lSKUJlQAp8uhCXRq+s2EucsBOb9rfjo=}")
  private String jwtSigningKey;

  @Value("${jwt.accessTokenValidititySeconds:43200}") // 12 hours
  private int accessTokenValiditySeconds;

  @Value("${jwt.authorizedGrantTypes:password,authorization_code,refresh_token}")
  private String[] authorizedGrantTypes;

  @Value("${jwt.refreshTokenValiditySeconds:2592000}") // 30 days
  private int refreshTokenValiditySeconds;

  public String getUsernameFromToken(String token) {
    Claims claims = getAllClaimsFromToken(token);

    return claims.getSubject();
  }

  public Long getUserIdFromToken(String token) {
    Claims claims = getAllClaimsFromToken(token);

    return claims.get("user_id", Long.class);
  }

  public String generateAccessToken(Authentication authentication, Long userId) {

    Instant now = Instant.now();

    return Jwts.builder()
      .setSubject(authentication.getName())
      .setHeaderParam("type", ACCESS_TOKEN_PREFIX)
      .claim("authorities", authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
      .claim("user_id", userId)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plusSeconds(accessTokenValiditySeconds)))
      .signWith(keygen())
      .compact();
  }

  public String generateRefreshToken(Authentication authentication, Long userId) {

    Instant now = Instant.now();

    return Jwts.builder()
      .setSubject(authentication.getName())
      .setHeaderParam("type", REFRESH_TOKEN_PREFIX)
      .claim("authorities", authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
      .claim("user_id", userId)
      .setIssuedAt(Date.from(now))
      .setExpiration(Date.from(now.plusSeconds(refreshTokenValiditySeconds)))
      .signWith(keygen())
      .compact();
  }

  public String generateAccessToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder()
      .setClaims(claims)
      .setHeaderParam("type", ACCESS_TOKEN_PREFIX)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(Date.from(Instant.now()))
      .setExpiration(Date.from(Instant.now().plusSeconds(accessTokenValiditySeconds)))
      .signWith(keygen())
      .compact();
  }


  private Claims getAllClaimsFromToken(String token) {
    return Jwts
      .parserBuilder()
      .setSigningKey(keygen())
      .build()
      .parseClaimsJws(token)
      .getBody();
  }

  public String generateRefreshToken(UserDetails userDetails) {
    Map<String, Object> claims = new HashMap<>();

    return Jwts.builder()
      .setClaims(claims)
      .setHeaderParam("type", REFRESH_TOKEN_PREFIX)
      .setSubject(userDetails.getUsername())
      .setIssuedAt(Date.from(Instant.now()))
      .setExpiration(Date.from(Instant.now().plusSeconds(refreshTokenValiditySeconds)))
      .signWith(keygen())
      .compact();
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    String username = getUsernameFromToken(token);

    return userDetails.getUsername().equals(username) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    Instant expiration = getExpirationDate(token);
    return expiration.isBefore(Instant.now());
  }


  private Key keygen() {
    return new SecretKeySpec(
      jwtSigningKey.getBytes(),
      SignatureAlgorithm.HS256.getJcaName()
    );
  }

  private Instant getExpirationDate(String token) {
    Claims claims = getAllClaimsFromToken(token);
    return claims.getExpiration().toInstant();
  }

  public String getTokenType(String token) {
    return (String)Jwts.parserBuilder().setSigningKey(keygen()).build().parseClaimsJws(token).getHeader().get("type");
  }
}
