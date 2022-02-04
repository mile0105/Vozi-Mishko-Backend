package com.vozimishko.backend.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class LoginApi {
  @NotBlank(message = "Email is required")
  @Email(message = "Invalid e-mail")
  private String email;
  @NotBlank(message = "Password is required")
  private String password;
}
