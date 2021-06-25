package com.vozimishko.backend.error.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorMessageResponse {

  private final String message;
}
