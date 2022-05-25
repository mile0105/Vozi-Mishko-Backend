package com.vozimishko.backend.user.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserRequestBody {

  private String firstName;
  private String lastName;
  private String email;
  private String phoneNumber;
  private String password;
}
