package com.vozimishko.backend.security;

import com.vozimishko.backend.util.models.RequestLanguage;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.repository.UserRepository;
import com.vozimishko.backend.util.RequestUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

    User user = userRepository.findByEmail(email)
      .orElseThrow(() -> new UsernameNotFoundException(getErrorMessage()));

    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
      Collections.singletonList(authority));

  }

  private String getErrorMessage() {
    RequestLanguage requestLanguage = RequestUtils.getRequestLanguage();
    switch (requestLanguage) {
      case ENGLISH: return "Invalid credentials";
      case MACEDONIAN: return "Невалидни креденцијали";
      case ALBANIAN: return "";
      default: return null;
    }
  }
}
