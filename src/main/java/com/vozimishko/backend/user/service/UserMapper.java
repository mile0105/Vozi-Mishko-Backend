package com.vozimishko.backend.user.service;

import com.vozimishko.backend.user.model.RegisterRequestBody;
import com.vozimishko.backend.user.model.Role;
import com.vozimishko.backend.user.model.User;
import com.vozimishko.backend.user.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper {

  private final PasswordEncoder passwordEncoder;

  public User transformToDbModel(RegisterRequestBody registerRequestBody) {

    return User.builder()
      .email(registerRequestBody.getEmail())
      .password(passwordEncoder.encode(registerRequestBody.getPassword()))
      .firstName(registerRequestBody.getFirstName())
      .lastName(registerRequestBody.getLastName())
      .phoneNumber(registerRequestBody.getPhoneNumber())
      .role(Role.NORMAL_USER.getName())
      .build();
  }

  public RegisterRequestBody transformFromDbModel(User user) {
    return RegisterRequestBody.builder()
      .email(user.getEmail())
      .phoneNumber(user.getPhoneNumber())
      .firstName(user.getFirstName())
      .lastName(user.getLastName())
      .id(user.getId())
      .build();
  }

  public UserDetails transformToUserDetails(User user) {
    return UserDetails.builder()
      .email(user.getEmail())
      .fullName(user.getFirstName() + " " + user.getLastName())
      .phoneNumber(user.getPhoneNumber())
      .build();
  }
}
