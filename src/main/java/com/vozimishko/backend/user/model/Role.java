package com.vozimishko.backend.user.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

  NORMAL_USER("NORMAL_USER");

  private final String name;
}
