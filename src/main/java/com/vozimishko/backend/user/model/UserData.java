package com.vozimishko.backend.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserData {

  private Long id;
  private String email;
  private String phoneNumber;
  private String firstName;
  private String lastName;

}
