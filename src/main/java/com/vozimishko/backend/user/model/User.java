package com.vozimishko.backend.user.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;


@Getter
@Builder
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
}
