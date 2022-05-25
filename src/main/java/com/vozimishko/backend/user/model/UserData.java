package com.vozimishko.backend.user.model;

import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
public class UserData {

  private Long id;
  private String email;
  private String phoneNumber;
  private String firstName;
  private String lastName;


  public boolean isProfileComplete() {
    return StringUtils.isNotEmpty(firstName) && StringUtils.isNotEmpty(lastName);
  }
}
