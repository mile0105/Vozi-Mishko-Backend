package com.vozimishko.backend.car.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarApi {

  private String manufacturerName;
  private String modelName;
  private int numberOfSeats;
}
