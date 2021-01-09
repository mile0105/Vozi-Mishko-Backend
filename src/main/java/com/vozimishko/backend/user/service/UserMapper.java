package com.vozimishko.backend.user.service;

import com.vozimishko.backend.user.model.Role;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.model.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final PasswordEncoder passwordEncoder;

  public User transformToDbModel(UserApi userApi) {

    return User.builder()
      .email(userApi.getEmail())
      .password(passwordEncoder.encode(userApi.getPassword()))
      .firstName(userApi.getFirstName())
      .lastName(userApi.getLastName())
      .phoneNumber(userApi.getPhoneNumber())
      .role(Role.NORMAL_USER.getName())
      .build();
  }
}
