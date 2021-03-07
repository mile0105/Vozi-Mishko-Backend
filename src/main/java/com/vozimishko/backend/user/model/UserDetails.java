package com.vozimishko.backend.user.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDetails {

  private String fullName;
  private String email;
  private String phoneNumber;
}
