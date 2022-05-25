package com.vozimishko.backend.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestBody {

  //todo add custom validator for phone number - https://www.baeldung.com/spring-mvc-custom-validator

  private Long id;
  @NotBlank(message = "E-mail is mandatory")
  @Email(message = "Invalid e-mail")
  private String email;
  @NotBlank(message = "Password is mandatory")
  private String password;
  @NotBlank(message = "Phone number is mandatory")
  private String phoneNumber;

  public User transformToDbModel(PasswordEncoder passwordEncoder) {

    return User.builder()
      .email(email)
      .password(passwordEncoder.encode(password))
      .phoneNumber(phoneNumber)
      .role(Role.NORMAL_USER.getName())
      .build();
  }
}
