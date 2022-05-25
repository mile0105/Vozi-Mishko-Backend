package com.vozimishko.backend.user.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@EqualsAndHashCode
@Builder(toBuilder = true)
@Table("users")
public class User {

  @Id
  private Long id;
  private String email;
  private String password;
  private String phoneNumber;
  private String firstName;
  private String lastName;
  private String role;

  public UserData toUserData() {
    return UserData.builder()
      .id(id)
      .email(email)
      .firstName(firstName)
      .lastName(lastName)
      .phoneNumber(phoneNumber)
      .build();
  }

  public UserDetails transformToUserDetails() {
    return UserDetails.builder()
      .email(email)
      .fullName(firstName + " " + lastName)
      .phoneNumber(phoneNumber)
      .build();

  }
}
