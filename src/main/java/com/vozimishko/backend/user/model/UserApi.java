package com.vozimishko.backend.user.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserApi {
  private String email;
  private String password;
  private String phoneNumber;
  private String firstName;
  private String lastName;
}
