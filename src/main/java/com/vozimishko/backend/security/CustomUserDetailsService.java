package com.vozimishko.backend.security;

import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.repository.UserRepository;
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
      .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

    GrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

    return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
      Collections.singletonList(authority));

  }
}
