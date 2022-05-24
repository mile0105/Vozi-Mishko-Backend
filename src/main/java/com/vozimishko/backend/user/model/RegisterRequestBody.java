package com.vozimishko.backend.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
